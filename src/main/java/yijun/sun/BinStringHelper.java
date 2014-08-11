package yijun.sun;


/**
 * Get binary string from numbers(or byte array).
 * String pattern like "10101010 10101010".
 *
 * @author SunYiJun
 */
public final class BinStringHelper {

    public static String bytesToString(byte... bytes) {
        StringBuilder builder = new StringBuilder();
        for(byte aByte : bytes) {
            String binStr = Integer.toBinaryString(aByte);
            int length = binStr.length();
            if(length > 8) {
                builder.append(binStr.substring(length - 8)).append(" ");
            } else {
                builder.append(String.format("%08d", Integer.parseInt(binStr)))
                        .append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * To 8 bytes.
     */
    public static String LongToString(long longNum) {
        byte[] bytes = NumberHelper.to8Bytes(longNum);
        return bytesToString(bytes);
    }

    /**
     * To 4 bytes.
     */
    public static String IntToString(int integer) {
        byte[] bytes = NumberHelper.to4Bytes(integer);
        return bytesToString(bytes);
    }

    /**
     * To 2 bytes.
     */
    public static String ShortToString(short shortNum) {
        byte[] bytes = NumberHelper.to2Bytes(shortNum);
        return bytesToString(bytes);
    }

    public static byte[] toByteArray(String binString) {
        if(binString == null) {
            return null;
        }
        binString = binString.replaceAll(" ", "");
        if(binString.isEmpty()) {
            return new byte[0];
        }
        int length = binString.length();
        int bytesCount = (length + 7) >>> 3;
        int firstByteStringLength = length & 0x07;
        byte[] bytes = new byte[bytesCount];
        if(firstByteStringLength != 0) {
            bytes[0] = (byte)Integer
                    .parseInt(binString.substring(0, firstByteStringLength), 2);
        } else {
            bytes[0] = (byte)Integer.parseInt(binString.substring(0, 8), 2);
            firstByteStringLength = 8;
        }
        int startIndex = firstByteStringLength;
        for(int i = 1; i < bytesCount; i++) {
            bytes[i] = (byte)Integer
                    .parseInt(binString.substring(startIndex, startIndex + 8), 2);
            startIndex += 8;
        }
        return bytes;
    }
}
