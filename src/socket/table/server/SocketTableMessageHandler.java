package socket.table.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import socket.table.RequestType;

public class SocketTableMessageHandler implements Runnable {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static final String PYTHON_MESSAGE_TEMPLATE = "\\{\"request\": \"([A-Z]+)\", \"key\": \"(([^\"]+)+)\"(, \"value\": \"?([^\"]+)\"?)?\\}";
    private static final String PYTHON_RESPONSE_TEMPLATE = "{\"key\": \"%s\", \"value\": \"%s\"}";

    private final Pattern MESSAGE_PATTERN = Pattern.compile(PYTHON_MESSAGE_TEMPLATE);

    private final Socket clientSocket;

    private final SocketTableData socketTableData;

    public SocketTableMessageHandler(SocketTableData socketTableData, Socket clientSocket) {
        this.socketTableData = socketTableData;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("Client connected: " + this.clientSocket.toString());
        try {
            // Create reader/write
            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            // Read incoming message
            String message = in.readLine();
            System.out.println("Message received: " + message);

            // Parse message and generate response
            String response = null;
            if (message != null) {
                response = handleMessage(message);
            }

            // Send response
            System.out.println("Sending Response: " + response);
            out.println(response);

        } catch (Exception ex) {
            System.out.println("Failure processing SocketTableServer request.");
            ex.printStackTrace();
        } finally {
            closeSocket();
        }
    }

    protected String handleMessage(String message) {

        String response = null;

        // Parse request type
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        if (matcher.find()) {
            String request = matcher.group(1);
            String key = matcher.group(2);

            if (request != null && key != null) {
                String result = null;
                if (request.equals(RequestType.GET.toString())) {
                    // Handle GET
                    result = socketTableData.getString(key, null);
                } else if (request.equals(RequestType.UPDATE.toString())) {
                    // Handle UPDATE
                    String value = matcher.group(5);
                    result = socketTableData.updateString(key, value);
                } else if (request.equals(RequestType.DELETE.toString())) {
                    // Handle DELETE
                    result = socketTableData.delete(key);

                } else {
                    System.out.println("Unknown request type: " + request);
                }

                response = formatResponse(key, result);
            }
        } else {
            System.out.println("Unable to parse message: " + message);
        }

        return response;
    }

    public String formatResponse(String key, String value) {
        return String.format(PYTHON_RESPONSE_TEMPLATE, key, value);
    }

    private void closeSocket() {
        if (clientSocket != null) {
            try {
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
