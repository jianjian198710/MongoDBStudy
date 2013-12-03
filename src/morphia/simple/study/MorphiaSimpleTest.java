package morphia.simple.study;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.Mongo;

public class MorphiaSimpleTest{
/**
 * @author jianjian
 * morphia是Java MongoDB的ORM框架
 * 学习使用的DB是morphia_test
 * https://github.com/mongodb/morphia
 */

	private Mongo mongo = null;
	private Morphia morphia = null;
	private Datastore ds = null;
	@Before
	public void init() throws Exception{
		mongo = new Mongo();
		morphia = new Morphia();
		ds = morphia.createDatastore(mongo, "morphia_test");
		morphia.map(Sensor.class);
		morphia.map(Data.class);
		
	}
	
	@After
	public void destroy(){
		if(mongo!=null){
			mongo.close();
		}
		mongo = null;
		morphia = null;
		ds = null;
	}
	
	@Test
	public void insert(){
		Sensor sensor = new Sensor();
		sensor.setSensorId("sensor-01");
		sensor.setObserveProperty("Temperature");
		
		Data data = new Data();
		data.setSensor(sensor);
		data.setValue(100);
		

		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongo, "morphia_test");
		
		morphia.map(sensor.getClass());
		morphia.map(data.getClass());
		//保存  会在DB中增加ClassName:包名.类名 
		ds.save(sensor);
		ds.save(data);
	}
	
	@Test
	public void update(){
		UpdateOperations<Data> ops = ds.createUpdateOperations(Data.class).set("value", 200.0);
		ds.update(ds.createQuery(Data.class).filter("value >", 50.0), ops);
	}
	
	@Test
	public void query(){
		
		System.out.println(ds.find(Data.class).get().getSensor().getSensorId());
		
		//Query对象extends QueryResults<T>
		Query<Sensor> sensors1 = ds.find(Sensor.class);
		//fetch返回一个Iterator
		for(Sensor sensor:sensors1.fetch()){
			System.out.println(sensor);
		}
		
		//只获取Sensor的sensorId属性 但是返回的还是对象 只不过这个对象里只有sensorId属性被赋值了
		System.out.println("使用retrievedFields建立条件查询");
		Query<Sensor> sensors2 = ds.createQuery(Sensor.class).retrievedFields(true, "sensorId");
		for(Sensor sensor:sensors2.fetch()){
			System.out.println(sensor);
		}
		
		//使用filter建立条件查询,>之前要有空格,可以建立多个filter表示AND条件
		System.out.println("使用filter建立条件查询");
		Query<Data> datas1 = ds.createQuery(Data.class).filter("value >", 1);
		for(Data data:datas1.fetch()){
			System.out.println(data);
		}
		
		//使用field方法建立条件查询
		System.out.println("使用field方法建立条件查询");
		Query<Data> data2 = ds.createQuery(Data.class).field("value").equal(100.0);
		for(Data data:data2.fetch()){
			System.out.println(data);
		}
	}
	
	@Test
	public void findEmbedded(){
		System.out.println("根据内嵌对象查询");
		Query<Data> data2 = ds.createQuery(Data.class).field("sensor.observeProperty").equal("Temperature");
		for(Data data:data2.fetch()){
			System.out.println(data);
		}
	}
	
	
	@Test
	public void remove(){
		ds.delete(ds.createQuery(Sensor.class));
		ds.delete(ds.createQuery(Data.class));
	}
}

//指定到映射的Collection
@Entity("datas")
class Data{
	@Id
	ObjectId id;
	
	private double value;
	//映射到某个Sensor
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