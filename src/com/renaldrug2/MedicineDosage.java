package com.renaldrug2;

public class MedicineDosage {
	Medicine medicine;
	DosageType[] dosageTypes;
	DosageSubType[] dosageSubTypes;
	String dosageValue;
	
	public MedicineDosage(Medicine medicine, DosageType[] dosageTypes,
			DosageSubType[] dosageSubTypes, String dosageValue) {
		super();
		this.medicine = medicine;
		this.dosageTypes = dosageTypes;
		this.dosageSubTypes = dosageSubTypes;
		this.dosageValue = dosageValue;
	}
	public Medicine getMedicine() {
		return medicine;
	}
	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}
	public DosageType[] getDosageTypes() {
		return dosageTypes;
	}
	public void setDosageTypes(DosageType[] dosageTypes) {
		this.dosageTypes = dosageTypes;
	}
	public DosageSubType[] getDosageSubTypes() {
		return dosageSubTypes;
	}
	public void setDosageSubTypes(DosageSubType[] dosageSubTypes) {
		this.dosageSubTypes = dosageSubTypes;
	}
	public String getDosageValue() {
		return dosageValue;
	}
	public void setDosageValue(String dosageValue) {
		this.dosageValue = dosageValue;
	}
	
	
	
}
