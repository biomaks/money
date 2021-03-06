package money.logic;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.Set;
import java.util.TreeMap;

/**
 * author: erik
 */
public class AccountsDAO {
    private final DBCollection accountsCollection;

    public AccountsDAO(final DB moneyDB) {
        accountsCollection = moneyDB.getCollection("accounts");
    }

    public boolean addAccount(String userId, String accountName) {
        DBObject account = new BasicDBObject("user", userId)
                .append("accountName", accountName)
                .append("isActive", true);
        try {
            accountsCollection.insert(account);
            return true;
        } catch (MongoException.DuplicateKey mdr) {
            System.out.println("Duplicate account for user " + mdr);
            return false;
        }
    }

    public TreeMap getAccounts(String userId) {
        DBObject query = new BasicDBObject("user", userId)
                .append("isActive", true);
        TreeMap map = new TreeMap();
        DBCursor cursor = accountsCollection.find(query);
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            map.put(object.get("_id").toString(), object.get("accountName"));
        }

        return map;
    }

    public void deleteAccounts(String username, Set<String> accountsToDelete) {   //todo
        BasicDBList list = new BasicDBList();
        for (String id : accountsToDelete) {
            list.add(new ObjectId(id));
        }

        DBObject query = new BasicDBObject("user", username).append("_id", new BasicDBObject("$in", list));
        accountsCollection.update(query, new BasicDBObject("$set", new BasicDBObject("isActive", false)), false, true);
    }
}
