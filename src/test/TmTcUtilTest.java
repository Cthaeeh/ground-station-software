package test;

import data.Config;
import serial.TmTcUtil;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kai on 6/27/17.
 * Testing the TM T Util class
 * The TmTcUtil Class is something like a wrapper for the CRC16 class.
 * Its tested here because there can a lot of stuff go wrong with CRC stuff.
 * Website for checking the CRCs: http://crccalc.com/
 */
class TmTcUtilTest {

    @org.junit.jupiter.api.Test
    void insertCRC0() {
        byte[] message = {65, 66, 67};
        byte[] expected = {65, 66, 67}; //with http://crccalc.com/
        byte[] actual = TmTcUtil.insertCRC(message, -1, Config.ByteEndianity.LITTLE_ENDIAN);
        assertArrayEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void insertCRC() {
        byte[] message = {65, 66, 67};
        byte[] expected = {0, 0, 65, 66, 67}; //with http://crccalc.com/
        byte[] crc16Bytes = DatatypeConverter.parseHexBinary("f508");
        expected[0] = crc16Bytes[0];
        expected[1] = crc16Bytes[1];
        byte[] actual = TmTcUtil.insertCRC(message, 0, Config.ByteEndianity.LITTLE_ENDIAN);
        assertArrayEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void insertCRC2() {
        byte[] message = {65, 66, 67};
        byte[] expected = {65, 66, 67, 0, 0}; //with http://crccalc.com/
        byte[] crc16Bytes = DatatypeConverter.parseHexBinary("f508");
        expected[3] = crc16Bytes[0];
        expected[4] = crc16Bytes[1];
        byte[] actual = TmTcUtil.insertCRC(message, 22, Config.ByteEndianity.LITTLE_ENDIAN);
        assertArrayEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void isCrcValid1() {
        byte[] message = {65, 66, 67, -11, 8};  //Message with correct CRC -16 from crc calc website
        boolean valid = TmTcUtil.isCrcValid(message, 3);
        assertEquals(true, valid, "Message was invalid, but should be valid");
    }

    @org.junit.jupiter.api.Test
    void isCrcValid2() {
        byte[] message = getValidMessage("Hallo was geht ab ?", 0);  //Message with correct CRC -16 at the beginning.
        boolean valid = TmTcUtil.isCrcValid(message, 0);
        assertEquals(true, valid, "Message was invalid, but should be valid");
    }

    @org.junit.jupiter.api.Test
    void isCrcValid3() {
        for (int i = 0; i < 100; i++) {
            byte[] message = getValidMessage("Hallo was geht ab ?", i);  //Message with correct CRC -16 at pos i.
            boolean valid = TmTcUtil.isCrcValid(message, i);
            assertEquals(true, valid, "Message was invalid, but should be valid CRC POS :" + i);
        }
    }

    @org.junit.jupiter.api.Test
    void isCrcValid4() {
        for (int i = 0; i < 100; i++) {
            byte[] message = getValidMessage("Hallo was geht ab ?", i);  //Message with correct CRC -16 at pos i.
            message[5]= (byte) (message[5]+2);  //Make it incorrect
            boolean valid = TmTcUtil.isCrcValid(message, i);
            assertEquals(false, valid, "Message was valid, but should be invalid CRC POS :" + i);
        }
    }

    @org.junit.jupiter.api.Test
    void concatenateTest1() {
        byte[] A = {1, 2, 3};
        byte[] B = {4, 5, 6};
        byte[] expected = {1, 2, 3, 4, 5, 6};
        assertArrayEquals(expected, TmTcUtil.concatenate(A, B));
    }

    @org.junit.jupiter.api.Test
    void concatenateNull() {
        byte[] A = {1, 2, 3};
        byte[] B = null;
        byte[] expected = {1, 2, 3};
        assertArrayEquals(expected, TmTcUtil.concatenate(A, B));
    }

    @org.junit.jupiter.api.Test
    void concatenateNull2() {
        byte[] A = null;
        byte[] B = null;
        byte[] expected = {};
        assertArrayEquals(expected, TmTcUtil.concatenate(A, B));
    }

    /**
     * Use the programs own way of creating CRC16 legit messages.
     * U can specify where the CRC is inserted.
     * @param s the message as ascii string
     * @param crcPos where the crc should be inserted
     * @return message with crc.
     */
    byte[] getValidMessage(String s, int crcPos) {
        byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);
        return TmTcUtil.insertCRC(bytes, crcPos, Config.ByteEndianity.LITTLE_ENDIAN);
    }
}