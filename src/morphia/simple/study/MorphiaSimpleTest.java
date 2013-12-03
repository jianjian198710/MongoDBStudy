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
 * morphia��Java MongoDB��ORM���
 * ѧϰʹ�õ�DB��morphia_test
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
		//����  ����DB������ClassName:����.���� 
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
		
		//Query����extends QueryResults<T>
		Query<Sensor> sensors1 = ds.find(Sensor.class);
		//fetch����һ��Iterator
		for(Sensor sensor:sensors1.fetch()){
			System.out.println(sensor);
		}
		
		//ֻ��ȡSensor��sensorId���� ���Ƿ��صĻ��Ƕ��� ֻ�������������ֻ��sensorId���Ա���ֵ��
		System.out.println("ʹ��retrievedFields����������ѯ");
		Query<Sensor> sensors2 = ds.createQuery(Sensor.class).retrievedFields(true, "sensorId");
		for(Sensor sensor:sensors2.fetch()){
			System.out.println(sensor);
		}
		
		//ʹ��filter����������ѯ,>֮ǰҪ�пո�,���Խ������filter��ʾAND����
		System.out.println("ʹ��filter����������ѯ");
		Query<Data> datas1 = ds.createQuery(Data.class).filter("value >", 1);
		for(Data data:datas1.fetch()){
			System.out.println(data);
		}
		
		//ʹ��field��������������ѯ
		System.out.println("ʹ��field��������������ѯ");
		Query<Data> data2 = ds.createQuery(Data.class).field("value").equal(100.0);
		for(Data data:data2.fetch()){
			System.out.println(data);
		}
	}
	
	@Test
	public void findEmbedded(){
		System.out.println("������Ƕ�����ѯ");
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

//ָ����ӳ���Collection
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