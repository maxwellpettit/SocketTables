package socket.table.server;

import java.util.HashMap;
import java.util.Map;

public class SocketTableData {

    private Map<String, String> data;

    public SocketTableData() {
        reset();
    }

    public void reset() {
        this.data = new HashMap<>();
    }

    public String getString(String key, String defaultValue) {
        String value = null;
        if (this.data.containsKey(key)) {
            value = this.data.get(key);
        }

        return value;
    }

}