package gson.simple.userguie;

import org.junit.Test;

import com.google.gson.Gson;

public class GsonTest{

	@Test
	public void test(){
		//简单类型数据转换成JSON
		Gson gson = new Gson();
		System.out.println(gson.toJson(1));
		System.out.println(gson.toJson("abc"));
		System.out.println(gson.toJson(new Long(10)));
		System.out.println(gson.toJson(new int[]{1,2,3,4}));
	}
	
	@Test
	public void test2(){
		//JSON转换成简单类型
		Gson gson = new Gson();
		System.out.println(gson.fromJson("1", Integer.class));
		System.out.println(gson.fromJson("abc", String.class));
		System.out.println(gson.fromJson("\"abc\"", String.class));
	}

	@Test
	public void test3(){
		//JSON与Java对象相互转换
		Sensor sensor =  new Sensor();
		sensor.setSensorId("sensor1");
		sensor.setObserveProperty("Temperature");
		
		Data data = new Data();
		data.setSensor(sensor);
		data.setValue(10);
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(sensor));
		System.out.println(gson.toJson(data));
		
		System.out.println(gson.fromJson("{'sensorId':'sensor1','observeProperty':'Temperature'}", Sensor.class));
	}
}

class Data{
	private double value;
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
	
}

class Sensor{
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
