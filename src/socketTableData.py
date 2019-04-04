#!/usr/bin/env python3

"""
----------------------------------------------------------------------------
Author(s):     Maxwell Pettit

Date:          4/1/2019

Description:   SocketTables provide a socket based communication protcol
               for performing simple CRUD (Create, Read, Update, Delete)
               operations.  SocketTables are designed to use JSON messages
               to provide access to an in-memory, key-value data map.
----------------------------------------------------------------------------
"""

import json


class SocketTableData:

    # Header for getting data
    GET = 'GET'
    # Header for adding/updating data
    UPDATE = 'UPDATE'
    # Header for deleting data
    DELETE = 'DELETE'

    """
    Data populated by clients

    Matches the format:
    data = {
        key: value
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

        key = message['key']
        value = self.data.get(key)

        response = {
            'key': key,
            'value': value
        }

        return response

    def update(self, message):
        """
        Update the value for the message key to the message value.
        """

        key = message['key']
        value = message['value']

        if (key is not None):
            self.data[key] = value

        response = {
            'key': key,
            'value': value
        }

        return response

    def delete(self, message):
        """
        Delete the value for the message key.
        """

        key = message['key']
        value = self.data.pop(key)

        response = {
            'key': key,
            'value': value
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

            if (message['request'] == self.UPDATE):
                response = self.update(message)
            elif (message['request'] == self.GET):
                response = self.get(message)
            elif (message['request'] == self.DELETE):
                response = self.delete(message)
            else:
                print('Unknown message format: ' + repr(message))

        encodedResponse = json.dumps(response).encode(encoding='utf-8')
        return encodedResponse
