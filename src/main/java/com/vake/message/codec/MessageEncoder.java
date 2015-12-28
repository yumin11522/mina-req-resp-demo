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
package com.vake.message.codec;

import java.io.NotSerializableException;
import java.net.SocketAddress;

import com.vake.ArrayUtils;
import com.vake.message.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.reqres.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add class comment here<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author xiongqimin
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2014-12-15 xiongqimin    created<br/>
 * <p/>
 */
public class MessageEncoder extends ProtocolEncoderAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEncoder.class);

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
    {
        if (message instanceof Message)
        {
            final Message msg = (Message) message;
            sendData(msg, session);
        } else if (message instanceof Request)
        {
            final Request req = (Request) message;
            final Message data = (Message) req.getMessage();
            sendData(data, session);
        } else
        {
            throw new NotSerializableException();
        }
    }

    private void sendData(Message msg, IoSession session)
    {
        final byte[] encodeToBytes = msg.encodeToBytes();
        final SocketAddress remoteAddress = session.getRemoteAddress();
        LOGGER.debug("send data to {}, bytes={}", remoteAddress, ArrayUtils.toHexString(encodeToBytes));
        final IoBuffer wrap = IoBuffer.wrap(encodeToBytes);
        session.write(wrap);
    }
}