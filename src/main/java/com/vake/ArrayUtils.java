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

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * 数组工具集<p/>
 *
 * @author xiongqimin
 * @version 1.0.0
 * @history<br/> ver    date       author desc
 * 1.0.0  2015-12-28 xiongqimin    created<br/>
 * <p/>
 * @since 1.0.0
 */
public class ArrayUtils
{
    public static final byte[] EMPTY_BYTE_ARRAY = org.apache.commons.lang.ArrayUtils.EMPTY_BYTE_ARRAY;

    public static final Object[] EMPTY_OBJECT_ARRAY = org.apache.commons.lang.ArrayUtils.EMPTY_OBJECT_ARRAY;

    /**
     * 将字节数组转换为由radix指定的数制的字符串<p/>
     * 如果bytes等于null，将返回"null"，如果bytes的长度为零，将返回"[]"
     *
     * @param bytes                 字节数组
     * @param radix，数制，值必须为2、8或16之一
     * @return 指定数制的字符串
     */
    public static String toString(byte[] bytes, int radix)
    {
        if (2 != radix && 8 != radix && 16 != radix)
        {
            final String msg = "radix must be 2, 8 or 16";
            throw new IllegalArgumentException(msg);
        }
        if (null == bytes)
        {
            return "null";
        }

        final StringBuilder builder = new StringBuilder("[");
        final int len = bytes.length;
        for (int i = 0; i < len; i++)
        {
            final int intValue = bytes[i] & 0xFF;
            final String string;
            final String padString;
            switch (radix)
            {
                case 2:
                    string = Integer.toBinaryString(intValue);
                    padString = StringUtils.leftPad(string, 8, '0');
                    break;
                case 8:
                    string = Integer.toOctalString(intValue);
                    padString = StringUtils.leftPad(string, 3, '0');
                    break;
                default:
                    string = Integer.toHexString(intValue);
                    padString = StringUtils.leftPad(string, 2, '0');
            }
            builder.append(padString.toUpperCase());
            if (i < len - 1)
            {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static String toHexString(byte[] bytes)
    {
        return toString(bytes, 16);
    }

    public static String toBinaryString(byte[] bytes)
    {
        return toString(bytes, 16);
    }

    public static boolean isEquals(Object array1, Object array2)
    {
        return org.apache.commons.lang.ArrayUtils.isEquals(array1, array2);
    }

    public static short bytesToShort(byte[] bytes)
    {
        return (short) ((bytes[0] << 8) + (bytes[1] & 0xFF));
    }

    // FIXME 测试是无符号转换是否正确(C可能无符号)
    public static int bytesToInt(byte[] bytes)
    {
        final byte[] tmp = new byte[4];
        for (int i = bytes.length; i > 0; i--)
        {
            tmp[3 - i + 1] = bytes[bytes.length - i];
        }
        return (tmp[0] << 24) + ((tmp[1] & 0xFF) << 16) + ((tmp[2] & 0xFF) << 8)
                + (tmp[3] & 0xFF);
    }

    public static long bytesToLong(byte[] bytes)
    {
        long r = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            r <<= 8;
            r |= (bytes[i] & 0x00000000000000ff);
        }
        return r;
    }

    public static byte bitArrayToByte(byte[] bytes)
    {
        byte rtnValue = 0;
        final int length = bytes.length;
        for (int i = 1; i <= length; i++)
        {
            final int power = length - i;
            rtnValue += bytes[i - 1] * Math.pow(2, power);
        }
        return rtnValue;
    }

    /**
     * 合并两个字节数组
     *
     * @param a 字节数组
     * @param b 字节数组
     * @return 合并后的字节数组
     */
    public static byte[] combine(byte[] a, byte[] b)
    {
        if (null == a && null == b)
        {
            return null;
        }
        if (null == a && null != b)
        {
            return b;
        }
        if (null != a && null == b)
        {
            return a;
        }
        final byte[] bytes = new byte[a.length + b.length];
        System.arraycopy(a, 0, bytes, 0, a.length);
        System.arraycopy(b, 0, bytes, a.length, b.length);
        return bytes;
    }

    public static boolean isEmpty(byte[] bytes)
    {
        return org.apache.commons.lang.ArrayUtils.isEmpty(bytes);
    }

    public static String cppAsciiBytesToJavaString(byte[] bytes)
    {
        return cppAsciiBytesToJavaString(bytes, null);
    }

    public static String cppAsciiBytesToJavaString(byte[] bytes, Charset charset)
    {
        if (null == bytes)
        {
            return null;
        }
        if (0 >= bytes.length)
        {
            return "";
        }
        // 去掉C/C++的字符串结束符后的内容
        int endStringPos = 0;
        while (endStringPos < bytes.length && 0 != bytes[endStringPos])
        {
            endStringPos++;
        }
        final String string = null == charset ? new String(bytes, 0, endStringPos)
                : new String(bytes, 0, endStringPos, charset);
        return string;
    }

    public static boolean isEmpty(Object[] array)
    {
        return array == null || array.length == 0;
    }

    /**
     * 将byte数组填充成指定长度（length）,不足长度则在数组后补"0"
     * 大于指定长度则将超过的部分丢弃掉。
     *
     * @param src
     * @param length
     * @return
     */
    public static byte[] fillArray(byte[] src, int length)
    {
        if (src == null)
        {
            throw new IllegalArgumentException("needed fill byte array can't be null!");
        }
        final int srcLength = src.length;
        byte[] dest = new byte[length];
        //如果src长度小于需要的长度，则在后面补零
        if (srcLength < length)
        {
            Arrays.fill(dest, (byte) 0);
            System.arraycopy(src, 0, dest, 0, srcLength);
            return dest;
        }

        //否则只取src中的前length长度
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    public static String bytesToIpV4Address(byte[] bytes)
    {
        final StringBuilder sb = new StringBuilder();
        for (byte element : bytes)
        {
            final byte[] ipFragmentBytes = new byte[1];
            ipFragmentBytes[0] = element;
            sb.append(bytesToInt(ipFragmentBytes));
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String bytesToMac(byte[] bytes)
    {
        final StringBuilder sb = new StringBuilder();
        for (byte element : bytes)
        {
            final String fragment = Integer.toHexString(0xFF & element);
            if (fragment.length() < 2)
            {
                sb.append(0);
            }
            sb.append(fragment);
            sb.append("-");
            sb.append((byte) (element & 0x0F));
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String bytesToMacString(byte[] bytes)
    {
        if (isEmpty(bytes) || bytes.length != 6)
        {
            final String msg = "invalid mac address bytes, string=" + toHexString(bytes);
            throw new IllegalArgumentException(msg);
        }
        final String[] copy = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            final String hex = Integer.toHexString(bytes[i]);
            copy[i] = hex.length() < 2 ? "0" + hex : hex;
        }
        return org.apache.commons.lang.StringUtils.join(copy, "-");
    }

    /**
     * 将字节数组转换为字符串的简单表示形式，中间没有任何分隔符，每个字节变成XX的形式<p/>
     * 如果bytes等于null，将返回"null"，如果bytes的长度为零，将返回""
     *
     * @param bytes 字节数组
     * @return 字节数组的字符串表示形式
     */
    public static String toSimpleHexString(byte[] bytes)
    {
        if (null == bytes)
        {
            return "null";
        }

        final int len = bytes.length;
        final StringBuilder builder = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++)
        {
            final int intValue = bytes[i] & 0xFF;
            final String string = Integer.toHexString(intValue);
            final String padString = StringUtils.leftPad(string, 2, '0');
            builder.append(padString.toUpperCase());
        }
        return builder.toString();
    }
}
