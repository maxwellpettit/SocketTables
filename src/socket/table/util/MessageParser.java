package socket.table.util;

/*
----------------------------------------------------------------------------
Author(s):     Maxwell Pettit

Date:          4/1/2019

Description:   SocketTables provide a socket based communication protocol 
               for performing simple in-memory CRUD (Create, Read, Update, 
               Delete) operations. SocketTables are designed to use JSON 
               messages to provide access to a key-value mapping on a 
               Python server.
----------------------------------------------------------------------------
*/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    // Key/Values must be alphanumeric ('A-Z', 'a-z', '0-9', '_', '-', '.', ' ')
    private static final String KEY_VALUE_FORMAT = "\"?([A-Za-z\\d_\\-\\.\\s]+)\"?";

    private static final String CLIENT_REQUEST_TEMPLATE = "\"request\":\\s*\"([A-Z]+)\"";
    private static final String CLIENT_KEY_TEMPLATE = "\"key\":\\s*" + KEY_VALUE_FORMAT;
    private static final String CLIENT_VALUE_TEMPLATE = "\"value\":\\s*" + KEY_VALUE_FORMAT;

    public static final Pattern REQUEST_PATTERN = Pattern.compile(CLIENT_REQUEST_TEMPLATE);
    public static final Pattern KEY_PATTERN = Pattern.compile(CLIENT_KEY_TEMPLATE);
    public static final Pattern VALUE_PATTERN = Pattern.compile(CLIENT_VALUE_TEMPLATE);

    private static final String SERVER_RESPONSE_TEMPLATE = "{\"key\": \"%s\", \"value\": \"%s\"}";
    private static final String CLIENT_MESSAGE_TEMPLATE = "{\"request\": \"%s\", \"key\": \"%s\", \"value\": \"%s\"}";

    public static String parseMessage(String message, Pattern pattern) {
        String parsedValue = null;
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            parsedValue = matcher.group(1);
        }
        return parsedValue;
    }

    public static String formatResponse(String key, String value) {
        return String.format(SERVER_RESPONSE_TEMPLATE, key, value);
    }

    public static String formatMessage(RequestType request, String key, String value) {
        return String.format(CLIENT_MESSAGE_TEMPLATE, request.toString(), key, value);
    }

}
