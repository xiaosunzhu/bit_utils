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

package yijun.sun.bit;


import org.junit.Test;
import yijun.sun.bit.BitBuffer;

import java.nio.BufferOverflowException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;


/**
 * @author SunYiJun
 */
public class TestBitBufferPut {

    @Test
    public void test_put_bit_byte_len8_in_one_byte() {
        //11010010
        byte data = (byte)210;
        BitBuffer buffer = BitBuffer.allocate(8);
        assertThat(buffer.remainingBits()).isEqualTo(8);
        buffer.put(data, 8);
        assertThat(buffer.remainingBits()).isEqualTo(0);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(8);
        assertThat(buffer.getByte()).isEqualTo((byte)210);

        //01011001
        data = 89;
        buffer = BitBuffer.allocate(10);
        assertThat(buffer.remainingBits()).isEqualTo(10);
        buffer.put(data, 8);//01011001
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(8);
        assertThat(buffer.getByte()).isEqualTo((byte)89);

        buffer = BitBuffer.allocate(7);
        try {
            buffer.put((byte)1, 8);
            fail("Out of bounds must throw exception.");
        } catch(BufferOverflowException e) {
        }
    }

    @Test
    public void test_put_bit_truncate_byte_len2_in_one_byte() {
        //1001
        byte data = 9;
        BitBuffer buffer = BitBuffer.allocate(14);
        buffer.put(data, 2);
        buffer.put(data, 8);//01000010 01
        buffer.flip();
        assertThat(buffer.getByte()).isEqualTo((byte)66);
        assertThat(buffer.getByte(2)).isEqualTo((byte)1);
    }

    @Test
    public void test_put_bit_byte_len4_in_one_byte() {
        //1001
        byte data = (byte)9;
        BitBuffer buffer = BitBuffer.allocate(8);
        buffer.put(data, 4);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(4);
        assertThat(buffer.getByte(4)).isEqualTo((byte)9);
        try {
            buffer.getByte(1);
            fail("Out of bounds must throw exception.");
        } catch(BufferOverflowException e) {
        }

        //0010
        data = 2;
        buffer = BitBuffer.allocate(4);
        buffer.put(data, 4);
        buffer.flip();
        assertThat(buffer.getByte(2)).isEqualTo((byte)0);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        assertThat(buffer.getByte(2)).isEqualTo((byte)2);
        assertThat(buffer.remainingBits()).isEqualTo(0);
    }

    @Test
    public void test_put_bit_byte_len4_and_len8_in_two_bytes() {
        BitBuffer buffer = BitBuffer.allocate(12);
        //1101
        buffer.put((byte)13, 4);
        assertThat(buffer.remainingBits()).isEqualTo(8);
        //01011001
        buffer.put((byte)89, 8);
        assertThat(buffer.remainingBits()).isEqualTo(0);

        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(12);
        assertThat(buffer.getByte(4)).isEqualTo((byte)13);//1101
        assertThat(buffer.getByte(8)).isEqualTo((byte)89);//01011001

        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(12);
        assertThat(buffer.getByte(8)).isEqualTo((byte)213);//11010101
        assertThat(buffer.getByte(4)).isEqualTo((byte)9);//1001

        buffer = BitBuffer.allocate(12);
        //110
        buffer.put((byte)6, 3);
        //0001010
        buffer.put((byte)10, 7);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(10);
        assertThat(buffer.getByte()).isEqualTo((byte)194);//11000010
        assertThat(buffer.getByte(2)).isEqualTo((byte)2);//10
    }

    @Test
    public void test_put_bit_bytes_len16_in_two_bytes() {
        //11010010 01011001
        byte[] data = new byte[]{ (byte)210, 89 };
        BitBuffer buffer = BitBuffer.allocate(16);
        try {
            buffer.put(data, 17);
            fail("Out of length must throw exception.");
            assertThat(buffer.remainingBits()).isEqualTo(16);
        } catch(IllegalArgumentException e) {
        }
        buffer.put(data, 16);
        buffer.flip();
        assertThat(buffer.getByte()).isEqualTo((byte)210);
        assertThat(buffer.getByte()).isEqualTo((byte)89);
    }

    @Test
    public void test_put_bit_bytes_len18_in_three_bytes() {
        //11010010 01011001 11010010
        byte[] data = new byte[]{ (byte)210, 89, (byte)210 };
        BitBuffer buffer = BitBuffer.allocate(20);
        buffer.put(data, 18);//11010010 01011001 11
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(18);
        assertThat(buffer.getByte()).isEqualTo((byte)210);
        assertThat(buffer.getByte()).isEqualTo((byte)89);
        assertThat(buffer.getByte(2)).isEqualTo((byte)3);//11
    }

    @Test
    public void test_put_bit_byte_len4_in_one_byte_from_1() {
        //1001
        byte data = (byte)9;
        BitBuffer buffer = BitBuffer.allocate(8);
        buffer.put(data, 1, 4);
        assertThat(buffer.remainingBits()).isEqualTo(8);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(0);

        //1001
        data = (byte)9;
        buffer = BitBuffer.allocate(7);
        buffer.put(data, 4);
        //10
        data = (byte)2;
        buffer.put(data, 1, 2);
        assertThat(buffer.remainingBits()).isEqualTo(3);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(4);
        assertThat(buffer.getByte(4)).isEqualTo((byte)13);//00001101

        //1001
        data = (byte)9;
        buffer = BitBuffer.allocate(2);
        buffer.put(data, 2);//10
        //11
        data = (byte)3;
        try {
            buffer.put(data, 1, 2);
            fail("Out of bounds must throw exception.");
        } catch(BufferOverflowException e) {
        }
    }

    @Test
    public void test_put_bit_bytes_len14_in_two_byte_from_1() {
        //00001001 10011100
        byte[] data = { 9, (byte)156 };
        BitBuffer buffer = BitBuffer.allocate(16);
        buffer.put(data);
        assertThat(buffer.remainingBits()).isEqualTo(0);
        buffer.put(data, 1, 14);//00000100 11001110
        assertThat(buffer.remainingBits()).isEqualTo(0);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(16);
        assertThat(buffer.getByte()).isEqualTo((byte)4);//00000100
        assertThat(buffer.getByte()).isEqualTo((byte)206);//11001110

        //00001001 10011100
        data = new byte[]{ 9, (byte)156 };
        buffer = BitBuffer.allocate(16);
        buffer.put(data, 16);
        assertThat(buffer.remainingBits()).isEqualTo(0);
        try {
            buffer.put(data, 1, 16);//00000100 11001110
        } catch(BufferOverflowException e) {
        }
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(16);
        assertThat(buffer.getByte()).isEqualTo((byte)9);//00000100
        assertThat(buffer.getByte()).isEqualTo((byte)156);//11001110
    }

    @Test
    public void test_put_bit_bytes_from_bytes_right_part() {
        //00001001 10011100
        byte[] data = { 9, (byte)156 };
        BitBuffer buffer = BitBuffer.allocate(16);
        buffer.putRightPart(data, 8);//10011100
        buffer.flip();
        assertThat(buffer.getByte()).isEqualTo((byte)156);

        //00001001 10011100
        data = new byte[]{ 9, (byte)156 };
        buffer = BitBuffer.allocate(16);
        buffer.putRightPart(data, 6);//011100
        buffer.flip();
        assertThat(buffer.getByte(6)).isEqualTo((byte)28);

        //00001001 10011100
        data = new byte[]{ 9, (byte)156 };
        buffer = BitBuffer.allocate(16);
        buffer.putRightPart(data, 10);//01 10011100
        buffer.flip();
        assertThat(buffer.getByte()).isEqualTo((byte)103);//01100111
        assertThat(buffer.getByte(2)).isEqualTo((byte)0);//00
    }

    @Test
    public void test_put_int_len14_in_two_byte() {
        //1001 10011100
        int data = 2460;
        BitBuffer buffer = BitBuffer.allocate(16);
        buffer.put(data, 14);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(14);
        //00100110 011100
        assertThat(buffer.getByte()).isEqualTo((byte)38);//00100110
        assertThat(buffer.getByte(6)).isEqualTo((byte)28);//011100

        //11101001 10011100
        data = 59804;
        buffer = BitBuffer.allocate(16);
        buffer.put(data, 14);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(14);
        //10100110 011100
        assertThat(buffer.getByte()).isEqualTo((byte)166);//10100110
        assertThat(buffer.getByte(6)).isEqualTo((byte)28);//011100

        //11110000 11110000 11101001 10011100
        data = 0xf0f0e99c;
        buffer = BitBuffer.allocate(16);
        buffer.put(data, 14);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(14);
        //10100110 011100
        assertThat(buffer.getByte()).isEqualTo((byte)166);//10100110
        assertThat(buffer.getByte(6)).isEqualTo((byte)28);//011100
    }


    @Test
    public void test_put_short_len14_in_two_byte() {
        //1001 10011100
        short data = 2460;
        BitBuffer buffer = BitBuffer.allocate(16);
        buffer.put(data, 14);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(14);
        //00100110 011100
        assertThat(buffer.getByte()).isEqualTo((byte)38);//00100110
        assertThat(buffer.getByte(6)).isEqualTo((byte)28);//011100

        //11101001 10011100
        data = (short)0xe99c;
        buffer = BitBuffer.allocate(16);
        buffer.put(data, 14);
        assertThat(buffer.remainingBits()).isEqualTo(2);
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(14);
        //10100110 011100
        assertThat(buffer.getByte()).isEqualTo((byte)166);//10100110
        assertThat(buffer.getByte(6)).isEqualTo((byte)28);//011100
    }

}
