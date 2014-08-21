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

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author SunYiJun
 */
public class TestBinStringHelper {

    @Test
    public void test_210_to_11010010() {
        //11010010
        byte data = (byte)210;
        String bin = BinStringHelper.bytesToString(data);
        assertThat(bin).isEqualTo("11010010 ");
    }

    @Test
    public void test_1_to_00000001() {
        byte data = 1;
        String bin = BinStringHelper.bytesToString(data);
        assertThat(bin).isEqualTo("00000001 ");
    }

    @Test
    public void test_ff_00_to_11111111_00000000() {
        byte[] data = { (byte)0xff, (byte)0x00 };
        String bin = BinStringHelper.bytesToString(data);
        assertThat(bin).isEqualTo("11111111 00000000 ");
    }

    @Test
    public void test_int_1_to_00000000_00000000_00000000_00000001() {
        int data = 1;
        String bin = BinStringHelper.IntToString(data);
        assertThat(bin).isEqualTo("00000000 00000000 00000000 00000001 ");
    }

    @Test
    public void test_int_negative1_to_11111111_11111111_11111111_11111111() {
        int data = -1;
        String bin = BinStringHelper.IntToString(data);
        assertThat(bin).isEqualTo("11111111 11111111 11111111 11111111 ");
    }

    @Test
    public void test_short_0_to_00000000_00000000() {
        short data = 0;
        String bin = BinStringHelper.ShortToString(data);
        assertThat(bin).isEqualTo("00000000 00000000 ");
    }

    @Test
    public void test_short_negative1_to_00000000_00000000() {
        short data = -1;
        String bin = BinStringHelper.ShortToString(data);
        assertThat(bin).isEqualTo("11111111 11111111 ");
    }

    @Test
    public void test_long_9223372036854775807_to_0_and_63_1() {
        long data = 9223372036854775807l;
        String bin = BinStringHelper.LongToString(data);
        assertThat(bin).isEqualTo("01111111 11111111 11111111 11111111 11111111 " +
                "11111111 11111111 11111111 ");
    }

    @Test
    public void test_10000000_00000000_to_128_0() {
        String data = "10000000 00000000";
        byte[] bytes = BinStringHelper.toByteArray(data);
        assertThat(bytes).isEqualTo(new byte[]{ (byte)128, 0 });
    }

    @Test
    public void test_1000000_10000000_to_64_0() {
        String data = "1000000 10000000";
        byte[] bytes = BinStringHelper.toByteArray(data);
        assertThat(bytes).isEqualTo(new byte[]{ 64, (byte)128 });
        data = "01000000 10000000";
        bytes = BinStringHelper.toByteArray(data);
        assertThat(bytes).isEqualTo(new byte[]{ 64, (byte)128 });
    }

    @Test
    public void test_10000000_to_128() {
        String data = "10000000";
        byte[] bytes = BinStringHelper.toByteArray(data);
        assertThat(bytes).isEqualTo(new byte[]{ (byte)128 });
    }

    @Test
    public void test_1000_to_8() {
        String data = "1000";
        byte[] bytes = BinStringHelper.toByteArray(data);
        assertThat(bytes).isEqualTo(new byte[]{ 8 });
        data = "001000";
        bytes = BinStringHelper.toByteArray(data);
        assertThat(bytes).isEqualTo(new byte[]{ 8 });
    }

}
