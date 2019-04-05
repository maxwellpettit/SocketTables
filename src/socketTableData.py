#!/usr/bin/env python3

"""
----------------------------------------------------------------------------
Author(s):     Maxwell Pettit

Date:          4/1/2019

Description:   SocketTables provide a socket based communication protocol 
               for performing simple in-memory CRUD (Create, Read, Update, 
               Delete) operations. SocketTables are designed to use JSON 
               messages to provide access to a key-value mapping on a 
               Python server.
----------------------------------------------------------------------------
"""

import json
from time import gmtime, strftime

# JSON value for getting data
GET = 'GET'
# JSON value for getting all data
GETALL = 'GETALL'
# JSON value for adding/updating data
UPDATE = 'UPDATE'
# JSON value for deleting data
DELETE = 'DELETE'

# JSON property for request
REQUEST = 'request'
# JSON property for key
KEY = 'key'
# JSON property for value
VALUE = 'value'
# JSON property for timestamp
TIMESTAMP = 'timestamp'


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
    data: dict

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

        key = message[KEY]

        value = None
        result = self.data.get(key)
        if (result != None):
            value = result[VALUE]

        response = {
            KEY: key,
            VALUE: value
        }

        return response

    def getAll(self):
        """
        Get all values from the server.
        """

        return self.data

    def update(self, message):
        """
        Update the value for the message key to the message value.
        """

        key = message[KEY]
        value = message[VALUE]

        if (key is not None):
            t = strftime("%Y-%m-%d %H:%M:%S", gmtime())
            self.data[key] = {
                VALUE: value,
                TIMESTAMP: t
            }

        response = {
            KEY: key,
            VALUE: value
        }

        return response

    def delete(self, message):
        """
        Delete the value for the message key.
        """

        key = message[KEY]

        value = None
        result = self.data.pop(key)
        if (result != None):
            value = result[VALUE]

        response = {
            KEY: key,
            VALUE: value
        }

        return response

    def handleMessage(self, encodedMessage):
        """
        Parse the encoded JSON message received from the client and
        generate an encoded JSON response.
        """
        response = None

        if (encodedMessage is not None and len(encodedMessage) > 0):
            message = json.loads(encodedMessage.decode(encoding='utf-8'))
            print('Received message: ' + repr(message))

            if (message[REQUEST] == GET):
                response = self.get(message)
            elif (message[REQUEST] == GETALL):
                response = self.getAll()
            elif (message[REQUEST] == UPDATE):
                response = self.update(message)
            elif (message[REQUEST] == DELETE):
                response = self.delete(message)
            else:
                print('Unknown message format: ' + repr(message))

            print('Responding with message: ' + repr(response))

        encodedResponse = json.dumps(response).encode(encoding='utf-8')
        return encodedResponse
