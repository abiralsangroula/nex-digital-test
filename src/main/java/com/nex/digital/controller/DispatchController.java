package com.nex.digital.controller;

import com.nex.digital.domain.Medication;
import com.nex.digital.dto.DroneDTO;
import com.nex.digital.dto.DroneMedicationDTO;
import com.nex.digital.dto.RegisterDTO;
import com.nex.digital.dto.RegisterMedicationDTO;
import com.nex.digital.service.DroneService;
import com.nex.digital.service.MedicationService;
import com.nex.digital.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class DispatchController {
	
	@Autowired
	private DroneService droneService;

    @Autowired
    private MedicationService medicationService;

	
	@PostMapping(value= "/drones")
	public Map<String, Object> droneRegistration(@RequestBody RegisterDTO dto) {
		try {           
            DroneDTO registeredDrone = droneService.droneRegistration(dto);
            return ResponseBuilder.buildObjectResponse(registeredDrone, ResponseBuilder.ResponseCode.OK_STATUS_CODE, ResponseBuilder.ResponseType.SUCCESS, "Successfully Registered Drone.");
        } catch (RuntimeException e) {
            return ResponseBuilder.buildObjectResponse(null, ResponseBuilder.ResponseCode.INVALID_INPUT_STATUS_CODE, ResponseBuilder.ResponseType.ERROR, e.getMessage());
        }
    }
	
	@PostMapping(value= "/drones/loading/{droneId}")
	public Map<String, Object> droneLoading(@RequestBody List<DroneMedicationDTO> dto, @PathVariable Long droneId) {
		try {           
           
            return ResponseBuilder.buildObjectResponse(droneService.loadingDrone(dto, droneId), ResponseBuilder.ResponseCode.OK_STATUS_CODE, ResponseBuilder.ResponseType.SUCCESS, "Successfully Loaded Drone.");
        } catch (RuntimeException e) {
            return ResponseBuilder.buildObjectResponse(null, ResponseBuilder.ResponseCode.INVALID_INPUT_STATUS_CODE, ResponseBuilder.ResponseType.ERROR, e.getMessage());
        }
    }

    @GetMapping(value= "/drones/loading")
    public Map<String, Object> availableDronesForLoading() {
        try {

            return ResponseBuilder.buildObjectResponse(droneService.availableDronesForLoading(), ResponseBuilder.ResponseCode.OK_STATUS_CODE, ResponseBuilder.ResponseType.SUCCESS, "All available drones for loading");
        } catch (RuntimeException e) {
            return ResponseBuilder.buildObjectResponse(null, ResponseBuilder.ResponseCode.INVALID_INPUT_STATUS_CODE, ResponseBuilder.ResponseType.ERROR, e.getMessage());
        }
    }

    @PostMapping(value= "/drones/{droneId}")
    public Map<String, Object> viewDroneMedicationItems(@PathVariable Long droneId) {
        try {

            return ResponseBuilder.buildListResponse(medicationService.viewDroneMedicationItems(droneId), ResponseBuilder.ResponseCode.OK_STATUS_CODE, ResponseBuilder.ResponseType.SUCCESS, "Medication Items");
        } catch (RuntimeException e) {
            return ResponseBuilder.buildObjectResponse(null, ResponseBuilder.ResponseCode.INVALID_INPUT_STATUS_CODE, ResponseBuilder.ResponseType.ERROR, e.getMessage());
        }
    }

    @GetMapping(value= "/drones/{droneId}/battery")
    public Map<String, Object> viewBatteryPercentagge(@PathVariable Long droneId) {
        try {

            Integer battery= droneService.retrieveBatteryLevel(droneId);
            return ResponseBuilder.buildListResponse(battery, ResponseBuilder.ResponseCode.OK_STATUS_CODE, ResponseBuilder.ResponseType.SUCCESS, "Current Battery Level: "+battery);
        } catch (RuntimeException e) {
            return ResponseBuilder.buildObjectResponse(null, ResponseBuilder.ResponseCode.INVALID_INPUT_STATUS_CODE, ResponseBuilder.ResponseType.ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/medication", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> addMedicationItem(@RequestBody Medication medication, @RequestParam MultipartFile imageFile) {
        try {
            RegisterMedicationDTO dto= medicationService.addMedicationItem(medication,imageFile);
            return ResponseBuilder.buildListResponse(dto, ResponseBuilder.ResponseCode.OK_STATUS_CODE, ResponseBuilder.ResponseType.SUCCESS, "New Medication Loaded ");
        } catch (RuntimeException e) {
            return ResponseBuilder.buildObjectResponse(null, ResponseBuilder.ResponseCode.INVALID_INPUT_STATUS_CODE, ResponseBuilder.ResponseType.ERROR, e.getMessage());
        }
    }
}
