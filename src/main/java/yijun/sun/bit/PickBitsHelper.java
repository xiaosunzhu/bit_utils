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


/**
 * Pick some bits from number tool.
 *
 * @author SunYiJun
 */
public class PickBitsHelper {

    /**
     * Example: 10010011
     * <br/>
     * Pick 3 bits is 00000011.<br/>
     * Pick 5 bits is 00010011.<br/>
     *
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     */
    public static byte pickBitsFromRightPartOfByte(byte data, int bitLength) {
        if(bitLength > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, bitLength must not larger than 8.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        return (byte)(data & getCoverToPickBitsInByteRight(bitLength));
    }

    /**
     * Example: 10010011
     * <br/>
     * Pick 3 bits is 00000100.<br/>
     * Pick 5 bits is 00010010.<br/>
     *
     * @throws IllegalArgumentException bitLength can't lager than 8 or be negative.
     */
    public static byte pickBitsFromLeftPartOfByte(byte data, int bitLength) {
        if(bitLength > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, bitLength must not larger than 8.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        return pickBitsPartOfByte(data, 0, bitLength);
    }

    /**
     * Example: 10010011
     * <br/>
     * Pick 3 bits from 1 is 00000001.<br/>
     * Pick 5 bits from 2 is 00001001.<br/>
     *
     * @throws IllegalArgumentException fromBit+bitLength can't lager than 8.
     *                                  BitLength and fromBit can't be negative.
     */
    public static byte pickBitsPartOfByte(byte data, int fromBit, int bitLength) {
        if((fromBit + bitLength) > 8) {
            throw new IllegalArgumentException(
                    "One byte have 8 bit, fromBit+bitLength must not larger than 8.");
        }
        if(fromBit < 0) {
            throw new IllegalArgumentException("From bit can't be negative.");
        }
        if(bitLength < 0) {
            throw new IllegalArgumentException("Length can't be negative.");
        }
        return pickBitsFromByte(data, fromBit, fromBit + bitLength);
    }

    static byte pickBitsFromByte(byte fromByte, int startBit, int endBit) {
        int rightMove = 8 + startBit - endBit;
        return (byte)((((fromByte << startBit) & 0xff) >>> rightMove));
    }

    static byte getCoverToPickBitsInByteRight(int bitLength) {
        return (byte)~(0xff << bitLength);
    }

    static byte getCoverToPickBitsInByteLeft(int bitLength) {
        return (byte)(~(0xff << bitLength) << (8 - bitLength));
    }
}
