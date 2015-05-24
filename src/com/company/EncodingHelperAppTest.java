package com.company;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Thomas Redding & Ben Wedin on 4/4/15.
 */

public class EncodingHelperAppTest {

    private EncodingHelperApp encodingHelperApp;

    @Before
    public void setUp() throws Exception {
        encodingHelperApp = new EncodingHelperApp();
    }

    @Test
    public void guessInputType_shouldCorrectlyGuessCodepoints() throws Exception {
        String codepoints = "U+0048 U+AC10 U+FF92";
        String guess = EncodingHelperApp.guessInputType(codepoints);
        assertEquals("codepoint", guess);
    }

    @Test
    public void guessInputType_shouldCorrectlyRejectObviouslyBadCodepoints() throws Exception {
        String codepoints = "U+0048 U+AC10 U+FF92 U+82";
        String guess = EncodingHelperApp.guessInputType(codepoints);
        assertEquals("string", guess);
    }

    @Test
    public void guessInputType_shouldCorrectlyGuessUTF8() throws Exception {
        String bytes = "\\xB5\\xC2\\x83\\xC0\\x70";
        String guess = EncodingHelperApp.guessInputType(bytes);
        assertEquals("utf8", guess);
    }

    @Test
    public void guessInputType_shouldCorrectlyRejectObviouslyBadUTF8() throws Exception {
        String bytes = "\\xB5\\xC2\\x8\\xC0\\x70";
        String guess = EncodingHelperApp.guessInputType(bytes);
        assertEquals("string", guess);
    }

    @Test
    public void testUtf8ToCharList_shouldConvertUTF8ToCharList() throws Exception {
        List<EncodingHelperChar> output = new ArrayList<EncodingHelperChar>();
        output.add(new EncodingHelperChar('π'));
        output.add(new EncodingHelperChar(' '));
        output.add(new EncodingHelperChar('i'));
        output.add(new EncodingHelperChar('s'));
        output.add(new EncodingHelperChar('\u1C60'));

        List<EncodingHelperChar> actualOutput = encodingHelperApp.utf8ToCharList(
                "\\xCF\\x80\\x20\\x69\\x73\\xE1\\xB1\\xA0");

        if(actualOutput.size() != output.size()) {
            assertEquals(false, true);
            return;
        }

        for(int i=0; i<actualOutput.size(); i++) {
            if(output.get(i).getCodePoint() != actualOutput.get(i).getCodePoint()) {
                assertEquals(false, true);
                return;
            }
        }
        assertEquals(true, true);
    }

    @Test
    public void testStringToCharList_shouldConvertStringToCharList() throws Exception {
        List<EncodingHelperChar> expectedOutput = new ArrayList<EncodingHelperChar>();
        expectedOutput.add(new EncodingHelperChar('r'));
        expectedOutput.add(new EncodingHelperChar('ü'));
        expectedOutput.add(new EncodingHelperChar('∫'));

        List<EncodingHelperChar> actualOutput = encodingHelperApp.stringToCharList("rü∫");

        if(actualOutput.size() != expectedOutput.size()) {
            assertEquals(expectedOutput.size(), actualOutput.size());
            return;
        }

        for(int i=0; i<actualOutput.size(); i++) {
            if(expectedOutput.get(i).getCodePoint() != actualOutput.get(i).getCodePoint()) {
                assertEquals(expectedOutput.get(i).getCodePoint(), actualOutput.get(i).getCodePoint());
                return;
            }
        }
        assertEquals(true, true);
    }

    @Test
    public void testCodepointsToCharList_shouldConvertCodepointsToCharList() throws Exception {
        List<EncodingHelperChar> expectedOutput = new ArrayList<EncodingHelperChar>();
        expectedOutput.add(new EncodingHelperChar('\u2020'));
        expectedOutput.add(new EncodingHelperChar('!'));
        expectedOutput.add(new EncodingHelperChar('m'));
        expectedOutput.add(new EncodingHelperChar('o'));

        List<EncodingHelperChar> actualOutput = encodingHelperApp.
                codepointsToCharList("U+2020 U+0021 U+006D U+006F");

        if(actualOutput.size() != expectedOutput.size()) {
            assertEquals(false, true);
            return;
        }

        for(int i=0; i<actualOutput.size(); i++) {
            if(expectedOutput.get(i).getCodePoint() != actualOutput.get(i).getCodePoint()) {
                assertEquals(expectedOutput.get(i).getCodePoint(), actualOutput.get(i).getCodePoint());
                return;
            }
        }
        assertEquals(true, true);
    }

    @Test
    public void testCharListToString_shouldConvertCharListToString() throws Exception {
        List<EncodingHelperChar> input = new ArrayList<EncodingHelperChar>();
        input.add(new EncodingHelperChar('\u2140'));
        input.add(new EncodingHelperChar('5'));
        input.add(new EncodingHelperChar('*'));
        input.add(new EncodingHelperChar('i'));

        assertEquals(encodingHelperApp.charListToString(input), "\u21405*i");
    }

    @Test
    public void testCharListToUtf8_shouldConvertCharListToUTF8() throws Exception {
        List<EncodingHelperChar> input = new ArrayList<EncodingHelperChar>();
        input.add(new EncodingHelperChar('₤'));
        input.add(new EncodingHelperChar('7'));
        input.add(new EncodingHelperChar('s'));
        assertEquals("\\xE2\\x82\\xA4\\x37\\x73", encodingHelperApp.charListToUtf8(input));
    }

    @Test
    public void testCharListToCodepoint_shouldConvertCharListToCodepoint() throws Exception {
        List<EncodingHelperChar> input = new ArrayList<EncodingHelperChar>();
        input.add(new EncodingHelperChar('\u2152'));
        input.add(new EncodingHelperChar('\u22A2'));
        input.add(new EncodingHelperChar('q'));
        input.add(new EncodingHelperChar('é'));

        assertEquals("U+2152 U+22A2 U+0071 U+00E9",
                encodingHelperApp.charListToCodepoint(input));
    }

    @Test
    public void test2Utf8ToCharList_shouldReturnNotingIfIllegalUTF8() throws Exception  {
        List<EncodingHelperChar> actualOutput = encodingHelperApp.utf8ToCharList("\\xE1\\XC1\\XB3");
        assertEquals(null, actualOutput);
    }

    @Test
    public void test3Utf8ToCharList_shouldPrintErrorIfIllegalUTF8() throws Exception  {
        List<EncodingHelperChar> actualOutput = encodingHelperApp.utf8ToCharList("\\xE1\\XC1\\XB3");
        assertEquals(null, actualOutput);
    }

    @Test
    public void test2CodepointsToCharList_shouldReturnNotingIfIllegalCodepoint() throws Exception {
        List<EncodingHelperChar> actualOutput = encodingHelperApp.codepointsToCharList("U+072");
        assertEquals(null, actualOutput);
    }

    @Test
    public void test3CodepointsToCharList_shouldPrintErrorIfIllegalCodepoint() throws Exception {
        List<EncodingHelperChar> actualOutput = encodingHelperApp.codepointsToCharList("U+072");
        assertEquals(null, actualOutput);
    }
}