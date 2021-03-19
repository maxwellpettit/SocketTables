package socket.table.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class MessageParserTest {

    @Test
    protected void testParseValue() {
        String message1 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY1", "Value1");
        testParser(message1, MessageParser.VALUE_PATTERN, "Value1");

        String message2 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY2", "Value1, Value2");
        testParser(message2, MessageParser.VALUE_PATTERN, "Value1, Value2");

        String message3 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY3, TEST", "Value1");
        testParser(message3, MessageParser.VALUE_PATTERN, "Value1");

        String message4 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY4, TEST", "Value1, Value2");
        testParser(message4, MessageParser.VALUE_PATTERN, "Value1, Value2");
    }

    @Test
    protected void testParseKey() {
        String message1 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY1", "Value1");
        testParser(message1, MessageParser.KEY_PATTERN, "TEST_KEY1");

        String message2 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY2", "Value1, Value2");
        testParser(message2, MessageParser.KEY_PATTERN, "TEST_KEY2");

        String message3 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY3, TEST", "Value1");
        testParser(message3, MessageParser.KEY_PATTERN, "TEST_KEY3, TEST");

        String message4 = MessageParser.formatMessage(RequestType.UPDATE, "TEST_KEY4, TEST", "Value1, Value2");
        testParser(message4, MessageParser.KEY_PATTERN, "TEST_KEY4, TEST");
    }

    protected void testParser(String message, Pattern pattern, String expectedValue) {
        String parsedValue = MessageParser.parseMessage(message, pattern);
        System.out.println("Parsed Value: " + parsedValue);
        assertEquals(expectedValue, parsedValue);
    }

}
