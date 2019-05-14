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

from time import gmtime, strftime
from .socketTableMessage import SocketTableMessage


class SocketTableData:

    """
    Data populated by clients

    Matches the format:
    data = {
        KEY_NAME: {
            'value': CURRENT_VALUE,
            'timestamp': LAST_UPDATE_TIME
        }, 
        ...
    }
    """
    data = {}

    # Callback functions for a specified key
    callbacks = {}

    def __init__(self):
        self.reset()

    def reset(self):
        """
        Reset all data from clients.
        """
        self.data = {}

    def get(self, message):
        """
        Get the value for the message key.
        """

        key = message[SocketTableMessage.KEY]

        value = None
        result = self.data.get(key)
        if (result != None):
            value = result[SocketTableMessage.VALUE]

        response = {
            SocketTableMessage.KEY: key,
            SocketTableMessage.VALUE: value
        }

        return response

    def getAll(self):
        """
        Get all values from the server.
        """

        response = []
        for key in self.data:
            entry = {
                SocketTableMessage.KEY: key,
                SocketTableMessage.VALUE: self.data.get(key).get('value')
            }
            response.append(entry)

        return response

    def update(self, message):
        """
        Update the value for the message key to the message value.
        """

        key = message[SocketTableMessage.KEY]
        value = message[SocketTableMessage.VALUE]

        if (key is not None):
            t = strftime("%Y-%m-%d %H:%M:%S", gmtime())
            self.data[key] = {
                SocketTableMessage.VALUE: value,
                SocketTableMessage.TIMESTAMP: t
            }

        response = {
            SocketTableMessage.KEY: key,
            SocketTableMessage.VALUE: value
        }

        return response

    def delete(self, message):
        """
        Delete the value for the message key.
        """

        key = message[SocketTableMessage.KEY]

        value = None
        if (key in self.data):
            result = self.data.pop(key)
            value = result[SocketTableMessage.VALUE]

        response = {
            SocketTableMessage.KEY: key,
            SocketTableMessage.VALUE: value
        }

        return response

    def handleMessage(self, encodedMessage):
        """
        Parse the encoded JSON message received from the client and
        generate an encoded JSON response.
        """
        response = None

        if (encodedMessage is not None and len(encodedMessage) > 0):
            message = SocketTableMessage.decodeMessage(encodedMessage)
            print('Received message: ' + repr(message))

            if (message[SocketTableMessage.REQUEST] == SocketTableMessage.GET):
                response = self.get(message)
            elif (message[SocketTableMessage.REQUEST] == SocketTableMessage.GETALL):
                response = self.getAll()
            elif (message[SocketTableMessage.REQUEST] == SocketTableMessage.UPDATE):
                response = self.update(message)
            elif (message[SocketTableMessage.REQUEST] == SocketTableMessage.DELETE):
                response = self.delete(message)
            else:
                print('Unknown message format: ' + repr(message))

            print('Responding with message: ' + repr(response))

            # Handle callbacks
            self.handleCallbacks(message, response)

        encodedResponse = SocketTableMessage.encodeMessage(response)
        return encodedResponse

    def handleCallbacks(self, message, response):
        """
        Execute callback functions when a key changes.
        """

        if (SocketTableMessage.KEY in message):
            request = message[SocketTableMessage.REQUEST]
            key = message[SocketTableMessage.KEY]
            value = None
            if (response is not None and SocketTableMessage.VALUE in response):
                value = response[SocketTableMessage.VALUE]

            if (key is not None and key in self.callbacks):
                # Execute the callback function
                self.callbacks[key](value, request)

    def setCallback(self, key, callback):
        """
        Register a callback function for a specified key.
        """

        print("Registering callback for key: " + repr(key))

        self.callbacks[key] = callback
