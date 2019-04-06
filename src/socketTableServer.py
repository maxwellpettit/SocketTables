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

import asyncio
import threading
import time
from protocol import SocketTableData


HOST = '0.0.0.0'
PORT = 7777


class SocketTableServer:

    # Packet size in bytes
    PACKET_SIZE = 1024

    # Data received from client connections
    socketTableData = SocketTableData()

    def __init__(self, host=HOST, port=PORT):
        self.host = host
        self.port = port

    async def handleMessage(self, reader, writer):
        """
        Wait for an incoming connection, read the message, and send the response.
        """

        # Read the incoming message
        encodedMessage = await reader.read(self.PACKET_SIZE)

        addr = writer.get_extra_info('peername')
        print('Connected to:', repr(addr))

        # Generate the message response
        response = self.socketTableData.handleMessage(encodedMessage)

        # Write the response to the client
        writer.write(response)
        await writer.drain()

        writer.close()

    def startServer(self, loop: asyncio.AbstractEventLoop):
        """
        Continuously process I/O events and handle the connections accordingly.
        """

        server = asyncio.start_server(self.handleMessage, self.host, self.port, loop=loop)
        task = loop.run_until_complete(server)

        print('Serving on:', repr(task.sockets[0].getsockname()))

        # Continuously run the async loop
        try:
            loop.run_forever()
        except KeyboardInterrupt:
            print("Caught keyboard interrupt.  Exiting...")

        # Close the server
        task.close()
        loop.run_until_complete(task.wait_closed())
        loop.close()


def main():
    # Create the SocketTableServer
    server = SocketTableServer()

    # Create a thread for handling async connections
    loop = asyncio.get_event_loop()
    t = threading.Thread(target=server.startServer, args=(loop,))

    # Allow the thread to close when main() ends
    t.setDaemon(True)

    # Start the server thread
    t.start()

    # Wait for the thread to complete (optional)
    try:
        t.join()
    except KeyboardInterrupt:
        print("Caught keyboard interrupt.  Exiting...")


if __name__ == "__main__":
    main()
