package mongo.simple.crud;

import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class ConnectMongoDB {

	@Test
	public void test() throws UnknownHostException,MongoException{
		Mongo mongo = new Mongo();
		//GetDB
		DB db = mongo.getDB("tutorial");
		//Get collections
		DBCollection dbc = db.getCollection("users");
		//find
		DBCursor cur = dbc.find();
	    while (cur.hasNext()) {
	    	System.out.println(cur.next());
	    }
	    Assert.assertEquals(2, cur.count());
	    //ʹ��JSON��ʽ����ѯ���
	    System.out.println(JSON.serialize(cur));
	}

}
