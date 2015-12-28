/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.vake;

import com.vake.message.DeviceResponseTimeout;
import com.vake.message.Message;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.vake.message.codec.MessageProtocolCodecFactory;
import com.vake.message.request.MessageRequest;
import com.vake.message.request.MessageResponseInspector;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.reqres.RequestResponseFilter;
import org.apache.mina.filter.reqres.RequestTimeoutException;
import org.apache.mina.filter.reqres.Response;
import org.apache.mina.filter.statistic.ProfilerTimerFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageClient<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author xiongqimin
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2015-01-28 xiongqimin    created<br/>
 * <p/>
 */
public class MessageClient implements IoHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClient.class);

    // 设置UDP读取缓冲池大小
    public static final int READ_BUFFER_SIZE = 1024 * 1024;//1M


    private final NioDatagramConnector connector;

    private final ScheduledExecutorService scheduleExecutor;

    private IoSession session;


    private final MessageResponseInspector responseInspector;

    private ProfilerTimerFilter profiler;

    /**
     * Constructor
     *
     * @param scheduleExecutor check request time out schedule executor
     */
    public MessageClient(ScheduledExecutorService scheduleExecutor, String ip, int port)
    {
        connector = new NioDatagramConnector();
        this.scheduleExecutor = scheduleExecutor;
        responseInspector = new MessageResponseInspector();
        final DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
        filterChain.addLast("logging", new LoggingFilter(MessageClient.class));
        // 消息解码过滤器
        final MessageProtocolCodecFactory codec = new MessageProtocolCodecFactory();
        filterChain.addLast("codec", new ProtocolCodecFilter(codec));

        // thread pool
        filterChain.addLast("threadPool", new ExecutorFilter(IoEventType.MESSAGE_RECEIVED));

        // profile
        profiler = new ProfilerTimerFilter(TimeUnit.MILLISECONDS, IoEventType.MESSAGE_RECEIVED);
        filterChain.addLast("profiler", profiler);

        // request and response filter
        final RequestResponseFilter reqRspFilter = new RequestResponseFilter(responseInspector, scheduleExecutor);
        filterChain.addLast("requestResponse", reqRspFilter);

        final DatagramSessionConfig sessionConfig = connector.getSessionConfig();
        sessionConfig.setReceiveBufferSize(READ_BUFFER_SIZE);
        sessionConfig.setReuseAddress(true);

        // add chained io handler
        connector.setHandler(this);

        final ConnectFuture connect = connector.connect(new InetSocketAddress("10.8.9.60", port), new InetSocketAddress(port));
        connect.awaitUninterruptibly();
        session = connect.getSession();
    }

    public void destory()
    {
        scheduleExecutor.shutdown();
        connector.dispose();
    }

    public void sendMessage(Message msg)
    {
        if (session != null && session.isConnected())
        {
            session.write(msg);
        }
    }

    public Message execute(int sessionId, Message requestMsg) throws DeviceResponseTimeout
    {
        if (null != connector && connector.isActive())
        {
            final MessageRequest request = MessageRequest.createRequest(sessionId, requestMsg);
            if (session != null && session.isConnected())
            {
                responseInspector.addRequestId(request.getId());
                session.write(request);
            }
            Response response = null;
            try
            {
                response = request.awaitResponse(MessageRequest.DEFAULT_TIME_OUT * 2, TimeUnit.MILLISECONDS);
            } catch (RequestTimeoutException ex)
            {
                LOGGER.error("request is time out, request={}", request);
                LOGGER.error("", ex);
                throw new DeviceResponseTimeout(ex.getMessage());
            } catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
                LOGGER.error("while request wait response, thread interrupted, request={}", request);
                LOGGER.error("", ex);
                throw new DeviceResponseTimeout(ex.getMessage());
            } finally
            {
                responseInspector.removeRequestId(request.getId());
            }
            if (null != response)
            {
                final Message data = (Message) response.getMessage();
                return data;
            }
        } else
        {
            LOGGER.error("connector is unavaliable!");
        }
        return null;
    }

    public void profile(Logger logger)
    {
        logger = logger == null ? LOGGER : logger;
        final Set<IoEventType> events = profiler.getEventsToProfile();
        for (IoEventType eventType : events)
        {
            logger.debug("call eventType {}, average time is: {} milliseconds", eventType.name(), profiler.getAverageTime(eventType));
            logger.debug("call eventType {}, maxinum time is: {} milliseconds", eventType.name(), profiler.getMaximumTime(eventType));
            logger.debug("call eventType {}, total calls is: {}", eventType.name(), profiler.getTotalCalls(eventType));
            logger.debug("call eventType {}, total times is: {} milliseconds", eventType.name(), profiler.getTotalTime(eventType));
        }
    }

    public static void main(String[] args) throws Exception
    {
        final MessageClient messageClient = new MessageClient(Executors.newScheduledThreadPool(1), "10.8.9.194", MessageServer.PORT);
        System.out.println("begin....");
        int sessionId = 10000;
        int serial = 1;
        while (serial < 10000)
        {
            final Message requestMsg = new Message();
            requestMsg.setSerial(serial);
            requestMsg.setSessionId(sessionId);
            final String msg = "test" + serial;
            requestMsg.setContentLength(msg.getBytes().length);
            requestMsg.setContent(msg);
            final Message execute = messageClient.execute(sessionId, requestMsg);
            System.out.println(execute);
            serial++;
        }
        messageClient.destory();
    }

    public void sessionCreated(IoSession session) throws Exception
    {
        LOGGER.debug("session:{} is created", session);
    }

    public void sessionOpened(IoSession session) throws Exception
    {
        LOGGER.debug("session:{} is opened", session);
    }

    public void sessionClosed(IoSession session) throws Exception
    {
        LOGGER.debug("session:{} is closed", session);
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception
    {
        LOGGER.debug("session:{} is idle", session);
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception
    {
        LOGGER.error("process session:{} failed, cause:{}", session, cause);
    }

    public void messageReceived(IoSession session, Object message) throws Exception
    {
        LOGGER.debug("receive a msg from:{}, msg:{}", session, message);
    }

    public void messageSent(IoSession session, Object message) throws Exception
    {
        LOGGER.debug("send msg to:{}, msg:{}", session, message);
    }
}