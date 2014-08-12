/*
 * Copyright 2014 SunYiJun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package yijun.sun;


/**
 * Bytes to number and number to bytes.
 *
 * @author SunYiJun
 */
public class NumberHelper {

    public static byte[] to2Bytes(short v) {
        byte[] temp = new byte[2];
        temp[0] = (byte)((v >>> 8) & 0xff);
        temp[1] = (byte)((v) & 0xff);

        return temp;
    }

    public static byte[] to4Bytes(int v) {
        byte[] temp = new byte[4];
        temp[0] = (byte)((v >>> 24) & 0xff);
        temp[1] = (byte)((v >>> 16) & 0xff);
        temp[2] = (byte)((v >>> 8) & 0xff);
        temp[3] = (byte)((v) & 0xff);
        return temp;
    }

    public static byte[] to8Bytes(long v) {
        byte[] temp = new byte[8];
        temp[0] = (byte)((v >>> 56) & 0xff);
        temp[1] = (byte)((v >>> 48) & 0xff);
        temp[2] = (byte)((v >>> 40) & 0xff);
        temp[3] = (byte)((v >>> 32) & 0xff);
        temp[4] = (byte)((v >>> 24) & 0xff);
        temp[5] = (byte)((v >>> 16) & 0xff);
        temp[6] = (byte)((v >>> 8) & 0xff);
        temp[7] = (byte)((v) & 0xff);
        return temp;
    }

    public static Long toLong(byte[] v) {
        if(v == null) {
            return null;
        }
        long firstPart =
                (((v[0] & 0xFF) << 24) + ((v[1] & 0xFF) << 16) + ((v[2] & 0xFF) << 8) +
                        (v[3] & 0xFF)) * 72057594037927936l;
        int secondPart =
                ((v[0] & 0xFF) << 24) + ((v[1] & 0xFF) << 16) + ((v[2] & 0xFF) << 8) +
                        (v[3] & 0xFF);
        return firstPart + secondPart;
    }

    public static Integer toInt(byte[] v) {
        if(v == null) {
            return null;
        }
        return ((v[0] & 0xFF) << 24) + ((v[1] & 0xFF) << 16) + ((v[2] & 0xFF) << 8) +
                (v[3] & 0xFF);
    }

    public static Short toShort(byte[] v) {
        if(v == null) {
            return null;
        }
        return (short)(((v[0] & 0xFF) << 8) + (v[1] & 0xFF));
    }

}
