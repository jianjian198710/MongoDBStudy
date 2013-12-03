package mongo.simple.crud;

import java.util.Random;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryOperators;
import com.mongodb.WriteConcern;

/*
 * ѧϰ�������Զ����CURD�÷�
 * ��ӦDB CurdMongoDB2
 */
public class CurdMongoDB2{

	private Mongo mongo = null;
	private DB db = null;
	private Datastore ds = null;
	
	private Morphia morphia = null;
	private DBCollection sensors;
	private DBCollection datas;
	
	private static Random rand = new Random(47);
	
	private Sensor sensor1;
	private Sensor sensor2;

	
	@Before
	public void init() throws Exception{
		mongo = new Mongo();
		db = mongo.getDB("CurdMongoDB2");
		sensors = db.getCollection("sensors");
		datas = db.getCollection("datas");
		
		morphia = new Morphia();
		ds = morphia.createDatastore(mongo, "CurdMongoDB2");
		morphia.map(Sensor.class);
		morphia.map(Data.class);
		
		sensor1 = new Sensor();
		sensor2 = new Sensor();
		sensor1.setSensorId("sensor01");
		sensor1.setObserveProperty("temperature");
		sensor2.setSensorId("sensor02");
		sensor2.setObserveProperty("humidity");
	}
	
	@Test
	public void addData(){
		
		ds.save(sensor1, WriteConcern.SAFE);
		ds.save(sensor2, WriteConcern.SAFE);
		
		for(int i=0;i<10;i++){
			Data data1 = new Data();
			data1.setSensor(sensor1);
			int value = rand.nextInt(100);
			data1.setValue(value);
			ds.save(data1, WriteConcern.SAFE);
		}
		
		for(int i=0;i<10;i++){
			Data data2 = new Data();
			data2.setSensor(sensor2);
			int value = rand.nextInt(100);
			data2.setValue(value);
			ds.save(data2, WriteConcern.SAFE);
		}
	}
	
	@Test
	public void removeData(){
		sensors.remove(new BasicDBObject());
		datas.remove(new BasicDBObject());
	}
	
	//ͨ��java-driver���渴�϶���,������Morphia��������ClassName����
	@Test
	public void addComplex(){
		DBObject data = new BasicDBObject();
		DBObject sensor = new BasicDBObject();
		
		sensor.put("sensorId", "sensor01");
		sensor.put("observeProperty", "temperature");
		data.put("sensor", sensor);
		data.put("value", 200);
		
		datas.save(data, WriteConcern.SAFE);
	}
	
	//$set,��ֵС��50��data��ֵȫ��update��300
	@Test
	public void modifySet(){
		DBObject data = new BasicDBObject();
		DBObject updatedData = new BasicDBObject();
		
		data.put("value", 300);
		//�ֲ����� ֻ����ԭ��λ���ϵ�value����,�����Ǹ��������ĵ�,����һ��Ҫ��һ��������ΪudpateOperator����
		updatedData.put("$set", data);
		System.out.println(datas.update(new BasicDBObject("value",new BasicDBObject(QueryOperators.LT,50)),updatedData,false,true));
	}
	
	//$inc,��data��С��50��ֵȫ������300
	@Test
	public void modifyInc(){
		DBObject data = new BasicDBObject();
		DBObject updatedData = new BasicDBObject();
		
		data.put("value", 300);
		updatedData.put("$inc", data);
		System.out.println(datas.update(new BasicDBObject("value",new BasicDBObject(QueryOperators.LT,100)),updatedData,false,true));
	}
	
	//$unset,ɾ��data��value>50��valueֵ
	@Test
	public void modifyUnset(){
		DBObject data = new BasicDBObject();
		DBObject updatedData = new BasicDBObject();
		
		data.put("value",1);
		updatedData.put("$unset",data);
		System.out.println(datas.update(new BasicDBObject("value",new BasicDBObject(QueryOperators.GT,50)),updatedData,false,true));
	}
	/*
	 * $unset,������²���
	 */
	@Test
	public void modifyUnset2(){
		DBObject data = new BasicDBObject();
		DBObject sensor = new BasicDBObject();
		DBObject updatedData = new BasicDBObject();
		
		sensor.put("sensorId", "sensor01");
		sensor.put("observeProperty", "temperature");
		
		int[] values = {100,200,300};
		data.put("sensor", sensor);
		data.put("value", values);
		datas.save(data);
		
		updatedData.put("$unset", new BasicDBObject("value.0",1));
		
		//value��һ������ ͨ��unset������ĵ�һ����ֵ��Ϊnull
		datas.update(new BasicDBObject("value",values), updatedData,false,true);
	}

	//$pop,ɾ������Ԫ��
	@Test
	public void modifyPop(){
		DBObject data = new BasicDBObject();
		DBObject sensor = new BasicDBObject();
		DBObject updatedData = new BasicDBObject();
		DBObject updatedData2 = new BasicDBObject();
		
		sensor.put("sensorId", "sensor01");
		sensor.put("observeProperty", "temperature");
		
		int[] values = {100,200,300};
		data.put("sensor", sensor);
		data.put("value", values);
		data.put("tag", "ok");
		datas.save(data);
		
		//ɾ����һ��
		updatedData.put("$pop", new BasicDBObject("value",-1));
		datas.update(new BasicDBObject("sensor",sensor), updatedData,false,true);

		//ɾ�����һ��
		updatedData2.put("$pop", new BasicDBObject("value",1));
		datas.update(new BasicDBObject("sensor",sensor), updatedData2,false,true);
	}
	
	//$in��ѯ
	@Test
	public void findIn(){
		DBObject queryData = new BasicDBObject();
		int[] nums = {98,93};
		queryData.put("$in",nums);
		System.out.println(datas.find(new BasicDBObject("value",queryData)).toArray());
	}
	
	//$And��ѯ �������ѯ����ͬʱput��һ����ѯDBObject�Ϳ�
	@Test
	public void findAnd(){
		DBObject queryData = new BasicDBObject();
		DBObject queryData2 = new BasicDBObject();
		
		queryData.put("$in",new String[]{"ok","nok"});
		queryData2.put("value.0", 200);
		queryData2.put("tag", queryData);
		System.out.println(datas.find(queryData2).toArray());
	}
	
	//ͨ����Ƕ�����ĳһ���Բ�ѯ
	@Test
	public void findEmbbed(){
		System.out.println(datas.find(new BasicDBObject("sensor.observeProperty","temperature")).count());
	}
	
	//$whereͨ��where ʹ��JSֱ�Ӳ�ѯ
	@Test
	public void findWhere(){
		DBObject queryData = new BasicDBObject();
		queryData.put("$where", "this.value>90");
		System.out.println(datas.find(queryData).sort(new BasicDBObject("value",-1)).toArray());
	}
	
	@Test
	public void findRegex(){
		DBObject queryData = new BasicDBObject();
		queryData.put("$regex", "^ok$");
		System.out.println(datas.find(new BasicDBObject("tag",queryData)).toArray());
	}
	
	@After
	public void destroy(){
		if(mongo!=null){
			mongo.close();
		}
		mongo = null;
		morphia = null;
		ds = null;
		db = null;
		sensors = null;
		datas = null;
	}
}













@Entity("datas")
class Data{
	@Id
	ObjectId id;
	
	private double value;
	//ӳ�䵽ĳ��Sensor
	@Embedded
	private Sensor sensor;
	public double getValue(){
		return value;
	}
	public void setValue(double value){
		this.value = value;
	}
	public Sensor getSensor(){
		return sensor;
	}
	public void setSensor(Sensor sensor){
		this.sensor = sensor;
	}

	public String toString(){
		return "value:"+value;
	}
}

@Entity("sensors")
class Sensor {
	@Id
	private String sensorId;
	private String observeProperty;
	public String getSensorId(){
		return sensorId;
	}
	public void setSensorId(String sensorId){
		this.sensorId = sensorId;
	}
	public String getObserveProperty(){
		return observeProperty;
	}
	public void setObserveProperty(String observeProperty){
		this.observeProperty = observeProperty;
	}
	
	public String toString(){
		return "sensorId: "+sensorId+", observeProperty: "+observeProperty;
	}
}