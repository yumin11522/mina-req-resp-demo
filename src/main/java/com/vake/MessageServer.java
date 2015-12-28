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

import com.vake.message.Message;
import com.vake.message.codec.MessageProtocolCodecFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * TODO Add class comment here<p/>
 *
 * @author xiongqimin
 * @version 1.0.0
 * @history<br/> ver    date       author desc
 * 1.0.0  2015/12/28 xiongqimin created<br/>
 * <p/>
 * @since 1.0.0
 */
public class MessageServer extends IoHandlerAdapter
{
    private static Logger LOGGER = LoggerFactory.getLogger(MessageServer.class);

    public static final int PORT = 4999;

    public static void main(String[] args)
    {
        final NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MessageProtocolCodecFactory()));
        acceptor.getFilterChain().addLast("exec", new ExecutorFilter(1));

        // Bind
        acceptor.setHandler(new IoHandlerAdapter()
        {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception
            {
                LOGGER.debug("received a message from:{}, content:{}", session, message);
                Message msg = (Message) message;
                final Message copy = Message.copy(msg);
                copy.setContent("server reply " + copy.getContent());
                session.write(msg);
            }
        });

        final SocketSessionConfig scfg = acceptor.getSessionConfig();
        scfg.setReuseAddress(true);
        scfg.setReadBufferSize(1024 * 10);
        scfg.setSendBufferSize(1024 * 10);
        try
        {
            acceptor.bind(new InetSocketAddress(PORT));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        System.out.println("Listening on port " + PORT);
    }
}
