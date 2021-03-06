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

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

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
public class MessageProtocolCodecFactory implements org.apache.mina.filter.codec.ProtocolCodecFactory
{
    private ProtocolEncoder encoder;

    private ProtocolDecoder decoder;

    public MessageProtocolCodecFactory()
    {
        encoder = new MessageEncoder();
        decoder = new MessageDecoder();
    }

    public ProtocolEncoder getEncoder(IoSession session) throws Exception
    {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) throws Exception
    {
        return decoder;
    }
}