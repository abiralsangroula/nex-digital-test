package com.nex.digital.service;

import com.nex.digital.dto.DroneDTO;
import com.nex.digital.dto.DroneMedicationDTO;
import com.nex.digital.dto.RegisterDTO;

import java.util.List;
import java.util.Map;

public interface DroneService {
	
	 boolean isDroneModel(String type);
	 
	 boolean isDroneState(String type);
	 
	 DroneDTO droneRegistration(RegisterDTO registerDTO);
	 
	 DroneMedicationDTO loadingDrone(List<DroneMedicationDTO> dto, long droneId);

	List<Map<String,Object>> availableDronesForLoading();

	Integer retrieveBatteryLevel(long droneId);

}
