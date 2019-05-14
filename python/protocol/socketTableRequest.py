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


from .socketTableMessage import SocketTableMessage


class SocketTableRequest:

    @staticmethod
    def parseResponse(encodedResponse, default=None):
        """
        Parse the value from the JSON response.
        """

        response = SocketTableMessage.decodeMessage(encodedResponse)

        value = default
        if (response != None and response.get(SocketTableMessage.VALUE) != None):
            value = response.get(SocketTableMessage.VALUE)

        return value

    @staticmethod
    def parseGetAllResponse(encodedResponse, default=None):
        """
        Parse the GETALL values from the JSON response.
        """

        response = SocketTableMessage.decodeMessage(encodedResponse)
        values = {}

        if (response != None):
            for data in response:
                key = data.get('key')
                value = data.get('value')
                values[key] = value

        return values

    @staticmethod
    def get(key):
        """
        Get the value of the key from the SocketTableServer.
        """

        message = {
            SocketTableMessage.REQUEST: SocketTableMessage.GET,
            SocketTableMessage.KEY: key,
        }

        return SocketTableMessage.encodeMessage(message)

    @staticmethod
    def getAll():
        """
        Get all the values from the SocketTableServer.
        """

        message = {
            SocketTableMessage.REQUEST: SocketTableMessage.GETALL
        }

        return SocketTableMessage.encodeMessage(message)

    @staticmethod
    def update(key, value):
        """
        Update the value of the key in the SocketTableServer.
        """

        message = {
            SocketTableMessage.REQUEST: SocketTableMessage.UPDATE,
            SocketTableMessage.KEY: key,
            SocketTableMessage.VALUE: value
        }

        return SocketTableMessage.encodeMessage(message)

    @staticmethod
    def delete(key):
        """
        Delete the key from the SocketTableServer.
        """

        message = {
            SocketTableMessage.REQUEST: SocketTableMessage.DELETE,
            SocketTableMessage.KEY: key,
        }

        return SocketTableMessage.encodeMessage(message)
