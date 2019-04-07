package socket.table.server;

import java.util.HashMap;
import java.util.Map;

public class SocketTableData {

    private Map<String, String> data = new HashMap<>();

    public SocketTableData() {
    }

    public synchronized void reset() {
        this.data = new HashMap<>();
    }

    public synchronized String getString(String key, String defaultValue) {
        String value = null;
        if (this.data.containsKey(key)) {
            value = this.data.get(key);
        }

        return value;
    }

    public synchronized String updateString(String key, String value) {
        this.data.put(key, value);
        return value;
    }

    public synchronized String delete(String key) {
        String value = null;
        if (this.data.containsKey(key)) {
            value = this.data.remove(key);
        }

        return value;
    }

}