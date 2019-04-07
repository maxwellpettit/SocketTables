package socket.table.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import socket.table.RequestType;

public class SocketTableMessageHandler implements Runnable {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static final String PYTHON_MESSAGE_TEMPLATE = "\\{\"request\": \"([A-Z]+)\", \"key\": \"(.+)\", \"value\": \"?([^\"]+)\"?\\}";
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
        System.out.println("Client connected: ");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);

            String message = br.readLine();
            System.out.println("Message received: " + message);

            if (message != null) {
                String repsonse = handleMessage(message);
                System.out.println("Sending Response: " + repsonse);

                out.println(repsonse);
            }

        } catch (Exception ex) {
            System.out.println("Failure processing RobotServer client request!");
            ex.printStackTrace();
        }
    }

    protected String handleMessage(String message) {

        String response = null;

        // Parse request type
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        if (matcher.find()) {
            String request = matcher.group(1);
            String key = matcher.group(2);
            String value = matcher.group(3);

            if (request != null && key != null) {
                String result = null;
                if (request.equals(RequestType.GET.toString())) {
                    result = socketTableData.getString(key, null);
                } else if (request.equals(RequestType.UPDATE.toString())) {

                } else if (request.equals(RequestType.DELETE.toString())) {

                } else {
                    System.out.println("Unknown request type: " + request);
                }

                response = formatResponse(key, result);
            }
        } else {
            System.out.println("Unable to parse message.");
        }

        return response;
    }

    public String formatResponse(String key, String value) {
        String response = String.format(PYTHON_RESPONSE_TEMPLATE, key, value);
        return response;
    }
}
