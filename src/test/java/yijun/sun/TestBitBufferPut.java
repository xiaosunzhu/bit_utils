package yijun.sun;


import org.junit.Test;

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
        } catch(IndexOutOfBoundsException e) {
        }
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
        } catch(IndexOutOfBoundsException e) {
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
        } catch(IndexOutOfBoundsException e) {
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
        } catch(IndexOutOfBoundsException e) {
        }
        buffer.flip();
        assertThat(buffer.remainingBits()).isEqualTo(16);
        assertThat(buffer.getByte()).isEqualTo((byte)9);//00000100
        assertThat(buffer.getByte()).isEqualTo((byte)156);//11001110
    }

}
