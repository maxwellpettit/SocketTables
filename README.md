# SocketTables
SocketTables provide a socket based communication protocol for performing simple in-memory CRUD (Create, Read, Update, Delete) operations.

SocketTables are designed to use JSON messages to provide access to a key-value mapping on a Python server.


## Demo Usage
Start the server with the following command:

`python3 src/socketTableServer.py`

Start a demo client with the following command:

`python3 src/socketTableClient.py`

This will continuously get/update/delete values from the server.

## Example Client
The SocketTableClient class can be used to send/receive messages to/from the server.  The Python script below shows an example of how to get/update/delete values from the server. 

```python
from socketTableClient import SocketTableClient

# Server hostname / port
host = '127.0.0.1'
port = 7777

# Start the SocketTableClient
client = SocketTableClient(host, port)

# Key name / default value
key = 'TestKey'
defaultValue = 'Default Value'

# Update the value on the server
client.update(key, 'New Value')

# Get the value from the server
value = client.get(key, defaultValue)

# Delete the value from the server
client.delete(key)
```

## Protocol Format

The SocketTableServer expects JSON formatted string messages (utf-8 encoding).  Any language can be used to send/receive values from the server as long as the client sends can send messages with the proper format.

### Data is stored on the server in the following format:
```
data = {
    key1: value1,
    key2: value2,
    ...
}
```

### Messages can be sent to the server in the following formats:

Getting a value from the server:
```
{
    'request': 'GET',
    'key': 'REQUESTED_KEY'
}
```

Updating a value on the server:
```
{
    'request': 'UPDATE',
    'key': 'REQUESTED_KEY',
    'value': 'NEW_VALUE'
}
```

Deleting a value:
```
{
    'request': 'DELETE',
    'key': 'REQUESTED_KEY'
}
```

### Message responses use the following format:
```
{
    'key': 'REQUESTED_KEY',
    'value': 'CURRENT_VALUE'
}
```
