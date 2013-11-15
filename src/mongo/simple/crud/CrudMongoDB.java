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
		//存单个对象
		DBObject user = new BasicDBObject();
		user.put("name","Tom");
		user.put("sex","male");
		user.put("age", 24);
		users.insert(user);	
		
		//存多个数据
		users.insert(new BasicDBObject("name","Peter"),new BasicDBObject("name","Alice"));
		
		//存List
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
		System.out.println("操作前: ");
		queryAll();
		
		//删除name属性带Mark的
		users.remove(new BasicDBObject("name","Mark"));
		//删除age>=23的 $gt大于 $lt<
		users.remove(new BasicDBObject("age",new BasicDBObject("$gte", 23)));
		
		System.out.println("操作后: ");
		queryAll();
		users.remove(new BasicDBObject());
	}
	
	@Test
	public void modify(){
		add();
		System.out.println("操作前: ");
		queryAll();
		
		DBObject user  = new BasicDBObject("name","Peter");
		user.put("age", 100);
		//将name Peter增加一项 age 100
		users.update(new BasicDBObject("name","Peter"),user,true, false);
		//将id=5279d16a6fdca671e825557a的对象属性改为age 100 没有则增加该对象
		users.update(new BasicDBObject("_id", new ObjectId("5279d16a6fdca671e825557a")),new BasicDBObject("age",100), true, false);
		
		System.out.println("操作后: ");
		queryAll();
		users.remove(new BasicDBObject());
	}
	
	@Test
	public void query(){
		add();
		queryAll();
		//查询指定id
//		System.out.println("find selected id: "+users.find(new BasicDBObject("_id",new ObjectId("5279de430be9a671ae6826df"))).toArray());
		System.out.println("find age=24: "+users.find(new BasicDBObject("age",24)).toArray());
		System.out.println("find age>=30: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.GTE,30))).toArray());
		System.out.println("find age<=30: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.LTE,30))).toArray());
		System.out.println("find age!=24: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.NE,24))).toArray());
		System.out.println("find age in 24/25/26: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.IN,new int[]{24,25,26}))).toArray());
		System.out.println("find age not in 24/25/26: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.NIN,new int[]{24,25,26}))).toArray());
		System.out.println("age exist 排序: "+users.find(new BasicDBObject("age",new BasicDBObject(QueryOperators.EXISTS,true))).toArray());
		//不符合条件只显示id
		System.out.println("只查询age属性: "+users.find(null,new BasicDBObject("age",24)).toArray());
		//不符合条件不显示
		System.out.println("只查询age属性并要求对象本来有age属性: "+users.find(new BasicDBObject("age",24),new BasicDBObject("age",true)).toArray());
		
		//只查询一条数据，多条去第一条
		System.out.println("findOne: " + users.findOne());
		System.out.println("findOne age=24: " + users.findOne(new BasicDBObject("age", 24)));
		//不符合条件不显示 注意与null的差别
		System.out.println("findOne age=24 并且只显示其name: " + users.findOne(new BasicDBObject("age", 24), new BasicDBObject("name", true)));
		
		//只删除第一条
		System.out.println("find and remove age=24: "+users.findAndRemove(new BasicDBObject("age",24)));
		//只更改第一条
		System.out.println("find and age=24 and modify to name=ABC: "+users.findAndModify(new BasicDBObject("age",24), new BasicDBObject("name","ABC")));
		//TODO research
		System.out.println("find age=40 and modify to name=Abc: " + users.findAndModify(
			        new BasicDBObject("age", 40), //查询age=28的数据
			        new BasicDBObject("name", true), //查询name属性
			        new BasicDBObject("age", true), //查询age属性
			        false, //是否删除，true表示删除
			        new BasicDBObject("name", "Abc"), //修改的值，将name修改成Abc
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
		
		 //设置db为只读
	    db.setReadOnly(true);
	    users.save(user);
	}
	
	@Test
	public void other2(){
		//从id获取时间 因为id包含时间戳
		ObjectId oi =  new ObjectId("5279de430be9a671ae6826df");
		System.out.println(oi.getTime());
	}

}
