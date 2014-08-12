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


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * To operate byte array in binary.
 * All operate like {@link ByteBuffer}.<br/>
 * Use {@link #wrapBytes(byte[])} or {@link #allocate(int)} to create buffer.
 *
 * @author SunYiJun
 */
public class BitBuffer {

    private ByteBuffer buffer;

    private int positionInByte;

    private int voidBitsInLastByte;

    private int bitTotalLength;

    private BitBuffer() {
    }

    /**
     * Create a buffer filled with byte array.
     */
    public static BitBuffer wrapBytes(byte[] bytesData) {
        BitBuffer bitBuffer = new BitBuffer();
        if(bytesData == null || bytesData.length == 0) {
            throw new IllegalArgumentException("bytesData should not be null or empty.");
        }
        bitBuffer.buffer = ByteBuffer.wrap(bytesData);
        bitBuffer.bitTotalLength = bytesData.length << 3;
        return bitBuffer;
    }

    /**
     * Create a buffer in fixed bit length.
     *
     * @param bitLength not byte count,1 byte have 8 bit length.
     */
    public static BitBuffer allocate(int bitLength) {
        if(bitLength <= 0) {
            throw new IllegalArgumentException("bitLength must larger than 0.");
        }
        int byteLength = (bitLength + 7) >>> 3;
        BitBuffer bitBuffer = new BitBuffer();
        bitBuffer.buffer = ByteBuffer.allocate(byteLength);
        int temp = bitLength & 0x07;
        bitBuffer.voidBitsInLastByte = 8 - (temp == 0 ? 8 : temp);
        bitBuffer.bitTotalLength = bitLength;
        return bitBuffer;
    }

    /**
     * Like {@link java.nio.ByteBuffer#remaining()}, but return bit count not byte count.
     */
    public int remainingBits() {
        int remainingBytes = buffer.remaining();
        return (remainingBytes << 3) - positionInByte - voidBitsInLastByte;
    }

    /**
     * Like {@link java.nio.ByteBuffer#flip()}
     */
    public void flip() {
        if(positionInByte > 0) {
            buffer.position(buffer.position() + 1);
            voidBitsInLastByte = 8 - positionInByte;
        }
        positionInByte = 0;
        buffer.flip();
    }

    /**
     * Get one byte(8 bits) from beginning.
     *
     * @throws BufferOverflowException have not enough bit to get.
     */
    public byte getByte() {
        return getByte(8);
    }

    /**
     * Get fixed count bits from beginning into one byte. So length can't lager than 8.
     * <p/>
     * Example:<br/>
     * buffer like "10010101 01110001", you can get 16 bits totally. First,
     * you get 5 bits, will return a byte equals 18("00010010"). Then you get
     * 8 bits, will return a byte equals 174("10101110"). Now remain bit count is 3,
     * so if you get 4 bits, will throw BufferOverflowException.
     *
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public byte getByte(int bitLength) {
        if(bitLength > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, bitLength must not larger than 8.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(bitLength == 0) {
            return 0;
        }
        if(remainingBits() < bitLength) {
            throw new BufferOverflowException();
        }
        int endIndex = positionInByte + bitLength;
        Byte secondPart = null;
        int secondPartBitLength = 0;
        int currentBytePosition = buffer.position();
        if(endIndex > 8) {// combine current and next bytes
            secondPartBitLength = endIndex - 8;
            endIndex = 8;
            secondPart = pickBitFromByte(buffer.get(currentBytePosition + 1), 0,
                    secondPartBitLength);
        }
        byte bitData = pickBitFromByte(buffer.get(currentBytePosition), positionInByte,
                endIndex);
        if(secondPart != null) {
            bitData = (byte)((bitData << secondPartBitLength) | secondPart);
        }
        if(endIndex == 8) {
            buffer.position(currentBytePosition + 1);
            positionInByte = secondPartBitLength;
        } else {
            positionInByte = endIndex;
        }
        return bitData;
    }

    /**
     * Get fixed count bits from startBitIndex into one byte. So length can't lager than 8.
     * <p/>
     * Example:<br/>
     * buffer like "10010101 01110001".
     * If startBitIndex is 2, bitLength is 8, will return a byte equals 85("01010101").
     *
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public byte getByte(int startBitIndex, int bitLength) {
        if(bitLength > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, bitLength must not larger than 8.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(bitLength == 0) {
            return 0;
        }
        if(remainingBits() < bitLength) {
            throw new BufferOverflowException();
        }
        int startByte = startBitIndex >>> 3;
        int positionInByte = startBitIndex & 0x07;
        int endIndex = positionInByte + bitLength;
        Byte secondPart = null;
        int secondPartBitLength = 0;
        if(endIndex > 8) {// combine current and next bytes
            secondPartBitLength = endIndex - 8;
            endIndex = 8;
            secondPart =
                    pickBitFromByte(buffer.get(startByte + 1), 0, secondPartBitLength);
        }
        byte bitData = pickBitFromByte(buffer.get(startByte), positionInByte, endIndex);
        if(secondPart != null) {
            bitData = (byte)((bitData << secondPartBitLength) | secondPart);
        }
        return bitData;
    }

    /**
     * Get fixed count bits from startBitIndex into byte array. The byte array length
     * rest with bitLength.
     * <p/>
     * Example:<br/>
     * buffer like "10010101 01110001".
     * If startBitIndex is 2, bitLength is 14, will return 2 bytes,
     * equals {85,49}, ("01010101 00110001").
     *
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public byte[] getBytes(int startBitIndex, int bitLength) {
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(bitLength == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[(bitLength + 7) >>> 3];

        if(bitLength <= 8) {
            bytes[0] = getByte(startBitIndex, bitLength);
            return bytes;
        }
        if(remainingBits() < bitLength) {
            throw new BufferOverflowException();
        }

        int bitLengthInFirstByte = (bitLength & 0x07) == 0 ? 8 : (bitLength & 0x07);
        bytes[0] = getByte(startBitIndex, bitLengthInFirstByte);
        startBitIndex += bitLengthInFirstByte;
        for(int i = 1; i < bytes.length; i++) {
            bytes[i] = getByte(startBitIndex, 8);
            startBitIndex += 8;
        }
        return bytes;
    }

    /**
     * Get fixed count bits from beginning into byte array. The byte array length
     * rest with bitLength.
     * <p/>
     * Example:<br/>
     * buffer like "10010101 01110001".
     * If bitLength is 14, will return 2 bytes,
     * equals {149,28}, ("10010101 00011100").
     *
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public byte[] getBytes(int bitLength) {
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        byte[] bytes = new byte[(bitLength + 7) >>> 3];

        if(bitLength <= 8) {
            bytes[0] = getByte(bitLength);
            return bytes;
        }

        int bitLengthInFirstByte = (bitLength & 0x07) == 0 ? 8 : (bitLength & 0x07);
        bytes[0] = getByte(bitLengthInFirstByte);
        for(int i = 1; i < bytes.length; i++) {
            bytes[i] = getByte();
        }
        return bytes;
    }

    private static byte pickBitFromByte(byte fromByte, int startBit, int endBit) {
        int rightMove = 8 + startBit - endBit;
        return (byte)((((fromByte << startBit) & 0xff) >>> rightMove));
    }

    /**
     * Put one byte into buffer.
     *
     * @return Current buffer.
     * @throws BufferOverflowException have not enough bit to get.
     */
    public BitBuffer put(byte data) {
        return put(data, 8);
    }

    /**
     * Put fixed bit count into buffer. Because of put one byte,
     * so bitLength can't larger than 8 or be negative.
     * <p/>
     * Example:<br/>
     * data=6 (110)
     * if bitLength=3, will put 110, first byte is "11000000".
     * if bitLength=4, will put 0110, first byte is "01100000".
     * if bitLength=2, will put 10, first byte is "10000000".
     * <br/> If current byte is "11000000", put data=6,
     * if bitLength=3, will put 110, first byte is "11011000".
     * if bitLength=8, will put 00000110, 2 bytes is "11000000 11000000".
     * But if buffer bits capacity is 11, current bit count is 11, if you put
     * another data, will throw BufferOverflowException.
     *
     * @return Current buffer.
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public BitBuffer put(byte data, int bitLength) {
        if(bitLength > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, bitLength must not larger than 8.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(bitLength == 0) {
            return this;
        }
        if(buffer.remaining() == 0 || remainingBits() < bitLength) {
            throw new BufferOverflowException();
        }
        byte firstPartBits;
        int firstPartBitLength = bitLength;
        Byte secondPartBits = null;
        int secondPartBitLength = (positionInByte + bitLength) - 8;
        if(secondPartBitLength > 0) {
            secondPartBits = pickBitsFromRightPartOfByte(data, secondPartBitLength);
            firstPartBitLength = 8 - positionInByte;
            firstPartBits = pickBitsFromLeftPartOfByte(data, firstPartBitLength);
        } else {
            firstPartBits = pickBitsFromRightPartOfByte(data, bitLength);
        }
        int currentBytePosition = buffer.position();
        byte currentByte = buffer.get(currentBytePosition);
        int fillZeroCover = ~(getCoverToPickBitsInByteRight(firstPartBitLength) <<
                (8 - positionInByte - firstPartBitLength));
        currentByte = (byte)((currentByte & fillZeroCover) |
                (firstPartBits << (8 - positionInByte - firstPartBitLength)));
        buffer.put(currentBytePosition, currentByte);
        positionInByte += firstPartBitLength;

        if(secondPartBits != null) {
            int nextBytePosition = currentBytePosition + 1;
            byte nextByte = buffer.get(nextBytePosition);
            fillZeroCover = ~(getCoverToPickBitsInByteRight(secondPartBits) <<
                    (8 - secondPartBitLength));
            nextByte = (byte)((nextByte & fillZeroCover) |
                    (secondPartBits << (8 - secondPartBitLength)));
            buffer.put(nextBytePosition, nextByte);
            buffer.position(nextBytePosition);
            positionInByte = secondPartBitLength;
        }
        return this;
    }

    /**
     * Put fixed bit count into buffer, start put position is putBitPosition.
     * Because of put one byte, so bitLength can't larger than 8 or be negative.
     * This will not change position.
     * <p/>
     * Example:<br/>
     * If origin buffer is "10000001",
     * data=6 (110)
     * if bitLength=3, putBitPosition=3, buffer data is "10110001".
     * <br/> If current byte is "11000000", put data=6,
     * if bitLength=3, will put 110, first byte is "11011000".
     *
     * @return Current buffer.
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public BitBuffer put(byte data, int putBitPosition, int bitLength) {
        if(bitLength > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, bitLength must not larger than 8.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(bitLength == 0) {
            return this;
        }
        int currentBytePosition = putBitPosition >>> 3;
        int positionInByte = putBitPosition & 0x07;
        if(buffer.limit() <= currentBytePosition ||
                remainingBits(putBitPosition) < bitLength) {
            throw new BufferOverflowException();
        }
        byte firstPartBits;
        int firstPartBitLength = bitLength;
        Byte secondPartBits = null;
        int secondPartBitLength = (positionInByte + bitLength) - 8;
        if(secondPartBitLength > 0) {
            secondPartBits = pickBitsFromRightPartOfByte(data, secondPartBitLength);
            firstPartBitLength = 8 - positionInByte;
            firstPartBits = pickBitsFromLeftPartOfByte(data, firstPartBitLength);
        } else {
            firstPartBits = pickBitsFromRightPartOfByte(data, bitLength);
        }

        byte currentByte = buffer.get(currentBytePosition);
        int fillZeroCover = ~(getCoverToPickBitsInByteRight(firstPartBitLength) <<
                (8 - positionInByte - firstPartBitLength));
        currentByte = (byte)((currentByte & fillZeroCover) |
                (firstPartBits << (8 - positionInByte - firstPartBitLength)));
        buffer.put(currentBytePosition, currentByte);

        if(secondPartBits != null) {
            int nextBytePosition = currentBytePosition + 1;
            byte nextByte = buffer.get(nextBytePosition);
            fillZeroCover = ~(getCoverToPickBitsInByteRight(secondPartBits) <<
                    (8 - secondPartBitLength));
            nextByte = (byte)((nextByte & fillZeroCover) |
                    (secondPartBits << (8 - secondPartBitLength)));
            buffer.put(nextBytePosition, nextByte);
        }
        return this;
    }

    /**
     * Put bits with byte array into buffer.
     *
     * @return Current buffer.
     * @throws BufferOverflowException have not enough bit to get.
     */
    public BitBuffer put(byte[] data) {
        return put(data, data.length << 3);
    }

    /**
     * Put fixed bit count into buffer, from byte left(11111111 11000000 count 10
     * will use 11111111 11, not 11110000 00). Because of put bytes,
     * so bitLength can't larger than 8*data.length or be negative.
     *
     * @return Current buffer.
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public BitBuffer put(byte[] data, int bitLength) {
        if(bitLength > data.length << 3) {
            throw new IllegalArgumentException(
                    "Bytes have " + (data.length << 3) + " bits, " +
                            "bitLength must not larger than that.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(bitLength == 0) {
            return this;
        }
        if(buffer.remaining() == 0 || remainingBits() < bitLength) {
            throw new BufferOverflowException();
        }
        int fullUsedByteLength = bitLength >>> 3;
        int lastByteUsedBitLength = bitLength & 0x07;
        for(int i = 0; i < fullUsedByteLength; i++) {
            put(data[i]);
        }
        if(lastByteUsedBitLength > 0) {
            byte bitsInLastByte = pickBitsFromLeftPartOfByte(data[fullUsedByteLength],
                    lastByteUsedBitLength);
            put(bitsInLastByte, lastByteUsedBitLength);
        }
        return this;
    }

    /**
     * Put fixed bit count into buffer, from byte left(11111111 11000000 count 10
     * will use 11111111 11, not 11110000 00), start put position is putBitPosition.
     * Because of put bytes, so bitLength can't larger than 8*data.length or be negative.
     * This will not change position.
     *
     * @return Current buffer.
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public BitBuffer put(byte[] data, int putBitPosition, int bitLength) {
        if(bitLength > data.length << 3) {
            throw new IllegalArgumentException(
                    "Bytes have " + (data.length << 3) + " bits, " +
                            "bitLength must not larger than that.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        if(putBitPosition < 0) {
            throw new IllegalArgumentException("Bit position can't be negative.");
        }
        if(bitLength == 0) {
            return this;
        }
        int bytePosition = putBitPosition >>> 3;
        if(buffer.limit() <= bytePosition || remainingBits(putBitPosition) < bitLength) {
            throw new BufferOverflowException();
        }
        int fullUsedByteLength = bitLength >>> 3;
        int lastByteUsedBitLength = bitLength & 0x07;
        for(int i = 0; i < fullUsedByteLength; i++) {
            put(data[i], putBitPosition, 8);
            putBitPosition += 8;
        }
        if(lastByteUsedBitLength > 0) {
            byte bitsInLastByte = pickBitsFromLeftPartOfByte(data[fullUsedByteLength],
                    lastByteUsedBitLength);
            put(bitsInLastByte, putBitPosition, lastByteUsedBitLength);
        }
        return this;
    }

    /**
     * Put fixed bit count of integer into buffer, from int right
     * (00000000 00000000 00000000 11000000 count 8 will use 11000000, not 00000000).
     * Because of put bytes, so bitLength can't larger than 8*data.length or be negative.
     *
     * @return Current buffer.
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public BitBuffer put(int data, int bitLength) {
        int changedData = data << (32 - bitLength);
        byte[] dataBytes = NumberHelper.to4Bytes(changedData);
        return put(dataBytes, bitLength);
    }

    /**
     * Put fixed bit count of short integer into buffer, from right
     * (00000000 11000000 count 8 will use 11000000, not 00000000).
     * Because of put bytes, so bitLength can't larger than 8*data.length or be negative.
     *
     * @return Current buffer.
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     * @throws BufferOverflowException  have not enough bit to get.
     */
    public BitBuffer put(short data, int bitLength) {
        short changedData = (short)(data << (16 - bitLength));
        byte[] dataBytes = NumberHelper.to2Bytes(changedData);
        return put(dataBytes, bitLength);
    }

    /**
     * Returns the byte array that backs this buffer.
     * <p/>
     * Example:<br/>
     * A buffer capacity is 14, filled bits are 11111111 111111,
     * will return byte[2] : 11111111 11111100.
     */
    public byte[] array() {
        int byteCount = bitTotalLength >>> 3;
        int bitsInLastByte = bitTotalLength & 0x07;
        byte[] allBytes = buffer.array();
        if(bitsInLastByte == 0) {
            allBytes = Arrays.copyOfRange(allBytes, 0, byteCount);
        } else {
            allBytes = Arrays.copyOfRange(allBytes, 0, byteCount + 1);
            allBytes[allBytes.length - 1] = (byte)(allBytes[allBytes.length - 1] &
                    getCoverToPickBitsInByteLeft(bitsInLastByte));
        }
        return allBytes;
    }

    /**
     * Returns the byte array that used in this buffer.
     * Means from 0 to position.
     * <p/>
     * Example:<br/>
     * A buffer capacity is 14, put(or get) 10 bits are 11111111 11,
     * will return byte[2] : 11111111 11000000.
     */
    public byte[] getUsedArray() {
        int bytePosition = buffer.position();
        if(positionInByte == 0) {
            return Arrays.copyOfRange(buffer.array(), 0, bytePosition);
        }
        byte[] usedArray = Arrays.copyOfRange(buffer.array(), 0, bytePosition + 1);
        usedArray[usedArray.length - 1] = (byte)(usedArray[usedArray.length - 1] &
                getCoverToPickBitsInByteLeft(positionInByte));
        return usedArray;
    }

    private int remainingBits(int fromBitPosition) {
        int bytePosition = fromBitPosition >>> 3;
        int remainingBytes = buffer.limit() - bytePosition;
        return (remainingBytes << 3) - (fromBitPosition & 0x07) - voidBitsInLastByte;
    }

    private byte pickBitsFromRightPartOfByte(byte data, int bitLength) {
        return (byte)(data & getCoverToPickBitsInByteRight(bitLength));
    }

    private byte pickBitsFromLeftPartOfByte(byte data, int bitLength) {
        return (byte)((data & getCoverToPickBitsInByteLeft(bitLength)) >>>
                (8 - bitLength));
    }

    private byte getCoverToPickBitsInByteRight(int bitLength) {
        return (byte)~(0xff << bitLength);
    }

    private byte getCoverToPickBitsInByteLeft(int bitLength) {
        return (byte)(~(0xff << bitLength) << (8 - bitLength));
    }

}
