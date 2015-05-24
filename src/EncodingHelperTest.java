import com.company.EncodingHelperApp;
import com.company.EncodingHelperChar;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Thomas Redding & Ben Wedin on 4/4/15.
 */

public class EncodingHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void encodingHelper_shouldTakeNonFlaggedStrings() throws Exception {
        System.out.println("{START 1");
        String args[] = {"summary"};
        EncodingHelper.main(args);
        System.out.println("END 1}");
    }

    @Test
    public void encodingHelper_shouldTakeNonFlaggedChars() throws Exception {
        System.out.println("{START 2");
        String args[] = {"s"};
        EncodingHelper.main(args);
        System.out.println("END 2}");
    }

    @Test
    public void encodingHelper_shouldTakeOutputFlaggedStrings() throws Exception {
        System.out.println("{START 3");
        String args[] = {"--output", "utf8", "Öberg"};
        EncodingHelper.main(args);
        System.out.println("END 3}");
    }

    @Test
    public void encodingHelper_shouldRejectPoorlyFlaggedStrings() throws Exception {
        System.out.println("{START 4");
        String args[] = {"--output", "utf16", "Öberg"};
        EncodingHelper.main(args);
        System.out.println("END 4}");
    }

    @Test
    public void encodingHelper_shouldPrintUsageMessage() throws Exception {
        System.out.println("{START 5");
        String args[] = {};
        EncodingHelper.main(args);
        System.out.println("END 5}");
    }

    @Test
    public void encodingHelper_shouldTakeManyFlags() throws Exception {
        System.out.println("{START 6");
        String args[] = {"-i","string","--output","summary","U+0034"};
        EncodingHelper.main(args);
        System.out.println("END 6}");
    }

    @Test
    public void encodingHelper_shouldGuessCodepointsEvenIfNotGreat() throws Exception {
        System.out.println("{START 7");
        String args[] = {"--output","summary","U+0034 U+MMII U+0089"};
        EncodingHelper.main(args);
        System.out.println("END 7}");
    }

    @Test
    public void encodingHelper_shouldGuessBytesEvenIfNotGreat() throws Exception {
        System.out.println("{START 8");
        String args[] = {"--output","summary","\\xB3\\xc1\\x10"};
        EncodingHelper.main(args);
        System.out.println("END 8}");
    }



}