package mongodb;

import com.mongodb.*;

import java.net.UnknownHostException;

public class Connection {
    private static class ConnectionInstanceHolder {
        private static final Connection INSTANCE = new Connection();
    }

    private Connection() {}

    public static Connection getInstance() {
        return ConnectionInstanceHolder.INSTANCE;
    }

    private MongoClient mongoClient;

    public void connect(String fullIp) throws UnknownHostException {
        if (mongoClient != null) {
            System.out.println("Already connected.");
            return;
        }
        mongoClient = new MongoClient(new MongoClientURI(fullIp));
    }

    public String testCmd() {
        StringBuilder result = new StringBuilder();
        DBCursor dbCursor = mongoClient.getDB("db").getCollection("use5rs").find();
        for (DBObject dbObject : dbCursor) {
            result.append(dbObject).append("\n");
        }
        return result.toString();
    }
}
