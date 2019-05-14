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
import java.util.Map;

import socket.table.util.MessageParser;
import socket.table.util.RequestType;

public class SocketTableRequest {

	private static final int TIMEOUT_MS = 50;

	private String host;
	private int port;
	private boolean debug = true;

	private Socket clientSocket;

	public SocketTableRequest(String host, int port) {
		this.host = host;
		this.port = port;
		createSocket();
	}

	private void createSocket() {
		try {
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);

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

	private String getResponse(RequestType request, String key, String value) throws IOException {
		// Get reader/writer
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		// Format message
		String message = MessageParser.formatMessage(request, key, value);

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
		return responseMessage;
	}

	public String processMessage(RequestType request, String key, String value) {
		String response = null;

		try {
			String responseMessage = getResponse(request, key, value);

			// Parse response
			response = MessageParser.parseMessage(responseMessage, MessageParser.VALUE_PATTERN);

			if (response == null) {
				System.out.println("Unable to parse message.");
			}

		} catch (Exception ex) {
			System.out.println("Could not process message.");

		} finally {
			closeSocket();
		}

		return response;
	}

	public Map<String, String> processGetAll() {
		Map<String, String> values = null;

		try {
			String responseMessage = getResponse(RequestType.GETALL, null, null);

			// Parse response
			values = MessageParser.parseGetAllMessage(responseMessage);

			if (values == null) {
				System.out.println("Unable to parse message.");
			}

		} catch (Exception ex) {
			System.out.println("Could not process message.");

		} finally {
			closeSocket();
		}

		return values;
	}

	public void debug(boolean enabled) {
		this.debug = enabled;
	}

}
