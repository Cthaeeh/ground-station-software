package test;

import main.Encoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kai on 12.08.2017.
 */
class EncoderTest {

    @Test
    void encodeFromHex() {
        String test = "1 1 1";
        byte[] expected = {1 ,1 ,1};
        assertArrayEquals(expected,Encoder.encodeFromHex(test));
    }

    @Test
    void encodeFromHexIgnoreUndefined() {
        String test = "1 1 1 u";
        byte[] expected = {1 ,1 ,1};
        assertArrayEquals(expected,Encoder.encodeFromHex(test));
    }

    @Test
    void encodeFromHexEmptryString() {
        String test = "";
        byte[] expected = {};
        assertArrayEquals(expected,Encoder.encodeFromHex(test));
    }
}