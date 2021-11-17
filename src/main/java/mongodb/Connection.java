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

    public boolean readyToWork() {
        return mongoClient != null;
    }

    private MongoClient mongoClient;

    public void connect(String fullIp) throws UnknownHostException {
        if (mongoClient != null) {
            System.out.println("Already connected.");
            return;
        }
        System.out.println("Connected successfully.");
        mongoClient = new MongoClient(new MongoClientURI(fullIp));
    }

    public String testCmd() {
        StringBuilder result = new StringBuilder();
        DB db = mongoClient.getDB("amogus");
        DBCollection dbCollection = db.getCollection("users");
        DBCursor dbCursor = dbCollection.find();
        for (DBObject dbObject : dbCursor) {
            result.append(dbObject).append("\n");
        }
        return result.toString();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
