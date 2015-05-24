package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * The main model class for the EncodingHelper project in
 * CS 257, Spring 2015, Carleton College. Each object of this class
 * represents a single Unicode character. The class's methods
 * generate various representations of the character. 
 */

/**
 * To be implemented by Thomas Redding & Ben Wedin well after 4/4/15.
 */

public class EncodingHelperApp {

    public static void main(String args[]) {
        if(args.length == 0) {
            // We were given no arguments, so we will print the usage message
            EncodingHelperApp.printUsageMessage();
            return;
        }

        /*
         Partition the arguments we are given into
            1. input flag values
            2. output flag vluaes
            3. the string for us to analyze
         */
        String in = "";
        String out = "";
        int i=0;
        if(args[i].equals("-i") || args[i].equals("--input")) {
            if (i + 1 < args.length) {
                // there is something after "-i"
                in = args[i + 1];
                i += 2;
            }
        }
        if(args[i].equals("-o") || args[i].equals("--output")) {
            if(i+1 < args.length) {
                // there is something after "-o"
                out = args[i+1];
                i += 2;
            }
        }
        if(out.equals("summary"))
            out = "";
        if((!in.equals("") && !in.equals("utf8") && !in.equals("codepoint") &&
                !in.equals("string")) || (!out.equals("") &&
                !out.equals("utf8") && !out.equals("codepoint")
                && !out.equals("string"))) {
            System.out.println("Invalid input/output specification.");
            System.out.println("");
            EncodingHelperApp.printUsageMessage();
            return;
        }
        String finalInput = args[i];
        while(i+1<args.length) {
                finalInput += " " + args[i+1];
                i += 1;
        }

        List<EncodingHelperChar> charList = null;
        // Guesses input type if no flag given
        if(in.equals(""))
            in = EncodingHelperApp.guessInputType(finalInput);

        /*
        Based on our interpretation of the users' input, we will conver the
        string they gave into a list of EncodingHelperChar objects
        */
        if(in.equals("string"))
            charList = EncodingHelperApp.stringToCharList(finalInput);
        else if(in.equals("utf8"))
            charList = EncodingHelperApp.utf8ToCharList(finalInput);
        else
            charList = EncodingHelperApp.codepointsToCharList(finalInput);

        if(charList == null)
            return;

        if(out== "") {
            if (charList.size() == 1) {
                System.out.println("Character: " +
                        EncodingHelperApp.charListToString(charList));
                System.out.println("Codepoint: " +
                        EncodingHelperApp.charListToCodepoint(charList));
                System.out.println("Name: " +
                        charList.get(0).getCharacterName());
                System.out.println("UTF8: " +
                        EncodingHelperApp.charListToUtf8(charList));
            }
            else {
                System.out.println("String: " +
                        EncodingHelperApp.charListToString(charList));
                System.out.println("Codepoints: " +
                        EncodingHelperApp.charListToCodepoint(charList));
                System.out.println("UTF8: " +
                        EncodingHelperApp.charListToUtf8(charList));
            }
        }
        else {
            String output = "";
            if (out.equals("string"))
                output = EncodingHelperApp.charListToString(charList);
            else if (out.equals("utf8"))
                output = EncodingHelperApp.charListToUtf8(charList);
            else
                output = EncodingHelperApp.charListToCodepoint(charList);
            System.out.println(output);
        }
        return;
    }

    /*
     * This method guesses what a user wanted a string to represent: a string of
     * characters, a string of code points or a string of hexadecimal UTF-8
     * values.
     * @param input the string whose semantic value we are guessing
     * @return a string ("string", "utf8", or "codepoint") representing our
     * guess for the input's correct semantic interpretation
     */
    public static String guessInputType(String input) {
        if(input.length() == 1) {
            return "string";
        }
        else if(!input.substring(0,2).equals("\\x") &&
                !input.substring(0,2).equals("U+")){
            return "string";
        }
        else if(input.substring(0,2).equals("\\x")){
            if(input.length()%4!=0){
                return "string";
            }
            int i=4;
            while(i<input.length()) {
                if(input.charAt(i) != '\\' || input.charAt(i+1) != 'x') {
                    return "string";
                }
                i+=4;
            }
            return "utf8";
        }
        else {
            // it is neccessarily the case that the first two characters in
            // input are "U+"
            String[] splitInput = input.split("\\s+");
            for(int i=0; i<splitInput.length; i++) {
                // e.g. splitInput[i] = U+04C2
                if(splitInput[i].length() < 6)
                    return "string";
                else if(splitInput[i].charAt(0) != 'U'
                        || splitInput[i].charAt(1) != '+')
                    return "string";
            }
            return "codepoint";
        }
    }

    /*
     * This method prints the usage message to the terminal. This method is
     * called if EncodingHelper is called on the terminal without any inputs
     */
    public static void printUsageMessage() {
        System.out.println("usage: EncodingHelper [-i|--input type] " +
                "[-o|—output type] <data>");
        System.out.println("");
        System.out.println
                ("EncodingHelper converts between strings, " +
                        "UTF-8 encodings, and Unicode code points. " +
                        "If no input argument is specified, " +
                        "EncodingHelper will guess the input type. " +
                        "If no output is specified, EncodingHelper " +
                        "will print out a summary of the input.");
        System.out.println("");
        System.out.println("The accepted input and output " +
                "specifications are ‘utf8’, ‘codepoint’, and ‘string’.");
        return;
    }

    /*
     * This method converts a string representing an array of UTF-8 bytes to a
     * list of EncodingHelperChar objects
     * @param input string representing the UTF-8 bytes
     * @return the list of EncodingHelperChar objects
     */
    public static List<EncodingHelperChar> utf8ToCharList(String input) {
        List<EncodingHelperChar> output = new ArrayList<EncodingHelperChar>();

        if(input.length()%4!=0) {
            System.out.println("Invalid UTF-8 Encoding.");
            return null;
        }
        for(int j = 0; j<input.length(); j+=4) {
            //Checks to see if each 4-character segment is of the form '\xFF'
            if(input.charAt(j) != '\\' || input.charAt(j+1) != 'x'){
                System.out.println("Invalid UTF-8 Encoding.");
                return null;
            }
        }
        int i = 0;
        while(i<input.length()){
            //Extracts characters and checks to see if it is valid hexadecimal
            int givenInt = EncodingHelperChar.hexadecimalToInteger
                    (input.substring(i+2,i+4));
            if(givenInt==-1) {
                System.out.println("Invalid UTF-8 Encoding.");
                return null;
            }
            byte givenByte = (byte) givenInt;

            i+=4;
            // Checks to see if encoding suggests 1 byte
            if((givenByte & (byte) 0x80) == (byte) 0x00) {
                try {
                    byte[] givenChar = {(byte) givenByte};
                    output.add(new EncodingHelperChar(givenChar));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UTF-8 Encoding.");
                    return null;
                }
            }

            if((givenByte & (byte) 0xC0) == (byte) 0x80) {
                System.out.println("Invalid UTF-8 Encoding.");
                return null;
            }

            // Checks to see if encoding suggests 2 bytes
            if((givenByte & (byte) 0xE0) == (byte) 0xC0) {
                try {
                    // Adds next byte sequence to byte array
                    // if valid hexadecimal and constructs
                    // the EncodingHelperChar
                    int givenInt2 = EncodingHelperChar.
                            hexadecimalToInteger(input.substring(i+2,i+4));
                    if(givenInt2==-1) {
                        System.out.println("Invalid UTF-8 Encoding.");
                        return null;
                    }
                    byte givenByte2 = (byte) givenInt2;
                    byte[] givenChar = {givenByte, givenByte2};
                    output.add(new EncodingHelperChar(givenChar));
                    i += 4;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UTF-8 Encoding.");
                    return null;
                }
            }
            // Checks to see if encoding suggests 3 bytes
            if((givenByte & (byte) 0xF0) == (byte) 0xE0) {
                try {
                    // Adds next two byte sequences to byte array
                    // if valid hexadecimal and constructs
                    // the EncodingHelperChar
                    int givenInt2 = EncodingHelperChar.
                            hexadecimalToInteger(input.substring(i+2,i+4));
                    int givenInt3 =  EncodingHelperChar.
                            hexadecimalToInteger(input.substring(i+6,i+8));
                    if(givenInt2==-1 || givenInt3==-1) {
                        System.out.println("Invalid UTF-8 Encoding.");
                        return null;
                    }
                    byte givenByte2 = (byte) givenInt2;
                    byte givenByte3 = (byte) givenInt3;
                    byte[] givenChar = {givenByte, givenByte2, givenByte3};
                    output.add(new EncodingHelperChar(givenChar));
                    i += 8;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UTF-8 Encoding.");
                    return null;
                }
            }
            // Checks to see if encoding suggests 4 bytes
            if((givenByte & (byte) 0xF8) == (byte) 0xF0) {
                try {
                    // Adds next three byte sequences to byte array
                    // and constructs the EncodingHelperChar
                    int givenInt2 = EncodingHelperChar.
                            hexadecimalToInteger(input.substring(i+2,i+4));
                    int givenInt3 = EncodingHelperChar.
                            hexadecimalToInteger(input.substring(i+6,i+8));
                    int givenInt4 = EncodingHelperChar.
                            hexadecimalToInteger(input.substring(i+10,i+12));
                    if(givenInt2==-1 || givenInt3==-1 || givenInt4==-1) {
                        System.out.println("Invalid UTF-8 Encoding.");
                        return null;
                    }
                    byte givenByte2 = (byte) givenInt2;
                    byte givenByte3 = (byte) givenInt3;
                    byte givenByte4 = (byte) givenInt4;
                    byte[] givenChar = {givenByte, givenByte2,
                            givenByte3, givenByte4};
                    output.add(new EncodingHelperChar(givenChar));
                    i += 12;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UTF-8 Encoding.");
                    return null;
                }
            }

        }

        return output;
    }

    /*
     * This method converts a string to a list of EncodingHelperChar objects
     * @param input string to be converted
     * @return the list of EncodingHelperChar objects
     */
    public static List<EncodingHelperChar> stringToCharList(String input) {
        List<EncodingHelperChar> output = new ArrayList<EncodingHelperChar>();
        for(int i=0; i<input.length(); i++) {
            output.add(i, new EncodingHelperChar(input.charAt(i)));
        }
        return output;
    }

    /*
     * This method converts a string representing an array of codepoints to a
     * list of EncodingHelperChar objects
     * @param input string representing the code points
     * @return the list of EncodingHelperChar objects
     */
    public static List<EncodingHelperChar> codepointsToCharList(String input) {
        String[] splitInput = input.split("\\s+");
        List<EncodingHelperChar> output = new ArrayList<EncodingHelperChar>(0);
        for(int i=0; i<splitInput.length; i++) {
            // e.g. splitInput[i] = U+04C2
            if(splitInput[i].length() < 6) {
                System.out.println("Invalid Code Point(s)");
                return null;
            }
            else if(splitInput[i].charAt(0) != 'U'
                    || splitInput[i].charAt(1) != '+') {
                System.out.println("Invalid Code Point(s)");
                return null;
            }
            else {
                try {
                    int codepoint = EncodingHelperChar.hexadecimalToInteger
                            (splitInput[i].substring(2));
                    output.add(i, new EncodingHelperChar(codepoint));
                }
                catch (IllegalArgumentException e) {
                    System.out.println("Invalid Code Point(s).");
                    return null;
                }
            }
        }
        return output;
    }

    /*
     * converts list of EncodingHelperChar objects into a string of characters
     * @param list of EncodingHelperChar objects
     * @return string of corresponding characters
     */
    public static String charListToString(List<EncodingHelperChar> charList) {
        String output = "";
        for(int i=0; i<charList.size(); i++) {
            int codePoint = charList.get(i).getCodePoint();
            String s = Character.toString((char)codePoint);
            output += s;
        }
        return output;
    }

    /*
     * converts list of EncodingHelperChar objects into a string representing an
     * array of UTF-8 bytes
     * @param list of EncodingHelperChar objects
     * @return string representing UTF-8 bytes
     */
    public static String charListToUtf8(List<EncodingHelperChar> charList) {
        String output = "";
        for(int i=0; i<charList.size(); i++) {
            output += charList.get(i).toUtf8String();
        }
        return output;
    }

    /*
     * converts list of EncodingHelperChar objects into a string representing an
     * array of codepoints
     * @param list of EncodingHelperChar objects
     * @return string representing codepoints
     */
    public static String charListToCodepoint
    (List<EncodingHelperChar> charList) {
        String output = "";
        for(int i=0; i<charList.size(); i++) {
            if(i != 0) {
                output += " ";
            }
            String hex = EncodingHelperChar.
                    integerToHexadecimal(charList.get(i).getCodePoint());
            if(hex.length() == 0)
                output += "U+0000";
            else if(hex.length() == 1)
                output += "U+000"+hex;
            else if(hex.length() == 2)
                output += "U+00"+hex;
            else if(hex.length() == 3)
                output += "U+0"+hex;
            else
                output += "U+"+hex;
        }
        return output;
    }
}
