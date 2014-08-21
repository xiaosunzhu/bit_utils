bit_utils
=========

Utils for binary operation.
Main contains two utils: BitBuffer and BinStringHelper.

-------------------------------------------------------
### BitBuffer ###
A buffer wrapped *java.nio.ByteBuffer*, provide operate a buffer in bit unit.
Functions like ByteBuffer.

*   You can use wrap a byte array to create BitBuffer. Then you can get some bits from it.
    Simplified usage like below:

        byte[] data = { (byte)210, 50 }; // 11010010  00110010
        BitBuffer buffer = BitBuffer.wrapBytes(data);
        
        System.out.println(buffer.remainingBits()); // 16
        
        byte bit = buffer.getByte(2); // get 2 bits, return in a byte.
        // This bit is 3.
        
        System.out.println(buffer.remainingBits()); // 14
        
        bit = buffer.getByte(7,8); // This means get 8 bits from 7 position, return in a bytes.
        // This bit is 25(00011001).
        
        System.out.println(buffer.remainingBits()); // 14, not change.
        
        // or  byte[] bits = buffer.getBytes(12); // This means get 12 bits, return in 2 bytes.
        // This bits is {210, 3}

*   You can use allocate fix capacity to create empty BitBuffer. Then you can put some bits in it.
    Simplified usage like below:
    
        byte data = (byte)9; // 1001
        BitBuffer buffer = BitBuffer.allocate(20);
        buffer.put(data, 4); // now buffer is 1001

        System.out.println(buffer.remainingBits()); // 16
        
        byte[] datas = new byte[]{ (byte)210, 89 }; // 11010010 01011001
        buffer.put(datas, 15); // This means put 15 bits into buffer, buffer is 10011101 00100101 100
        
        System.out.println(buffer.remainingBits()); // 1
        
        buffer.put(data, 2, 4); // This means put 4 bits into buffer, start position is 2
        // now buffer is 10100101 00100101 100
        
        System.out.println(buffer.remainingBits()); // 1, not change.
        
        buffer.flip();
        
        System.out.println(buffer.remainingBits()); // 19

More usage in doc and test.

-------------------------------------------------------
### PickBitsHelper ###
A simple tool to pick some bits from number.

Example: pickBitsPartOfByte(data, 2, 5);
If data is 10010011(binary), will pick bits 01001(binary), return byte is 0x09.

-------------------------------------------------------
### BinStringHelper ###
A simple tool, provide some static function to change number(byte,byte[],int,short,long) to a binary string.
Usually used to print for debug.

Example:

- byte 210 -> "11010010 "
- int -1 -> "11111111 11111111 11111111 11111111 "
