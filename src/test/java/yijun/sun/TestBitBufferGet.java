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


import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author SunYiJun
 */
public class TestBitBufferGet {

    @Test
    public void test_get_bit_len8_in_one_byte() {
        //11010010
        byte[] data = { (byte)210 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        assertThat(buffer.remainingBits()).isEqualTo(8);
        byte bit = buffer.getByte(8);
        assertThat(bit).isEqualTo((byte)210);
        assertThat(buffer.remainingBits()).isEqualTo(0);

        //01011001
        data = new byte[]{ 89 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getByte(8);
        assertThat(bit).isEqualTo((byte)89);
    }

    @Test
    public void test_get_bit_len2_and_len6_in_one_byte() {
        //11 010010
        byte[] data = { (byte)210 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        byte bit = buffer.getByte(2);
        assertThat(bit).isEqualTo((byte)3);
        assertThat(buffer.remainingBits()).isEqualTo(6);

        bit = buffer.getByte(6);
        assertThat(bit).isEqualTo((byte)18);
        assertThat(buffer.remainingBits()).isEqualTo(0);
    }

    @Test
    public void test_get_bit_from1_len5_in_one_byte() {
        //1 10100 10
        byte[] data = { (byte)210 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        byte bit = buffer.getByte(1, 5);
        assertThat(bit).isEqualTo((byte)20);
        assertThat(buffer.remainingBits()).isEqualTo(8);

        //0 10110 01
        data = new byte[]{ 89 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getByte(1, 5);
        assertThat(bit).isEqualTo((byte)22);

        //1 01110 01
        data = new byte[]{ (byte)185 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getByte(1, 5);
        assertThat(bit).isEqualTo((byte)14);
    }

    @Test
    public void test_get_bit_len2_and_len8_in_two_byte2() {
        //11 010010  00 110010
        byte[] data = { (byte)210, 50 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        assertThat(buffer.remainingBits()).isEqualTo(16);
        byte bit = buffer.getByte(2);
        assertThat(bit).isEqualTo((byte)3);
        assertThat(buffer.remainingBits()).isEqualTo(14);
        bit = buffer.getByte(8);
        //01001000
        assertThat(bit).isEqualTo((byte)72);
        assertThat(buffer.remainingBits()).isEqualTo(6);

        //11 110010  10 110010
        data = new byte[]{ (byte)242, (byte)178 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getByte(2);
        assertThat(bit).isEqualTo((byte)3);
        bit = buffer.getByte(8);
        //11001010
        assertThat(bit).isEqualTo((byte)202);
    }

    @Test
    public void test_get_bit_len8_and_len6_in_two_byte2() {
        //11010010  001100 10
        byte[] data = { (byte)210, 50 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        byte bit = buffer.getByte(8);
        assertThat(bit).isEqualTo((byte)210);
        assertThat(buffer.remainingBits()).isEqualTo(8);
        bit = buffer.getByte(6);
        //001100
        assertThat(bit).isEqualTo((byte)12);
        assertThat(buffer.remainingBits()).isEqualTo(2);

        //11110010  101100 10
        data = new byte[]{ (byte)242, (byte)178 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getByte(8);
        assertThat(bit).isEqualTo((byte)242);
        bit = buffer.getByte(6);
        //101100
        assertThat(bit).isEqualTo((byte)44);
    }

    @Test
    public void test_get_bytes_bit_len_less_than_8() {
        //1101 0010  00110010
        byte[] data = { (byte)210, 50 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        byte[] bit = buffer.getBytes(4);
        assertThat(bit.length).isEqualTo(1);
        assertThat(bit[0]).isEqualTo((byte)13);
        assertThat(buffer.remainingBits()).isEqualTo(12);

        //11110010  10110010
        data = new byte[]{ (byte)242, (byte)178 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getBytes(8);
        assertThat(bit.length).isEqualTo(1);
        assertThat(bit[0]).isEqualTo((byte)242);
        assertThat(buffer.remainingBits()).isEqualTo(8);
    }

    @Test
    public void test_get_bytes_bit_len_between_8_and_16() {
        //11010010  00 110010
        byte[] data = { (byte)210, 50 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        byte[] bits = buffer.getBytes(10);
        //11 01001000
        assertThat(bits.length).isEqualTo(2);
        assertThat(bits).isEqualTo(new byte[]{ (byte)3, 72 });
        assertThat(buffer.remainingBits()).isEqualTo(6);

        byte bit = buffer.getByte(6);
        //110010
        assertThat(bit).isEqualTo((byte)50);
        assertThat(buffer.remainingBits()).isEqualTo(0);
    }

    @Test
    public void test_get_bytes_bit_len_more_than_16() {
        //11010010  00110010  01001000
        byte[] data = { (byte)210, 50, 72 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        assertThat(buffer.remainingBits()).isEqualTo(24);
        byte[] bits = buffer.getBytes(16);
        //11010010  00110010
        assertThat(bits.length).isEqualTo(2);
        assertThat(bits).isEqualTo(new byte[]{ (byte)210, 50 });
        assertThat(buffer.remainingBits()).isEqualTo(8);
        byte bit = buffer.getByte();
        assertThat(bit).isEqualTo((byte)72);
        assertThat(buffer.remainingBits()).isEqualTo(0);

        //11010010  00110010  01001000
        data = new byte[]{ (byte)210, 50, 72 };
        buffer = BitBuffer.wrapBytes(data);
        bit = buffer.getByte();
        assertThat(bit).isEqualTo((byte)210);
        bits = buffer.getBytes(16);
        //00110010  01001000
        assertThat(bits.length).isEqualTo(2);
        assertThat(bits).isEqualTo(new byte[]{ 50, 72 });

        //11010010  00110010  01001000
        data = new byte[]{ (byte)210, 50, 72 };
        buffer = BitBuffer.wrapBytes(data);
        bits = buffer.getBytes(24);
        //11010010  00110010  01001000
        assertThat(bits.length).isEqualTo(3);
        assertThat(bits).isEqualTo(new byte[]{ (byte)210, 50, 72 });
        assertThat(buffer.remainingBits()).isEqualTo(0);

        //11010010  00110010  01001000
        data = new byte[]{ (byte)210, 50, 72 };
        buffer = BitBuffer.wrapBytes(data);
        bits = buffer.getBytes(20);
        //1101 00100011 00100100
        assertThat(bits.length).isEqualTo(3);
        assertThat(bits).isEqualTo(new byte[]{ 13, 35, 36 });
        assertThat(buffer.remainingBits()).isEqualTo(4);
        bit = buffer.getByte(4);
        //1000
        assertThat(bit).isEqualTo((byte)8);
        assertThat(buffer.remainingBits()).isEqualTo(0);

        //11010010  00110010  01001000
        data = new byte[]{ (byte)210, 50, 72 };
        buffer = BitBuffer.wrapBytes(data);
        buffer.getByte(6);
        assertThat(buffer.remainingBits()).isEqualTo(18);
        bits = buffer.getBytes(18);
        //10  00110010  01001000
        assertThat(bits.length).isEqualTo(3);
        assertThat(bits).isEqualTo(new byte[]{ 2, 50, 72 });
        assertThat(buffer.remainingBits()).isEqualTo(0);
    }

    @Test
    public void test_get_bytes_bit_from7_len_more_than_8_and_position_not_change() {
        //11010010  00110010  01001000
        byte[] data = { (byte)210, 50, 72 };
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        byte[] bits = buffer.getBytes(7, 8);
        //00011001
        assertThat(bits.length).isEqualTo(1);
        assertThat(bits[0]).isEqualTo((byte)25);
        assertThat(buffer.remainingBits()).isEqualTo(24);

        byte bit = buffer.getByte();
        //11010010
        assertThat(bit).isEqualTo((byte)210);
        assertThat(buffer.remainingBits()).isEqualTo(16);

        //11010010  00110010  01001000
        data = new byte[]{ (byte)210, 50, 72 };
        buffer = BitBuffer.wrapBytes(data);
        bits = buffer.getBytes(7, 16);
        //00011001 00100100
        assertThat(bits.length).isEqualTo(2);
        assertThat(bits).isEqualTo(new byte[]{ 25, 36 });

        //11010010  00110010  01001000
        data = new byte[]{ (byte)210, 50, 72 };
        buffer = BitBuffer.wrapBytes(data);
        bits = buffer.getBytes(7, 17);
        //0 00110010 01001000
        assertThat(bits.length).isEqualTo(3);
        assertThat(bits).isEqualTo(new byte[]{ 0, 50, 72 });
    }

}
