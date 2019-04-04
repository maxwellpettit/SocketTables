#!/usr/bin/env python3

"""
----------------------------------------------------------------------------
Author(s):     Maxwell Pettit

Date:          4/1/2019

Description:   SocketTables provide a socket based communication protocol
               for performing simple CRUD (Create, Read, Update, Delete)
               operations.  SocketTables are designed to use JSON messages
               to provide access to an in-memory, key-value data map.
----------------------------------------------------------------------------
"""

import socket
import selectors
import types
from socketTableData import SocketTableData


HOST = '0.0.0.0'
PORT = 7777


class SocketTableServer:

    # Packet size in bytes
    PACKET_SIZE = 1024

    # Data received from client connections
    socketTableData = SocketTableData()

    # Selector for handling incoming socket connections
    sel = selectors.DefaultSelector()

    def __init__(self, host=HOST, port=PORT):
        self.host = host
        self.port = port
        self.startSocket()
        self.process = True

    def startSocket(self):
        """
        Open a socket for client communication and register with the selector.
        """

        self.lsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.lsock.bind((self.host, self.port))
        self.lsock.listen()
        print("Listening on: ", (self.host, self.port))
        self.lsock.setblocking(False)
        self.sel.register(self.lsock, selectors.EVENT_READ, data=None)

    def processConnections(self):
        """
        Continuously process I/O events and handle the connections accordingly.
        """

        try:
            while (self.process):
                # Get the I/O events to process
                events = self.sel.select(timeout=None)
                for key, mask in events:
                    if key.data is None:
                        # New socket connection
                        self.acceptWrapper(key.fileobj)
                    else:
                        # Existing socket connection to read/write
                        self.serviceConnection(key, mask)

        except KeyboardInterrupt:
            print("Caught keyboard interrupt.  Exiting...")
        finally:
            self.sel.close()

    def acceptWrapper(self, sock):
        """
        Accept a new socket connection from a client.
        """

        conn, addr = sock.accept()
        print("Accepted connection from: ", addr)
        conn.setblocking(False)

        data = types.SimpleNamespace(addr=addr, response=None, sent=False)

        events = selectors.EVENT_READ | selectors.EVENT_WRITE
        self.sel.register(conn, events, data=data)

    def serviceConnection(self, key, mask):
        """
        Process data for an open socket connection.
        """

        sock = key.fileobj
        data = key.data
        if mask & selectors.EVENT_READ:
            # Socket is ready to read data from
            message = sock.recv(self.PACKET_SIZE)
            if message:
                data.response = self.socketTableData.handleMessage(message)
                data.sent = False
            else:
                print("Closing connection to: ", data.addr)
                self.sel.unregister(sock)
                sock.close()
        if mask & selectors.EVENT_WRITE:
            # Socket is ready to write data to
            if (data.response != None and not data.sent):
                print("Responding with value: ", repr(data.response), "to: ", data.addr)
                sent = sock.send(data.response)
                if (sent):
                    data.sent = True

    def stop(self):
        """
        Stop processing I/O events.
        """

        self.process = False


def main():
    server = SocketTableServer()
    server.processConnections()


if __name__ == "__main__":
    main()
