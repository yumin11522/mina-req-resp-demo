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

import java.net.SocketAddress;

import com.vake.ArrayUtils;
import com.vake.message.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trace消息解码器<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author xiongqimin
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2014-12-15 xiongqimin    created<br/>
 * <p/>
 */
public class MessageDecoder extends ProtocolDecoderAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
    {
        final int limit = in.limit();
        byte[] bytes = new byte[limit];
        in.get(bytes);
        in.position(limit);
        final SocketAddress remoteAddress = session.getRemoteAddress();
        LOGGER.info("receive data from {}, bytes={}", remoteAddress, ArrayUtils.toHexString(bytes));

        if (Message.isValid(bytes))
        {
            final Message message = Message.from(bytes);
            out.write(message);
        } else
        {
            LOGGER.info("can't parse , invalid bytes, bytes={}", ArrayUtils.toHexString(bytes));
        }
    }
}