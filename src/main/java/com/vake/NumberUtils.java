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

/**
 * 数字工具集<p/>
 *
 * @author xiongqimin
 * @version 1.0.0
 * @history<br/> ver    date       author desc
 * 1.0.0  2015-12-28 xiongqimin    created<br/>
 * <p/>
 * @since 1.0.0
 */
public class NumberUtils
{
    private static final char s_ucEndMask[] =
            {
                    0xf0, 0xf
            };

    private static final char s_ucMaxValue[] =
            {
                    0x90, 0x9
            };

    private static final char s_ucBitOffset[] =
            {
                    4, 0
            };

    public static byte[] longToBytes(long value)
    {
        final byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++)
        {
            final int offset = (bytes.length - 1 - i) * 8;
            bytes[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return bytes;
    }

    public static byte[] shortToBytes(short s)
    {
        byte[] shortBuf = new byte[2];
        for (int i = 0; i < 2; i++)
        {
            int offset = (shortBuf.length - 1 - i) * 8;
            shortBuf[i] = (byte) ((s >>> offset) & 0XFF);
        }
        return shortBuf;
    }

    public static short byteArrayToShort(byte[] b)
    {
        return (short) ((b[0] << 8) + (b[1] & 0xFF));
    }

    public static byte[] intToBytes(int value)
    {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0XFF);
        }
        return b;
    }

    public static int byteArrayToInt(byte[] b)
    {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    /**
     * 将32位int表示的IPV4地址转化为字符串的表示形式
     *
     * @param intIp IP
     * @return String，字符串形式的IP
     */
    public static String intToIpV4String(int intIp)
    {
        return ((intIp >> 24) & 0xFF)
                + "." + ((intIp >> 16) & 0xFF)
                + "." + ((intIp >> 8) & 0xFF)
                + "." + (intIp & 0xFF);
    }

    public static byte[] stringToBcd(String str, int ulStrLen)
    {
        int i, ulOver;
        char ch;
        byte[] bcd = new byte[ulStrLen / 2];
        for (int j = 0; j < ulStrLen / 2; j++)
        {
            bcd[j] = 0;
        }
        ulOver = 0;
        int len = str.length();
        if (len == ulStrLen)
        {
            len -= 1;
        }
        for (i = 0; i < len; i++)
        {
            ch = str.charAt(i);
            switch (ch)
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | (((char) (ch - '0')) << s_ucBitOffset[i % 2]));
                    break;
                case 'a':
                case 'A':
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | ((0x0A) << s_ucBitOffset[i % 2]));
                    break;
                case 'b':
                case 'B':
                case '*':
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | ((0x0B) << s_ucBitOffset[i % 2]));
                    break;
                case 'c':
                case 'C':
                case '#':
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | ((0x0C) << s_ucBitOffset[i % 2]));
                    break;
                case 'd':
                case 'D':
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | ((0x0D) << s_ucBitOffset[i % 2]));
                    break;
                case 'e':
                case 'E':
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | ((0x0E) << s_ucBitOffset[i % 2]));
                    break;
                case 'f':
                case 'F':
                default:  /* 碰到了非法字符，则转换成结束符 */
                    bcd[i / 2] = (byte) ((bcd[i / 2] & (s_ucEndMask[i % 2] << 4)) | ((0x0F) << s_ucBitOffset[i % 2]));
                    ulOver = 1;
                    break;
            }

            if (1 == ulOver)  /* 碰到了结束符，退出 */

            {
                break;
            }
        }
        bcd[i / 2] |= s_ucEndMask[i % 2];
        return bcd;
    }

    public static String bcdToString(byte[] bcd)
    {
        byte bcd_value;
        int i;
        char[] str = new char[bcd.length * 2];
        for (i = 0; i < bcd.length * 2; i++)
        {
            if ((bcd[i / 2] & s_ucEndMask[i % 2]) == s_ucEndMask[i % 2])
            {
                break;
            }

            if (i >= bcd.length * 2)
            {
                break;
            }

            bcd_value = (byte) ((bcd[i / 2] >> s_ucBitOffset[i % 2]) & 0xf);

            switch (bcd_value)
            {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    str[i] = (char) (bcd_value + '0');
                    break;

                case 0x0A:
                    str[i] = 'A';
                    break;

                case 0x0B:
                    str[i] = '*';
                    break;

                case 0x0C:
                    str[i] = '#';
                    break;

                case 0x0D:
                    str[i] = 'D';
                    break;

                case 0x0E:
                    str[i] = 'E';
                    break;

                case 0x0F:
                default:
                    str[i] = 'F';
                    break;
            }
        }

        StringBuilder stb = new StringBuilder("");
        for (int k = 0; k < i; k++)
        {
            stb.append(str[k]);
        }
        return stb.toString();
    }

    public static byte[] byte2bitArray(byte byteValue)
    {
        final int LENGTH = 8;
        final String binaryStr = Integer.toBinaryString(byteValue);
        final String binaryString = formatBinaryStr(binaryStr, LENGTH);
        int beginPos = binaryString.length() - LENGTH;
        final String byteBinaryStr = binaryString.substring(beginPos);
        byte[] bitArr = new byte[LENGTH];
        for (int i = 0; i < LENGTH; i++)
        {
            bitArr[i] = Byte.valueOf("" + byteBinaryStr.charAt(i));
        }
        return bitArr;
    }

    static String formatBinaryStr(String binaryStr, int length)
    {
        final int size = length - binaryStr.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++)
        {
            sb.append("0");
        }
        sb.append(binaryStr);
        return sb.toString();
    }

    /**
     * short高低为互换
     *
     * @param n
     * @return
     */
    public static short changedLhForShort(short n)
    {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return byteArrayToShort(b);
    }

    /**
     * int高低为互换
     *
     * @param n
     * @return
     */

    public static int changedLhForInt(int n)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return (byteArrayToInt(b) & 0xFFFFFFFF);
    }
}
