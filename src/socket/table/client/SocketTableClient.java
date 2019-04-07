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

import socket.table.RequestType;

public class SocketTableClient {

	private String host = "127.0.0.1";
	private int port = 7777;

	public SocketTableClient() {
	}

	public SocketTableClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getString(String key, String defaultValue) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String value = request.processMessage(RequestType.GET, key, null);
		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	public int getInt(String key, int defaultValue) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String response = request.processMessage(RequestType.GET, key, null);

		int value = defaultValue;
		try {
			value = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.out.println("Couldn't parse int.");
		}
		return value;
	}

	public double getDouble(String key, double defaultValue) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String response = request.processMessage(RequestType.GET, key, null);

		double value = defaultValue;
		try {
			value = Double.parseDouble(response);
		} catch (NumberFormatException e) {
			System.out.println("Couldn't parse double.");
		}
		return value;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String response = request.processMessage(RequestType.GET, key, null);

		boolean value = defaultValue;
		if (response != null) {
			value = Boolean.parseBoolean(response);
		}
		return value;
	}

	public boolean updateString(String key, String value) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String response = request.processMessage(RequestType.UPDATE, key, value);

		boolean success = false;
		if (value == null && response == null) {
			success = true;
		} else if (value != null && response != null) {
			success = value.equals(response);
		}
		return success;
	}

	public boolean updateInt(String key, int value) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String stringValue = Integer.toString(value);
		String response = request.processMessage(RequestType.UPDATE, key, stringValue);

		boolean success = stringValue.equals(response);
		return success;
	}

	public boolean updateDouble(String key, double value) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String stringValue = Double.toString(value);
		String response = request.processMessage(RequestType.UPDATE, key, stringValue);

		boolean success = stringValue.equals(response);
		return success;
	}

	public boolean updateBoolean(String key, boolean value) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String stringValue = Boolean.toString(value);
		String response = request.processMessage(RequestType.UPDATE, key, stringValue);

		boolean success = stringValue.equals(response);
		return success;
	}

	public boolean delete(String key) {
		SocketTableRequest request = new SocketTableRequest(host, port);

		String response = request.processMessage(RequestType.DELETE, key, null);

		boolean success = response != null;
		return success;
	}

	public static void main(String[] args) {
		SocketTableClient client = new SocketTableClient();

		long runs = 1000;
		long total = 0;
		for (int i = 0; i < runs; i++) {

			if (i % 3 == 0) {
				long start = System.currentTimeMillis();
				boolean success = client.updateString("test1", "Run - " + Integer.toString(i));

				System.out.println("UPDATE SUCCESS: " + success);

				long finish = System.currentTimeMillis();
				long timeElapsed = finish - start;
				total += timeElapsed;

			} else if (i % 3 == 1) {
				long start = System.currentTimeMillis();
				String value = client.getString("test1", "default");

				System.out.println("GOT VALUE: " + value);

				long finish = System.currentTimeMillis();
				long timeElapsed = finish - start;
				total += timeElapsed;

			} else {
				long start = System.currentTimeMillis();
				boolean success = client.delete("test1");

				System.out.println("DELETE SUCCESS: " + success);

				long finish = System.currentTimeMillis();
				long timeElapsed = finish - start;
				total += timeElapsed;
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		double average = (double) total / (double) runs;

		System.out.println("Average Round Trip Time (ms): " + average);
	}

}
