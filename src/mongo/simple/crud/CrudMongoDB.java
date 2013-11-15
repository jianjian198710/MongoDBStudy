package mongo.simple.crud;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryOperators;
import com.mongodb.util.JSON;

public class CrudMongoDB {
	private Mongo mongo = null;
	private DB db;
	private DBCollection users;

	@Before
	public void init() throws Exception{
		mongo = new Mongo();
		db = mongo.getDB("tutorial");
		users = db.getCollection("users");
	}
	
	@After
	public void destroy(){
		if(mongo!=null){
			mongo.close();
		}
		mongo = null;
		db = null;
		users = null;
	}
	
	private void queryAll(){
		DBCursor cur = users.find();
		while(cur.hasNext()){
			System.out.println(cur.next());
		}
	}
	
	@Test
	public void add() {
		//�浥������
		DBObject user = new BasicDBObject();
		user.put("name","Tom");
		user.put("sex","male");
		user.put("age", 24);
		users.insert(user);	
		
		//��������
		users.insert(new BasicDBObject("name","Peter"),new BasicDBObject("name","Alice"));
		
		//��List
		List<DBObject> list = new ArrayList<DBObject>();
		DBObject user2 = new BasicDBObject();
		user2.put("name","Mark");
		user2.put("sex","male");
		user2.put("age", 24);
		DBObject user3 = new BasicDBObject();
		user3.put("name","lucy");
		user3.put("age", 40);
		list.add(user2);
		list.add(user3);
		users.insert(list);
	}
	
	@Test		
	public void remove(){
		add();
		System.out.println("����ǰ: ");
		queryAll();
		
		//ɾ��name���Դ�Mark��
		users.remove(new BasicDBObject("name","Mark"));
		//ɾ��age>=23�� $gt���� $lt<
		users.remove(new BasicDBObject("age",new BasicDBObject("$gte", 23)));
		
		System.out.println("������: ");
		queryAll();
		users.remove(new BasicDBObject());
	}
	
	@Test
	public void modify(){
		add();
		System.out.println("����ǰ: ");
		queryAll();
		
		DBObject user  = new BasicDBObject("name","Peter");
		user.put("age", 100);
		//��name Peter����һ�� age 100
		users.update(new BasicDBObject("name","Peter"),user,true, false);
		//��id=5279d16a6fdca671e825557a�Ķ������Ը�Ϊage 100 û�������Ӹö���
		users.update(new BasicDBObject("_id", new ObjectId("5279d16a6fdca671e825557a")),new BasicDBObject("age",100), true, false);
		
		System.out.println("������: ");
		queryAll();
		users.remove(new BasicDBObject());
	}
	
	@Test
	public void query(){
		add();
		queryAll();
		//��ѯָ��id
//		System.out.println("find selected id: "+users.find(new BasicDBObject("_id",new ObjectId("5279de430be9a671ae6826df"))).toArray());
		System.out.println("find age=24: "+users.find(new BasicDBObject("age",24)).toArray());
		System.out.println("find age>=30: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.GTE,30))).toArray());
		System.out.println("find age<=30: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.LTE,30))).toArray());
		System.out.println("find age!=24: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.NE,24))).toArray());
		System.out.println("find age in 24/25/26: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.IN,new int[]{24,25,26}))).toArray());
		System.out.println("find age not in 24/25/26: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.NIN,new int[]{24,25,26}))).toArray());
		System.out.println("age exist ����: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.EXISTS,true))).toArray());
		//����������ֻ��ʾid
		System.out.println("ֻ��ѯage����: "+users.find(null,new BasicDBObject("age",24)).toArray());
		//��������������ʾ
		System.out.println("ֻ��ѯage���Բ�Ҫ���������age����: "+users.find(new BasicDBObject("age",24),new BasicDBObject("age",true)).toArray());
		
		//ֻ��ѯһ�����ݣ�����ȥ��һ��
		System.out.println("findOne: " + users.findOne());
		System.out.println("findOne age=24: " + users.findOne(new BasicDBObject("age", 24)));
		//��������������ʾ ע����null�Ĳ��
		System.out.println("findOne age=24 ����ֻ��ʾ��name: " + users.findOne(new BasicDBObject("age", 24), new BasicDBObject("name", true)));
		
		//ֻɾ����һ��
		System.out.println("find and remove age=24: "+users.findAndRemove(new BasicDBObject("age",24)));
		//ֻ���ĵ�һ��
		System.out.println("find and age=24 and modify to name=ABC: "+users.findAndModify(new BasicDBObject("age",24), new BasicDBObject("name","ABC")));
		//TODO research
		System.out.println("find age=40 and modify to name=Abc: " + users.findAndModify(
			        new BasicDBObject("age", 40), //��ѯage=28������
			        new BasicDBObject("name", true), //��ѯname����
			        new BasicDBObject("age", true), //��ѯage����
			        false, //�Ƿ�ɾ����true��ʾɾ��
			        new BasicDBObject("name", "Abc"), //�޸ĵ�ֵ����name�޸ĳ�Abc
			        true, 
			        true));
		
		queryAll();
		users.remove(new BasicDBObject());
	}
	
	@Test
	public void other(){
		DBObject user = new BasicDBObject();
		user.put("name", "Author");
		user.put("age", 30);
		
		System.out.println("JSON Serialize: "+JSON.serialize(user));
		System.out.println("JSON Externalize: "+JSON.parse("{ \"name\" : \"hoojo\" , \"age\" : 24}"));
		
		 //����dbΪֻ��
	    db.setReadOnly(true);
	    users.save(user);
	}
	
	@Test
	public void other2(){
		//��id��ȡʱ�� ��Ϊid����ʱ���
		ObjectId oi =  new ObjectId("5279de430be9a671ae6826df");
		System.out.println(oi.getTime());
	}

}
