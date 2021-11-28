package mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class Connection {
    private static class ConnectionInstanceHolder {
        private static final Connection INSTANCE = new Connection();
    }

    private Connection() {
    }

    public static Connection getInstance() {
        return ConnectionInstanceHolder.INSTANCE;
    }

    public boolean readyToWork() {
        return mongoClient != null;
    }

    private MongoClient mongoClient;

    public void connect(String fullIp) {
        if (mongoClient != null) {
            System.out.println("Already connected.");
            return;
        }
        System.out.println("Connected successfully.");
        mongoClient = MongoClients.create(fullIp);
    }

    public void disconnect() {
        if (mongoClient == null) {
            return;
        }

        mongoClient.close();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
