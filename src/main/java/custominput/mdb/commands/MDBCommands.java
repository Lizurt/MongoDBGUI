package custominput.mdb.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import custominput.mdb.ChildDelimiter;
import custominput.mdb.parameters.*;
import mongodb.Connection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public abstract class MDBCommands {
    public static final List<MDBCommandPattern> AVAILABLE_MDB_COMMANDS = new ArrayList<>();

    // todo: add an ability to have multiple connections, so we might loose that simple singleton access
    static {
        MDBCommandPattern parentMDBC;
        MDBCommandPattern childMDBC;

        parentMDBC = new MDBCommandPattern(
                String.class,
                "use",
                new MDBParametersPattern(
                        ParameterSearchPlace.FORWARD,
                        ParameterScanStartStopDelimiter.SPACE,
                        ParameterDelimiter.NOTHING,
                        new MDBParameterPattern(String.class, false)
                ),
                ChildDelimiter.IGNORED_DELIMITER
        ) {
            @Override
            public MDBCommandResult apply(MDBParameters params) {
                Object dbName = params.useAndGetParameter();
                Connection.getInstance().setCurrentDatabase(dbName.toString());
                return new MDBCommandResult(null, "switched to " + dbName.toString());
            }
        };
        AVAILABLE_MDB_COMMANDS.add(parentMDBC);

        parentMDBC = new MDBCommandPattern(
                MongoDatabase.class,
                "db",
                MDBParametersPattern.NO_PARAMS,
                ChildDelimiter.DOT
        ) {
            @Override
            public MDBCommandResult apply(MDBParameters params) {
                MongoDatabase mongoDatabase = Connection.getInstance().getMongoClient().getDatabase(
                        Connection.getInstance().getCurrentDatabaseName()
                );
                return new MDBCommandResult(mongoDatabase, Connection.getInstance().getCurrentDatabaseName());
            }
        };
        AVAILABLE_MDB_COMMANDS.add(parentMDBC);

        childMDBC = new MDBCommandPattern(
                MongoCollection.class,
                MDBCommandPattern.COMMAND_AS_PARAMETER,
                new MDBParametersPattern(
                        ParameterSearchPlace.COMMAND_AS_PARAMETER,
                        ParameterScanStartStopDelimiter.NOTHING,
                        ParameterDelimiter.NOTHING,
                        new MDBParameterPattern(String.class, false)
                ),
                ChildDelimiter.DOT
        ) {
            @Override
            public MDBCommandResult apply(MDBParameters params) {
                Object collectionName = params.useAndGetParameter();
                MongoDatabase mongoDatabase = (MongoDatabase) getParent().apply(params).getResult();
                return new MDBCommandResult(
                        mongoDatabase.getCollection(collectionName.toString()),
                        collectionName.toString()
                );
            }
        };
        AVAILABLE_MDB_COMMANDS.add(childMDBC);

        childMDBC.setParent(parentMDBC);

        parentMDBC = childMDBC;
        childMDBC = new MDBCommandPattern(
                FindIterable.class,
                "find",
                new MDBParametersPattern(
                        ParameterSearchPlace.FORWARD,
                        ParameterScanStartStopDelimiter.BRACKETS_FORWARD,
                        ParameterDelimiter.COMMA,
                        new MDBParameterPattern(Bson.class, true),
                        new MDBParameterPattern(Bson.class, true)
                ),
                ChildDelimiter.DOT
        ) {
            @Override
            public MDBCommandResult apply(MDBParameters params) {
                Object query = params.useAndGetParameter();
                Object projection = params.useAndGetParameter();
                MongoCollection<Document> collection = (MongoCollection<Document>) getParent().apply(params).getResult();
                FindIterable<Document> findIterable;
                if (query == null) {
                    findIterable = collection.find();
                } else {
                    findIterable = collection.find((Bson) query);
                }
                if (projection != null) {
                    findIterable = findIterable.projection((Bson) projection);
                }
                StringBuilder humanReadableResult = new StringBuilder();
                for (Document document : findIterable) {
                    humanReadableResult.append(document.toJson()).append("\r\n");
                }
                return new MDBCommandResult(findIterable, humanReadableResult.toString());
            }
        };
        AVAILABLE_MDB_COMMANDS.add(childMDBC);

        childMDBC.setParent(parentMDBC);
    }
}
