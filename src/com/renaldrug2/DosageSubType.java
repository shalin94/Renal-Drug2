package com.renaldrug2;

public class DosageSubType {
	int dosageType;
	String Description;
	public DosageSubType(int dosageType,String Description){
		super();
		this.dosageType = dosageType;
		this.Description=Description;
	}
	public int getDosageType() {
		return dosageType;
	}
	public void setDosageType(int dosageType) {
		this.dosageType = dosageType;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}

}
