package custominput.mdb.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import custominput.mdb.parameters.MDBParameterPattern;
import custominput.mdb.parameters.MDBParametersPattern;
import custominput.mdb.parameters.ParameterSearchPlace;
import custominput.mdb.parameters.MDBParameters;
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
                        ParameterSearchPlace.NEXT_WORD,
                        new MDBParameterPattern(String.class, false)
                ),
                MDBCommandPattern.IGNORED_CHILD_COMMANDS_ACCESS_DELIMITER
        ) {
            @Override
            public Object apply(MDBParameters params) {
                Object dbName = params.useAndGetParameter();
                Connection.getInstance().setCurrentDatabase(dbName.toString());
                return "switched to " + dbName.toString();
            }
        };
        AVAILABLE_MDB_COMMANDS.add(parentMDBC);

        parentMDBC = new MDBCommandPattern(
                MongoDatabase.class,
                "db",
                MDBParametersPattern.NO_PARAMS,
                '.'
        ) {
            @Override
            public Object apply(MDBParameters params) {
                return Connection.getInstance().getMongoClient().getDatabase(
                        Connection.getInstance().getCurrentDatabaseName()
                );
            }
        };
        AVAILABLE_MDB_COMMANDS.add(parentMDBC);

        childMDBC = new MDBCommandPattern(
                MongoCollection.class,
                MDBCommandPattern.COMMAND_AS_PARAMETER,
                new MDBParametersPattern(
                        ParameterSearchPlace.COMMAND_AS_PARAMETER,
                        new MDBParameterPattern(String.class, false)
                ),
                '.'
        ) {
            @Override
            public Object apply(MDBParameters params) {
                Object collectionName = params.useAndGetParameter();
                MongoDatabase mongoDatabase = (MongoDatabase) getParent().apply(null);
                return mongoDatabase.getCollection(collectionName.toString());
            }
        };
        AVAILABLE_MDB_COMMANDS.add(childMDBC);

        parentMDBC.addAvailableChildCommand(childMDBC);

        parentMDBC = childMDBC;
        childMDBC = new MDBCommandPattern(
                FindIterable.class,
                "find",
                new MDBParametersPattern(
                        ParameterSearchPlace.BRACKETS,
                        new MDBParameterPattern(Bson.class, true),
                        new MDBParameterPattern(Bson.class, true)
                ),
                '.'
        ) {
            @Override
            public Object apply(MDBParameters params) {
                Object query = params.useAndGetParameter();
                Object projection = params.useAndGetParameter();
                MongoCollection<Document> collection = (MongoCollection<Document>) getParent().apply(params);
                FindIterable<Document> findIterable;
                if (query == null) {
                    findIterable = collection.find();
                } else {
                    findIterable = collection.find((Bson) query);
                }
                if (projection != null) {
                    findIterable = findIterable.projection((Bson) projection);
                }
                return findIterable;
            }
        };
        AVAILABLE_MDB_COMMANDS.add(childMDBC);

        parentMDBC.addAvailableChildCommand(childMDBC);
    }
}
