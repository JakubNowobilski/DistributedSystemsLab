import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class MongoService {
    private static volatile MongoService instance = null;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    private MongoService(){
        System.setProperty("org.slf4j.simpleLogger.log.org.mongodb", "off");
        if(instance != null){
            throw new RuntimeException("Not allowed. Pleas use getInstance()");
        }
        connect();
    }

    public static MongoService getInstance() {
        if(instance == null){
            synchronized(MongoService.class){
                if(instance == null)
                    instance = new MongoService();
            }
        }
        return instance;
    }

    private void connect(){
        this.mongoClient = MongoClients.create("mongodb://127.0.0.1:27017");
        this.db = mongoClient.getDatabase("AstraLink");
        this.collection = db.getCollection("satellite");
    }

    public synchronized void add_error(int sat_id){
        this.collection.updateOne(eq("sat_id", sat_id), inc("errors_count", 1));
    }

    public int get_errors(int sat_id){
        Object errors_count = this.collection.find(eq("sat_id", sat_id)).first().get("errors_count");
        return (int) errors_count;
    }

    public void initCollection(){
        this.drop();
        this.db = mongoClient.getDatabase("AstraLink");
        this.collection = db.getCollection("satellite");

        List<Document> documents = new ArrayList<Document>();
        for (int i = 100; i < 200; i++){
            documents.add(new Document("sat_id", i)
                    .append("errors_count", 0));
        }
        collection.insertMany(documents);
    }

    public void drop(){
        this.db.drop();
    }
}
