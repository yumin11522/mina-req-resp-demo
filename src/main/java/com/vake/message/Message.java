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
package com.vake.message;

import com.vake.ArrayUtils;
import com.vake.NumberUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * 定义消息结构<p/>
 *
 * @author xiongqimin
 * @version 1.0.0
 * @history<br/> ver    date       author desc
 * 1.0.0  2015/12/25 xiongqimin created<br/>
 * <p/>
 * @since 1.0.0
 */
public class Message
{
    private static Logger LOGGER = LoggerFactory.getLogger(Message.class);

    // sessionId + serial + contentLength的长度
    public static final int HEAD_LENGTH = 8;

    // 客户端唯一标识
    private int sessionId;

    // 序列号,与发送的serial一致
    private int serial;

    // 内容长度
    private int contentLength;

    // 消息内容
    private String content;

    public int getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(int sessionId)
    {
        this.sessionId = sessionId;
    }

    public int getSerial()
    {
        return serial;
    }

    public void setSerial(int serial)
    {
        this.serial = serial;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getContentLength()
    {
        return contentLength;
    }

    public void setContentLength(int contentLength)
    {
        this.contentLength = contentLength;
    }

    public byte[] encodeToBytes()
    {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bytes = null;
        try
        {
            stream.write(NumberUtils.intToBytes(sessionId));
            stream.write(NumberUtils.intToBytes(serial));
            stream.write(NumberUtils.intToBytes(contentLength));

            final byte[] contentBytes = content.getBytes();
            stream.write(contentBytes);
            final int length = contentBytes.length;
            if (length <= contentLength)
            {
                stream.write(new byte[contentLength - length]);
            }
            stream.flush();
            bytes = stream.toByteArray();
        } catch (Exception ex)
        {
            LOGGER.error("convert value to byte[] error", ex);
            return null;
        } finally
        {
            try
            {
                stream.close();
            } catch (IOException e)
            {
                LOGGER.error("error raised", e);
            }
        }
        return bytes;
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "sessionId=" + sessionId +
                ", serial=" + serial +
                ", contentLength=" + contentLength +
                ", content='" + content + '\'' +
                '}';
    }

    public static boolean isValid(byte[] data)
    {
        return data.length >= Message.HEAD_LENGTH;
    }

    public static Message from(byte[] data)
    {
        Message msg = new Message();
        int position = 0;
        byte[] temp = new byte[4];
        System.arraycopy(data, position, temp, 0, 4);
        msg.sessionId = ArrayUtils.bytesToInt(temp);
        position += 4;

        temp = new byte[4];
        System.arraycopy(data, position, temp, 0, 4);
        msg.serial = ArrayUtils.bytesToInt(temp);
        position += 4;

        temp = new byte[4];
        System.arraycopy(data, position, temp, 0, 4);
        position += 4;
        msg.contentLength = ArrayUtils.bytesToInt(temp);

        if (msg.contentLength > 0)
        {
            temp = new byte[msg.contentLength];
            System.arraycopy(data, position, temp, 0, msg.contentLength);
            position += msg.contentLength;
            msg.content = new String(temp);
        } else
        {
            LOGGER.warn("actual content length:{} is less than 0", msg.contentLength);
        }
        return msg;
    }

    public static Message copy(Message src)
    {
        final Message dst = new Message();
        try
        {
            BeanUtils.copyProperties(dst, src);
        } catch (Exception e)
        {
            LOGGER.error("copy properties failed", e);
        }
        return dst;
    }
}
