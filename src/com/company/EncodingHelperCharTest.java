package com.company;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Thomas Redding & Ben Wedin on 4/4/15.
 */

public class EncodingHelperCharTest {

    private EncodingHelperChar objd, objd2, objN, objOwithTilde, objGold,
            objUnknown, objControl;

    @Before
    public void setUp() throws Exception {
        objd = new EncodingHelperChar('d');
        objd2 = new EncodingHelperChar('d');
        objN = new EncodingHelperChar(78);
        byte input[] = new byte[] {(byte) 0xc3, (byte) 0x95};
        objOwithTilde = new EncodingHelperChar(input);
        objGold = new EncodingHelperChar(128794);
        objUnknown = new EncodingHelperChar(180727);
        objControl = new EncodingHelperChar(7);
    }

    @Test
    public void testConstructor_ShouldBeDeterministic() throws Exception {
        assertEquals(objd.getCodePoint(), objd2.getCodePoint());
    }

    @Test(expected= IllegalArgumentException.class)
    public void testConstructor_ShouldThrowExceptionForNegativeIntegers() throws Exception {
        EncodingHelperChar objError = new EncodingHelperChar(-512);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testConstructor_ShouldThrowExceptionForLargeIntegers() throws Exception {
        EncodingHelperChar objError = new EncodingHelperChar(324789512);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testConstructor_ShouldThrowExceptionForEmptyByteArray() throws Exception {
        byte input[] = new byte[] {};
        EncodingHelperChar objError = new EncodingHelperChar(input);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testConstructor_ShouldThrowExceptionForIllegalByteArray() throws Exception {
        byte input[] = new byte[] {(byte) 123, (byte) 72, (byte) 152, (byte) 99, (byte) 211};
        EncodingHelperChar objError = new EncodingHelperChar(input);
    }



    @Test
    public void testGetCodePoint_ShouldCorrectlyReportASCIIValues() throws Exception {
        assertEquals(78, objN.getCodePoint());
    }

    @Test
    public void testGetCodePoint_ShouldCorrectlyReportNonASCIIValues() throws Exception {
        assertEquals(213, objOwithTilde.getCodePoint());
    }

    @Test
    public void testSetCodePoint_ShouldNotChangeIfNewCodepointIsTooLarge() throws Exception {
        int newCodepoint = 108923701;
        objN.setCodePoint(newCodepoint);
        assertEquals(78, objN.getCodePoint());
    }

    @Test
    public void testSetCodePoint_ShouldChangeIfEverythingWorks() throws Exception {
        int newCodepoint = 99;
        objd.setCodePoint(newCodepoint);
        assertEquals(99, objd.getCodePoint());
    }

    @Test
    public void testSetCodePoint_ShouldNotChangeIfNewCodepointIsTooSmall() throws Exception {
        int newCodepoint = -19038247;
        objOwithTilde.setCodePoint(newCodepoint);
        assertEquals(213, objOwithTilde.getCodePoint());
    }

    @Test
    public void testToUtf8Bytes_ShouldTranslateASCII() throws Exception {
        byte correctAnswer[] = new byte[] {(byte) 78};
        assertArrayEquals(correctAnswer, objN.toUtf8Bytes());
    }

    @Test
    public void testToUtf8Bytes_ShouldTranslateNonASCII() throws Exception {
        byte correctAnswer[] = new byte[] {(byte) 195, (byte) 149};
        assertArrayEquals(correctAnswer, objOwithTilde.toUtf8Bytes());
    }

    @Test
    public void testToCodePointString_ShouldTranslateASCII() throws Exception {
        assertEquals("U+004E", objN.toCodePointString());
    }

    @Test
    public void testToCodePointString_ShouldTranslateNonASCII() throws Exception {
        assertEquals("U+00D5", objOwithTilde.toCodePointString());
    }

    @Test
    public void testToCodePointString_ShouldTranslateFourByte() throws Exception {
        assertEquals("U+1F71A", objGold.toCodePointString());
    }

    @Test
    public void testToUtf8String_ShouldTranslateASCII() throws Exception {
        assertEquals("\\x4E", objN.toUtf8String());
    }

    @Test
    public void testToUtf8String_ShouldTranslateNonASCII() throws Exception {
        assertEquals("\\xF0\\x9F\\x9C\\x9A", objGold.toUtf8String());
    }

    @Test
    public void testGetCharacterName_ShouldNameASCII() throws Exception {
        assertEquals("LATIN CAPITAL LETTER N", objN.getCharacterName());
    }

    @Test
    public void testGetCharacterName_ShouldNameNonASCII() throws Exception {
        assertEquals("ALCHEMICAL SYMBOL FOR GOLD", objGold.getCharacterName());
    }

    @Test
    public void getCharacterName_ShouldReportUndefinedCodePoints()
            throws Exception {
        assertEquals("<unknown> U+2C1F7", objUnknown.getCharacterName());
    }

    @Test
    public void getCharacterName_ShouldReportControlsWell() throws Exception {
        assertEquals("<control> BELL", objControl.getCharacterName());
    }
}