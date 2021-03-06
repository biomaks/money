package money.logic;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.Set;
import java.util.TreeMap;

/**
 * author: erik
 */
public class CategoriesDAO {

    private final DBCollection categoriesDAOCollection;

    public CategoriesDAO(final DB mongoDB) {
        categoriesDAOCollection = mongoDB.getCollection("categories");
    }

    public boolean addCategory(String userId, String categoryName, Boolean isIncome) {
        DBObject category = new BasicDBObject("user", userId)
                .append("categoryName", categoryName)
                .append("isIncome", isIncome)
                .append("isActive", true);
        try {
            categoriesDAOCollection.insert(category);
            return true;
        } catch (MongoException.DuplicateKey mdr) {
            System.out.println("Duplicate category for user " + mdr);
            return false;
        }
    }

    public TreeMap getCategories(String userId, Boolean isIncome) {
        DBObject query = new BasicDBObject("user", userId)
                .append("isIncome", isIncome)
                .append("isActive", true);
        TreeMap map = new TreeMap();
        DBCursor cursor = categoriesDAOCollection.find(query);
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            map.put(object.get("_id").toString(), object.get("categoryName"));
        }

        return map;
    }

    public void deleteCategories(String username, Set<String> categoriesToDelete) {
        BasicDBList list = new BasicDBList();

        for (String id : categoriesToDelete) {
            list.add(new ObjectId(id));
        }

        DBObject inq = new BasicDBObject();
        DBObject query = new BasicDBObject();
        inq.put("$in", list);
        query.put("_id", inq);
        query.put("user", username);
        query.put("isActive", true);

        categoriesDAOCollection.update(query, new BasicDBObject("$set", new BasicDBObject("isActive", false)), false, true);
    }


}
