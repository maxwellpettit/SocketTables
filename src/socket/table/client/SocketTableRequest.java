package socket.table.client;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import socket.table.RequestType;

public class SocketTableRequest {

	private static final String PYTHON_MESSAGE_TEMPLATE = "{\"request\": \"%s\", \"key\": \"%s\", \"value\": \"%s\"}";
	private static final String PYTHON_RESPONSE_TEMPLATE = "\\{\"key\": \"(.+)\", \"value\": \"?([^\"]+)\"?\\}";

	private final Pattern RESPONSE_PATTERN = Pattern.compile(PYTHON_RESPONSE_TEMPLATE);

	private static final int TIMEOUT_MS = 50;

	private String host;
	private int port;
	private boolean debug = false;

	private Socket clientSocket;

	public SocketTableRequest(String host, int port) {
		this.host = host;
		this.port = port;
		createSocket();
	}

	private void createSocket() {
		try {
			long start = System.currentTimeMillis();
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);

			if (debug) {
				long finish = System.currentTimeMillis();
				long timeElapsed = finish - start;
				System.out.println("Socket Creation Time (ms): " + timeElapsed);
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public String processMessage(RequestType request, String key, String value) {
		String response = null;

		try {
			// Get reader/writer
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// Format message
			String message = String.format(PYTHON_MESSAGE_TEMPLATE, request.toString(), key, value);

			// Send message
			if (debug) {
				System.out.println("SENDING MESSAGE: " + message);
			}
			out.println(message);

			// Read response
			String responseMessage = in.readLine();
			if (debug) {
				System.out.println("GOT RESPONSE: " + responseMessage);
			}

			// Parse response
			Matcher matcher = RESPONSE_PATTERN.matcher(responseMessage);
			if (matcher.find()) {
				response = matcher.group(2);
			} else {
				System.out.println("Unable to parse message.");
			}

		} catch (Exception ex) {
			System.out.println("Could not process message.");

		} finally {
			closeSocket();
		}

		return response;
	}

	public void debug(boolean enabled) {
		this.debug = enabled;
	}

}
