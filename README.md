# SocketTables
SocketTables provide a socket based communication protocol for performing simple in-memory CRUD (Create, Read, Update, Delete) operations.

SocketTables are designed to use JSON messages to provide access to a key-value mapping on a Java or Python server.

## Demo Python Usage
Start the server with the following command:

`python3 python/socketTableServer.py`

Start a demo client with the following command:

`python3 python/socketTableClient.py`

This will continuously get/update/delete values from the server.

## Example Python Client
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

# Get all values from the server
allValues = client.getAll()
```

## Demo Java Usage
Start the demo server by running the main method of the class:

`src/socket/table/server/SocketTableServer.java`

Start the demo client by running the main method of the class:

`src/socket/table/client/SocketTableClient.java`

This will continuously get/update/delete values from the server.

## Example Java Client
The SocketTableClient class can be used to send/receive messages to/from the server.  The Java snippet below shows an example of how to get/update/delete values from the server.

```java
import java.util.Map;
import socket.table.client.SocketTableClient;

// Start the SocketTableClient
SocketTableClient client = new SocketTableClient();

// Key name / default value
String key = "TestKey";
String defaultValue = "Default Value";

// Update the value on the server
boolean success = client.updateString(key, "New Value");

// Get the value from the server
String value = client.getString(key, defaultValue);

// Delete the value from the server
boolean deleteSuccess = client.delete(key);

// Get all values from the server
Map<String, String> allValues = client.getAll();
```

## Protocol Format

The SocketTableServer expects JSON formatted string messages (utf-8 encoding).  Any language can be used to send/receive values from the server as long as the client can send messages with the proper format.

### Data is stored on the server in the following format:
```
data = {
    KEY_NAME: {
        'value': CURRENT_VALUE,
        'timestamp': LAST_UPDATE_TIME
    },
    ...
}
```

### Messages can be sent to the server in the following formats:

Getting a value from the server:
```
{
    'request': 'GET',
    'key': REQUESTED_KEY
}
```

Updating a value on the server:
```
{
    'request': 'UPDATE',
    'key': REQUESTED_KEY,
    'value': NEW_VALUE
}
```

Deleting a value:
```
{
    'request': 'DELETE',
    'key': REQUESTED_KEY
}
```

Getting all values:
```
{
    'request': 'GETALL'
}
```

### Message responses use the following format:
GET/UPDATE/DELETE:
```
{
    'key': REQUESTED_KEY,
    'value': CURRENT_VALUE
}
```

GETALL:
```
[
    {
        'key': KEY_1,
        'value': VALUE_1
    },
    {
        'key': KEY_2,
        'value': VALUE_2
    },
    ...
]
