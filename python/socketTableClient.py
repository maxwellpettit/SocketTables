#!/usr/bin/env python3

"""
----------------------------------------------------------------------------
Author(s):     	Maxwell Pettit

Date:          	4/1/2019

Description:   	SocketTables provide a socket based communication protocol 
               	for performing simple in-memory CRUD (Create, Read, Update, 
               	Delete) operations. SocketTables are designed to use JSON 
               	messages to provide access to a key-value mapping on a 
               	Java or Python server.
----------------------------------------------------------------------------
"""

import socket
import traceback
import time
from protocol import SocketTableRequest


HOST = '127.0.0.1'
PORT = 7777


class SocketTableClient:

    # Size of packets to send and reveive
    PACKET_SIZE = 1024
    # Connection Attempts before exiting
    CONNECTION_ATTEMPTS = 60
    # Timeout for receiving packets
    TIMEOUT = 0.05

    clientSocket = None

    def __init__(self, host=HOST, port=PORT):
        self.host = host
        self.port = port

        # Wait for connection at startup
        self.connect(retry=True)
        self.close()

    def connect(self, retry=False):
        """
        Open a new socket to the server and connect.
        """

        attempts = 0
        maxAttempts = self.CONNECTION_ATTEMPTS
        if (not retry):
            maxAttempts = 1

        connected = False
        while (not connected and attempts < maxAttempts):
            try:
                self.clientSocket = socket.socket(
                    socket.AF_INET, socket.SOCK_STREAM)
                self.clientSocket.settimeout(self.TIMEOUT)
                self.clientSocket.connect((self.host, self.port))

                connected = True
                print('Connected to server.')
            except:
                if (self.clientSocket):
                    self.clientSocket.close()

                attempts += 1
                print('Connection failed.  Attempts: ' + str(attempts))

                if (retry):
                    time.sleep(1)

        if (not connected and retry):
            print('Maximum connection attempts exceeded.')

        return connected

    def sendMessage(self, encodedMessage):
        """
        Send a JSON message to the server.
        """

        try:
            print('Sending: ' + repr(encodedMessage))
            self.clientSocket.sendall(encodedMessage)
        except:
            print('Error sending message.')
            traceback.print_exc()

    def receiveMessage(self):
        """
        Receive a JSON response from the server.
        """

        encodedResponse = None
        try:
            encodedResponse = self.clientSocket.recv(self.PACKET_SIZE)
        except socket.error:
            print('Error receiving message.')
            traceback.print_exc()

        print('Received: ' + repr(encodedResponse))

        return encodedResponse

    def processMessage(self, encodedMessage):
        """
        Send the JSON request to the server and wait for the JSON response.
        """

        start = time.time()

        connected = self.connect()

        encodedResponse = None
        if (connected):
            self.sendMessage(encodedMessage)
            encodedResponse = self.receiveMessage()

            self.close()

        end = time.time()
        diff = end - start
        print('Round Trip Time: ', str(diff))

        return encodedResponse

    def get(self, key, default=None):
        """
        Get the value of the key from the SocketTableServer.
        """

        encodedMessage = SocketTableRequest.get(key)

        response = self.processMessage(encodedMessage)
        value = SocketTableRequest.parseResponse(response, default)

        return value

    def getAll(self):
        """
        Get all the values from the SocketTableServer.
        """

        encodedMessage = SocketTableRequest.getAll()

        response = self.processMessage(encodedMessage)
        value = SocketTableRequest.parseGetAllResponse(response)

        return value

    def update(self, key, value):
        """
        Update the value of the key in the SocketTableServer.
        """

        encodedMessage = SocketTableRequest.update(key, value)

        response = self.processMessage(encodedMessage)
        value = SocketTableRequest.parseResponse(response)

        return value

    def delete(self, key):
        """
        Delete the key from the SocketTableServer.
        """

        encodedMessage = SocketTableRequest.delete(key)

        response = self.processMessage(encodedMessage)
        value = SocketTableRequest.parseResponse(response)

        return value

    def close(self):
        """
        Close the current socket if it is open.
        """

        if (self.clientSocket is not None):
            self.clientSocket.close()


def main():
    """
    Test function for evaluating get/update/delete
    """

    i = 0

    client = SocketTableClient()
    try:
        while (True):

            # Update value
            response = client.update('test1', i)
            print('Update Value: ', repr(response), '\n')

            # Get good value
            response = client.get('test1', 'default')
            print('Get Value (Good): ', repr(response), '\n')

            # Update value
            response = client.update('test2', i + 50)
            print('Update Value: ', repr(response), '\n')

            # Get all values
            response = client.getAll()
            print('Get All Values: ', repr(response), '\n')

            # Delete value
            response = client.delete('test1')
            print('Delete Value: ', repr(response), '\n')

            i += 1

            time.sleep(1.5)

    except KeyboardInterrupt:
        print("Caught keyboard interrupt.  Exiting...")


if __name__ == "__main__":
    main()
