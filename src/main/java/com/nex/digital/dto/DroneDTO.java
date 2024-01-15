package com.nex.digital.dto;

import com.nex.digital.domain.DroneModel;
import com.nex.digital.domain.DroneState;

import java.math.BigDecimal;

public class DroneDTO {
    private String serialNumber;

    private DroneModel model;

    private BigDecimal weightLimit;

    private int batteryCapacity;

    private DroneState state;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public DroneModel getModel() {
		return model;
	}

	public void setModel(DroneModel model) {
		this.model = model;
	}

	public BigDecimal getWeightLimit() {
		return weightLimit;
	}

	public void setWeightLimit(BigDecimal weightLimit) {
		this.weightLimit = weightLimit;
	}

	public int getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(int batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	public DroneState getState() {
		return state;
	}

	public void setState(DroneState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "DroneDTO [serialNumber=" + serialNumber + ", model=" + model + ", weightLimit=" + weightLimit
				+ ", batteryCapacity=" + batteryCapacity + ", state=" + state + "]";
	}
    
    
}
