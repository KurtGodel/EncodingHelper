package com.company;

import java.util.*;
import java.io.*;
import java.lang.IllegalStateException;
import java.io.FileNotFoundException;

/**
 * The main model class for the EncodingHelper project in
 * CS 257, Spring 2015, Carleton College. Each object of this class
 * represents a single Unicode character. The class's methods
 * generate various representations of the character. 
 */

/**
 * To be implemented by Thomas Redding & Ben Wedin well after 4/4/15.
 */

public class EncodingHelperChar {
    private int NUMBEROFCODEPOINTS = 1114112;
    private int codePoint;

    /*
     * The variable "descriptionHashTable" is a hashtable of code point
     * descriptions. Each key (a code point) is paired with its description.
     */
    private static Hashtable<Integer, String> descriptionHashTable;


    public EncodingHelperChar(int codePoint) {
        if (codePoint < 0 || codePoint >= NUMBEROFCODEPOINTS) {
            throw new IllegalArgumentException("Illegal codepoint.");
        }
        this.codePoint = codePoint;
        if(this.descriptionHashTable == null) {
            createDescriptionHashTableFromUnicodeDataFile();
        }
    }

    public EncodingHelperChar(byte[] utf8Bytes) {
        if(utf8Bytes.length == 0) {
            throw  new IllegalArgumentException
                    ("Empty byte array passed to constructor.");
        }
        else if((byte) (utf8Bytes[0] & 0b10000000) == (byte) 0b00000000) {
            // utf8Bytes should have length 1
            // 0xxxxxxx
            if(utf8Bytes.length != 1) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            this.codePoint = (int) utf8Bytes[0];
        }
        else if((byte) (utf8Bytes[0] & 0b11100000) == (byte) 0b11000000) {
            // utf8Bytes should have length 2
            // 110xxxxx	10xxxxxx
            if(utf8Bytes.length != 2) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            if((byte) (utf8Bytes[1] & 0b11000000) != (byte) 0b10000000) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            byte firstByte = (byte) (utf8Bytes[0] & 0b00011111);
            byte secondByte = (byte) (utf8Bytes[1] & 0b00111111);
            this.codePoint = 64*((int) firstByte) + ((int) secondByte);
        }
        else if((byte) (utf8Bytes[0] & 0b11110000) == (byte) 0b11100000) {
            // utf8Bytes should have length 3
            // 1110xxxx 10xxxxxx 10xxxxxx
            if(utf8Bytes.length != 3) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            if((byte) (utf8Bytes[1] & 0b11000000) != (byte) 0b10000000) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            if((byte) (utf8Bytes[2] & 0b11000000) != (byte) 0b10000000) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            byte firstByte = (byte) (utf8Bytes[0] & 0b00001111);
            byte secondByte = (byte) (utf8Bytes[1] & 0b00111111);
            byte thirdByte = (byte) (utf8Bytes[2] & 0b00111111);
            this.codePoint = 4096*((int) firstByte)
                    + 64*((int) secondByte) + ((int) thirdByte);
        }
        else if((byte) (utf8Bytes[0] & 0b11111000) == (byte) 0b11110000) {
            // utf8Bytes should have length 4
            // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            if(utf8Bytes.length != 4) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            if((byte) (utf8Bytes[1] & 0b11000000) != (byte) 0b10000000) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            if((byte) (utf8Bytes[2] & 0b11000000) != (byte) 0b10000000) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            if((byte) (utf8Bytes[3] & 0b11000000) != (byte) 0b10000000) {
                throw  new IllegalArgumentException
                        ("Illegal bytes in constructor.");
            }
            byte firstByte = (byte) (utf8Bytes[0] & 0b00000111);
            byte secondByte = (byte) (utf8Bytes[1] & 0b00111111);
            byte thirdByte = (byte) (utf8Bytes[2] & 0b00111111);
            byte fourthByte = (byte) (utf8Bytes[3] & 0b00111111);
            this.codePoint = 262144*((int) firstByte) + 4096*((int) secondByte)
                    + 64*((int) thirdByte) + ((int) fourthByte);
        }
        else {
            throw new IllegalArgumentException("Illegal bytes in Constructor.");
        }
        createDescriptionHashTableFromUnicodeDataFile();
    }
    
    public EncodingHelperChar(char ch) {
        String hexString = Integer.toHexString(ch | 0x10000).substring(1).toUpperCase();
        codePoint = hexadecimalToInteger(hexString);
        if(codePoint == -1) {
            throw new IllegalArgumentException
                    ("The given input is not a valid unicode character.");
        }
        createDescriptionHashTableFromUnicodeDataFile();
    }
    
    public int getCodePoint() {
        return this.codePoint;
    }
    
    public void setCodePoint(int newCodePoint) {
        if(newCodePoint >= 0 && newCodePoint <= NUMBEROFCODEPOINTS) {
            this.codePoint = newCodePoint;
        }
    }
    
    /**
     * Converts this character into an array of the bytes in its UTF-8
     * representation.
     *   For example, if this character is a lower-case letter e with an acute
     * accent, whose UTF-8 form consists of the two-byte sequence C3 A9, then
     * this method returns a byte[] of length 2 containing C3 and A9.
     * 
     * @return the UTF-8 byte array for this character
     */
    public byte[] toUtf8Bytes() {
        byte utfBytes[];
        int byte1, byte2, byte3, byte4, i;
        int codepointCopy = this.codePoint;
        if (this.codePoint < 0) {
            // Constructor should not allow for negative code points
            throw new IllegalStateException("code point is negative");
        }
        else if (0 <= this.codePoint && this.codePoint <= 127) {
            // 1 byte
            utfBytes = new byte[] {(byte) this.codePoint};
            return utfBytes;
        }
        // Constructs UTF-8 encoding by shifting bits of code point
        else if (128 <= this.codePoint && this.codePoint <= 2047) {
            // 2 bytes
            byte leadingOnes = (byte) 0xC0;
            byte endMask = (byte) 0x3F;
            byte oneZeroMask = (byte) 0x80;
            byte1 = (byte) (leadingOnes | (this.codePoint >> 6));
            byte2 = (byte) (oneZeroMask | (this.codePoint & endMask));
            utfBytes = new byte[] {(byte) byte1, (byte) byte2};
            return utfBytes;
        }
        else if (2048 <= this.codePoint && this.codePoint <= 65535) {
            // 3 bytes
            byte leadingOnes = (byte) 0xE0;
            byte endMask = (byte) 0x3F;
            byte oneZeroMask = (byte) 0x80;
            byte1 = (byte) (leadingOnes | (this.codePoint >> 12));
            byte2 = (byte) (oneZeroMask | (this.codePoint >> 6 & endMask));
            byte3 = (byte) (oneZeroMask | (this.codePoint & endMask));
            utfBytes = new byte[] {(byte) byte1, (byte) byte2, (byte) byte3};
            return utfBytes;
        }
        else if (65536 <= this.codePoint && this.codePoint <= 2097151) {
            // 4 bytes
            byte leadingOnes = (byte) 0xF0;
            byte endMask = (byte) 0x3F;
            byte oneZeroMask = (byte) 0x80;
            byte1 = (byte) (leadingOnes | (this.codePoint >> 18));
            byte2 = (byte) (oneZeroMask | (this.codePoint >> 12 & endMask));
            byte3 = (byte) (oneZeroMask | (this.codePoint >> 6 & endMask));
            byte4 = (byte) (oneZeroMask | (this.codePoint & endMask));
            utfBytes = new byte[]
                    {(byte) byte1, (byte) byte2, (byte) byte3, (byte) byte4};
            return utfBytes;
        }
        else if (2097152 <= this.codePoint) {
            // Constructor should not allow for code points this large
            throw new IllegalStateException("code point is too large");
        }
        return null;
    }
    
    /**
     * Generates the conventional 4-digit hexadecimal code point notation for
     * this character.
     *   For example, if this character is a lower-case letter e with an acute
     * accent, then this method returns the string U+00E9 (no quotation marks in
     * the returned String).
     *
     * @return the U+ string for this character
     */
    public String toCodePointString() {
        String hexString = Integer.toHexString(this.codePoint).toUpperCase();
        if(hexString.length() == 1)
            return "U+000"+hexString;
        else if(hexString.length() == 2)
            return "U+00"+hexString;
        else if(hexString.length() == 3)
            return "U+0"+hexString;
        else
            return "U+"+hexString;

    }
    
    /**
     * Generates a hexadecimal representation of this character suitable for
     * pasting into a string literal in languages that support hexadecimal byte
     * escape sequences in string literals (e.g. C, C++, and Python).
     *   For example, if this character is a lower-case letter e with an acute
     * accent (U+00E9), then this method returns the string \xC3\xA9. Note that
     * quotation marks should not be included in the returned String.
     *
     * @return the escaped hexadecimal byte string
     */
    public String toUtf8String() {
        String output = "";
        byte[] bytes = toUtf8Bytes();
        for(int i=0; i<bytes.length; i++) {
            output += "\\x";
            String stringToAdd = byteToHexadecimal(bytes[i]);
            if(stringToAdd == "") {
                throw new IllegalStateException
                        ("Failed to convert from code point to UTF-8 string");
            }
            output += stringToAdd;
        }
        return output;
    }

    /*
     * Converts a single byte into a 2-character hexadecimal string.
     * If the conversion fails, it returns an empty string.
     *
     * @param input the byte to be converted into hexadecimal
     * @return the 2-character hexadecimal string version of the byte
     */
    private String byteToHexadecimal(byte input) {
        int inputAsInteger = (int) input;

        // int is signed, but we want our hexadecimal representation to be
        // unsigned, so we have to add 256 if the input is negative
        if(input < 0) {
            inputAsInteger += 256;
        }

        int sixteensPlace = inputAsInteger/16;
        int onesPlace = inputAsInteger%16;
        String outputString = nibbleIntegerToHexadecimal(sixteensPlace)
                + nibbleIntegerToHexadecimal(onesPlace);
        if(outputString.length() == 2) {
            return outputString;
        }
        return "";
    }

    /*
     * Converts an integer from 0-15 into a hexadecimal string of length 1.
     * If this fails, it returns an empty string.
     *
     * @param input the nibble to be converted into hexadecimal
     * @return the 1-character hexadecimal string version of the nibble
     */
    private static String nibbleIntegerToHexadecimal(int input) {
        if(input == 0)
            return "0";
        else if(input == 1)
            return "1";
        else if(input == 2)
            return "2";
        else if(input == 3)
            return "3";
        else if(input == 4)
            return "4";
        else if(input == 5)
            return "5";
        else if(input == 6)
            return "6";
        else if(input == 7)
            return "7";
        else if(input == 8)
            return "8";
        else if(input == 9)
            return "9";
        else if(input == 10)
            return "A";
        else if(input == 11)
            return "B";
        else if(input == 12)
            return "C";
        else if(input == 13)
            return "D";
        else if(input == 14)
            return "E";
        else if(input == 15)
            return "F";
        else
            return "";
    }
    
    /**
     * Generates the official Unicode name for this character.
     *   For example, if this character is a lower-case letter e with an acute
     * accent, then this method returns the string "LATIN SMALL LETTER E WITH
     * ACUTE" (without quotation marks).
     *
     * @return this character's Unicode name
     */
    public String getCharacterName() {
        String name = this.descriptionHashTable.get(this.codePoint);
        if(name == null) {
            return "<unknown> U+" + integerToHexadecimal(this.codePoint);
        }
        return name;
    }


    /**
     * Populates "descriptionHashTable" from the CharacterTable.txt file
     */
    private void createDescriptionHashTableFromUnicodeDataFile() {
        this.descriptionHashTable = new Hashtable<Integer, String>();

        // Open CharacterTable.txt
        String pathName = System.getProperty("user.dir");
        pathName += "/src/com/company/CharacterTable.txt";
        File file = new File(pathName);
        try {
            int currentLineNumber = 0;
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                // Iterate throw each line in CharacterTable.txt.

                String codePointString = "";
                String descriptionString = "";
                String line = input.nextLine();

                if(currentLineNumber < 32) {
                    // get descriptions of control code points
                    int i;
                    for (i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ';') {
                            codePointString  = line.substring(0, i);
                            break;
                        }
                    }
                    for(i = 0; i < line.length()-3; i++) {
                        if(line.substring(i, i+3).equals(";N;")) {
                            descriptionString = "<control> "
                                    + line.substring(i+3, line.length()-4);
                            break;
                        }
                    }
                }
                else {
                    // get descriptions of non-control code points
                    int i;
                    int lastSemicolon = -1;
                    for (i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ';') {
                            if (lastSemicolon == -1) {
                                codePointString  = line.substring(0, i);
                                lastSemicolon = i;
                            } else {
                                descriptionString =
                                        line.substring(lastSemicolon+1, i);
                                break;
                            }
                        }
                    }
                }

                if (codePointString.equals("") ||
                        descriptionString.equals("")) {
                    // If we can't find the code point string or the,
                    // description, then we throw an exception
                    throw new IllegalArgumentException
                            ("CharacterTable.txt contains errors.");
                }

                int unicodeValue = hexadecimalToInteger(codePointString);

                if(unicodeValue == -1) {
                    // If there was a problem converting the code point into
                    // hexadecimal, throw an error
                    throw new IllegalArgumentException
                            ("CharacterTable.txt contains errors.");
                }
                else {
                    this.descriptionHashTable.put
                            (unicodeValue, "" + descriptionString);
                }
                currentLineNumber++;
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Converts a hexadecimal string into an integer. If the conversion fails,
     * -1 is returned. Beware, integers will loop around past 2^31 - they will
     * NOT return -1
     *
     * @param input the hexadecimal string to be converted
     * @return the integer value of the hexadecimal string
     */
    public static int hexadecimalToInteger(String input) {
        int output = 0;
        for(int i=0; i<input.length(); i++) {
            int characterDecimalValue =
                    hexadecimalCharacterToInteger(input.charAt(i));
            if(characterDecimalValue == -1) {
                return -1;
            }
            output += characterDecimalValue * Math.pow(16, input.length()-i-1);
        }
        return output;
    }

    /*
     * Converts a single hexadecimal character into an integer between 0 and 15.
     * If the given character is not 0-9 or A-F, -1 is returned
     *
     * @param the char to be converted from hexadecimal to an integer
     * @return the integer value of the hexadecimal character
     */
    private static int hexadecimalCharacterToInteger(char input) {
        if(input == '0')
            return 0;
        else if(input == '1')
            return 1;
        else if(input == '2')
            return 2;
        else if(input == '3')
            return 3;
        else if(input == '4')
            return 4;
        else if(input == '5')
            return 5;
        else if(input == '6')
            return 6;
        else if(input == '7')
            return 7;
        else if(input == '8')
            return 8;
        else if(input == '9')
            return 9;
        else if(input == 'A' || input == 'a')
            return 10;
        else if(input == 'B' || input == 'b')
            return 11;
        else if(input == 'C' || input == 'c')
            return 12;
        else if(input == 'D' || input == 'd')
            return 13;
        else if(input == 'E' || input == 'e')
            return 14;
        else if(input == 'F' || input == 'f')
            return 15;
        else
            return -1;
    }

    /*
     * Converts an integer into a hexadecimal string.
     *
     * @param input the integer to be converted into hexadecimal
     * @return the hexadecimal string representing the given integer
     */
    public static String integerToHexadecimal(int input) {
        String flippedOutput = "";
        int residual = input;
        int digit = 1;
        int digitValue;

        /*
         * First, we construct a hexadecimal string where the smallest digit is
         * on the right and the largest digit is on the left.
         */
        while(residual != 0) {
            digitValue = residual%(digit*16);
            residual -= digitValue;
            flippedOutput += nibbleIntegerToHexadecimal(digitValue/digit);
            digit *= 16;
        }

        // Now, we just flip the string's character-order.
        String output = "";
        for(int i=flippedOutput.length()-1; i>=0; i--) {
            output += flippedOutput.charAt(i);
        }
        return output;
    }
}
