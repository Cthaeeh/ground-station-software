package test;

import command.TelecommandUtil;
import data.Config;

import javax.xml.bind.DatatypeConverter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Created by kai on 6/27/17.
 * Testing the TeleCommand Util class
 */
class TelecommandUtilTest {

    @org.junit.jupiter.api.Test
    void insertCRC0() {
        byte[] message = {65,66,67};
        byte[] expected = {65,66,67}; //with http://crccalc.com/
        byte[] actual = TelecommandUtil.insertCRC(message,-1, Config.ByteEndianity.LITTLE_ENDIAN);
        assertArrayEquals(expected,actual);
    }

    @org.junit.jupiter.api.Test
    void insertCRC() {
        byte[] message = {65,66,67};
        byte[] expected = {0,0,65,66,67}; //with http://crccalc.com/
        byte[] crc16Bytes = DatatypeConverter.parseHexBinary("f508");
        expected[0]=crc16Bytes[0];
        expected[1]=crc16Bytes[1];
        byte[] actual = TelecommandUtil.insertCRC(message,0, Config.ByteEndianity.LITTLE_ENDIAN);
        assertArrayEquals(expected,actual);
    }

    @org.junit.jupiter.api.Test
    void insertCRC2() {
        byte[] message = {65,66,67};
        byte[] expected = {65,66,67,0,0}; //with http://crccalc.com/
        byte[] crc16Bytes = DatatypeConverter.parseHexBinary("f508");
        expected[3]=crc16Bytes[0];
        expected[4]=crc16Bytes[1];
        byte[] actual = TelecommandUtil.insertCRC(message,22, Config.ByteEndianity.LITTLE_ENDIAN);
        assertArrayEquals(expected,actual);
    }

    @org.junit.jupiter.api.Test
    void concatenateTest1() {
        byte[] A = {1,2,3};
        byte[] B = {4,5,6};
        byte[] expected = {1,2,3,4,5,6};
        assertArrayEquals(expected, TelecommandUtil.concatenate(A,B));
    }

    @org.junit.jupiter.api.Test
    void concatenateNull() {
        byte[] A = {1,2,3};
        byte[] B = null;
        byte[] expected = {1,2,3};
        assertArrayEquals(expected, TelecommandUtil.concatenate(A,B));
    }

    @org.junit.jupiter.api.Test
    void concatenateNull2() {
        byte[] A = null;
        byte[] B = null;
        byte[] expected = {};
        assertArrayEquals(expected, TelecommandUtil.concatenate(A,B));
    }
}