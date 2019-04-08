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
        String value = defaultValue;
        if (data.containsKey(key)) {
            value = this.data.get(key);
        }
        return value;
    }

    public synchronized int getInt(String key, int defaultValue) {
        int value = defaultValue;
        if (this.data.containsKey(key)) {
            String response = data.get(key);
            try {
                value = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                System.out.println("Couldn't parse int.");
            }
        }
        return value;
    }

    public synchronized double getDouble(String key, double defaultValue) {
        double value = defaultValue;
        if (this.data.containsKey(key)) {
            String response = data.get(key);
            try {
                value = Double.parseDouble(response);
            } catch (NumberFormatException e) {
                System.out.println("Couldn't parse double.");
            }
        }
        return value;
    }

    public synchronized boolean getBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (this.data.containsKey(key)) {
            String response = data.get(key);
            value = Boolean.parseBoolean(response);
        }
        return value;
    }

    public synchronized String updateString(String key, String value) {
        this.data.put(key, value);
        return value;
    }

    public synchronized int updateInt(String key, int value) {
        this.data.put(key, Integer.toString(value));
        return value;
    }

    public synchronized double updateDouble(String key, double value) {
        this.data.put(key, Double.toString(value));
        return value;
    }

    public synchronized boolean updateBoolean(String key, boolean value) {
        this.data.put(key, Boolean.toString(value));
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
