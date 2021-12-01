package mongodb;

import org.bson.BsonType;
import org.bson.codecs.BsonTypeClassMap;

import java.util.ArrayList;
import java.util.List;

public abstract class Util {
    public static final String MONGO_ID_KEY = "_id";
    public static final String MONGO_SET_COMMAND = "$set";
    public static final List<Class<?>> MONGO_DATA_TYPES = new ArrayList<>();
    public static final List<String> MONGO_DATA_TYPES_STRINGED = new ArrayList<>();

    static {
        // small trick to "access" the private field (actually just to copy).
        // Incapsulation? Information hiding? Whatcha talking about?
        // I just don't wanna add these possible mongo classes manually and found the class that already contains them
        // so why not
        BsonType[] bsonTypes = BsonType.values();
        BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap();
        for (int i = 0; i < bsonTypes.length; i++) {
            Class<?> clazz = bsonTypeClassMap.get(bsonTypes[i]);
            // have we not found the matching class and it's null?
            if (clazz == null) {
                // yes so we can't meet this bsontype as a class in java. Let's try another type...
                continue;
            }
            MONGO_DATA_TYPES.add(clazz);
            MONGO_DATA_TYPES_STRINGED.add(clazz.getSimpleName());
        }
    }
}
