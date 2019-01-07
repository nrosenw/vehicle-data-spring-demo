package io.rosenwald.springDemo.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity representing a vehicle. All vehicle data is from the United States Department of Energy and the 
 * United States Environmental Protection Agency. 
 * 
 * Vehicle data used is available for everyone at https://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip.
 * 
 * TODO: Potentially add emissions data to the entity or create a separate entity for emissions data.
 * 
 * @author Nathaniel Rosenwald
 *
 */
@Entity(name = "VEHICLES")
public class Vehicle {

	@Id private String id;
	
	private int 	year;
	private String 	make;
	private String 	model;
	private String 	drive;
	private String 	transmission;
	private int 	cylinders;
	private float 	displacement;
	private String 	altType;
	private String 	evMotor;

	private static final String STRING_FORMAT = "Vehicle[id=%s, year='%d', make='%s', model='%s, drive='%s', transmission='%s', cylinders='&d', displacement='&d', altType='&s', evMotor='&s']";
	
	/**
	 * No-arg constructor.
	 */
	public Vehicle() {
		this.id = UUID.randomUUID().toString();
	};
	
	/**
	 * Full constructor.
	 * 
	 * @param year The year of the vehicle.
	 * @param make The make of the vehicle.
	 * @param model The model of the vehicle.
	 */
	public Vehicle(int year, String make, String model, String drive, String transmission, int cylinders, float displacement, String altType, String evMotor) {
		this.id = UUID.randomUUID().toString();
		this.year = year;
		this.make = make;
		this.model = model;
		this.drive = drive;
		this.transmission = transmission;
		this.cylinders = cylinders;
		this.displacement = displacement;
		this.altType = altType;
		this.evMotor = evMotor;
	}

	public String getId() {
		return id;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public String getDrive() {
		return drive;
	}

	public void setDrive(String drive) {
		this.drive = drive;
	}
	
	public String getTransmission() {
		return transmission;
	}

	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}
	
	public int getCylinders() {
		return cylinders;
	}

	public void setCylinders(int cylinders) {
		this.cylinders = cylinders;
	}

	public float getDisplacement() {
		return displacement;
	}

	public void setDisplacement(float displacement) {
		this.displacement = displacement;
	}

	public String getEvMotor() {
		return evMotor;
	}

	public void setEvMotor(String evMotor) {
		this.evMotor = evMotor;
	}
	
	public String getAltType() {
		return altType;
	}

	public void setAltType(String altType) {
		this.altType = altType;
	}

	@Override
	public String toString() {
		return String.format(STRING_FORMAT, id, year, make, model, drive, transmission, cylinders, displacement, altType, evMotor);
	}
}
