package com.zjut.bluetoothle;

import java.io.Serializable;

public class ExerciseData implements Serializable{
	private String data_time;
	private String device_id;
	private String data_energry;
	private String counts1;
	private String counts2;
	private String counts3;
	private String counts4;
	private Boolean isEmpty;
	
	public String getData_time() {
		return data_time;
	}
	public void setData_time(String data_time) {
		this.data_time = data_time;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getData_energry() {
		return data_energry;
	}
	public void setData_energry(String data_energry) {
		this.data_energry = data_energry;
	}
	public String getCounts1() {
		return counts1;
	}
	public void setCounts1(String counts1) {
		this.counts1 = counts1;
	}
	public String getCounts2() {
		return counts2;
	}
	public void setCounts2(String counts2) {
		this.counts2 = counts2;
	}
	public String getCounts3() {
		return counts3;
	}
	public void setCounts3(String counts3) {
		this.counts3 = counts3;
	}
	public String getCounts4() {
		return counts4;
	}
	public void setCounts4(String counts4) {
		this.counts4 = counts4;
	}
	public Boolean getIsEmpty() {
		return isEmpty;
	}
	public void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	public ExerciseData(String data_time, String device_id,
			String data_energry, String counts1, String counts2,
			String counts3, String counts4) {
		super();
		this.data_time = data_time;
		this.device_id = device_id;
		this.data_energry = data_energry;
		this.counts1 = counts1;
		this.counts2 = counts2;
		this.counts3 = counts3;
		this.counts4 = counts4;
	}
	@Override
	public String toString() {
		return "[" + data_time + ","
				+ device_id + "," + data_energry + ","
				+ counts1 + "," + counts2 + "," + counts3
				+ "," + counts4 + "]";
	}
	
	
	
}
