package custom_input;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
            public Object apply(List<Object> objects) {
                Connection.getInstance().setCurrentDatabase(objects.get(0).toString());
                return "switched to " + objects.get(0);
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
            public Object apply(List<Object> params) {
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
            public Object apply(List<Object> params) {
                MongoDatabase mongoDatabase = (MongoDatabase) getParent().apply(null);
                return mongoDatabase.getCollection((String) params.get(0));
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
            public Object apply(List<Object> objects) {
                return ((MongoCollection<Document>) getParent().apply(objects)).find((Bson) objects.get(1));
            }
        };
        AVAILABLE_MDB_COMMANDS.add(childMDBC);

        parentMDBC.addAvailableChildCommand(childMDBC);
    }
}
