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
package com.vake.message.request;

import java.util.Set;

import com.vake.message.Message;
import org.apache.mina.filter.reqres.ResponseInspector;
import org.apache.mina.filter.reqres.ResponseType;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check data whether the response<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author xiongqimin
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2015-01-28 xiongqimin    created<br/>
 * <p/>
 */
public class MessageResponseInspector implements ResponseInspector
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageResponseInspector.class);

    private final Set requestIdSet = new ConcurrentHashSet<Object>();

    public void addRequestId(Object requestId)
    {
        final boolean existed = requestIdSet.add(requestId);
        if (!existed)
        {
            LOGGER.warn("requestId={} is existed", requestId);
        }
    }

    public void removeRequestId(Object requestId)
    {
        final boolean existed = requestIdSet.remove(requestId);
        if (!existed)
        {
            LOGGER.warn("requestId={} is not existed", requestId);
        }
    }

    public Object getRequestId(Object data)
    {
        if (data instanceof Message)
        {
            final Message property = (Message) data;
            // 
            final int sessionId = property.getSessionId();
            final String requestId = MessageRequest.generateKey(sessionId, property.getSerial());
            return requestIdSet.contains(requestId) ? requestId : null;
        }
        return null;
    }

    /**
     * 仅支持包不大的情况，一个包包含一整条完整的消息
     * @param message
     * @return
     */
    public ResponseType getResponseType(Object message)
    {
        return ResponseType.WHOLE;
    }


}