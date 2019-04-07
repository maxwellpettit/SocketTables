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
from collections import OrderedDict


class SocketTableMessage:

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

    @staticmethod
    def encodeMessage(message):
        encodedMessage = json.dumps(
            OrderedDict(message)).encode(encoding='utf-8')
        return encodedMessage

    @staticmethod
    def decodeMessage(encodedMessage):
        message = json.loads(encodedMessage.decode(
            encoding='utf-8'), object_pairs_hook=OrderedDict)
        return message
