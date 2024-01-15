package com.nex.digital.service.impl;

import com.nex.digital.domain.*;
import com.nex.digital.dto.DroneDTO;
import com.nex.digital.dto.DroneMedicationDTO;
import com.nex.digital.dto.RegisterDTO;
import com.nex.digital.repository.DroneMedicationRepository;
import com.nex.digital.repository.DroneRepository;
import com.nex.digital.repository.MedicationRepository;
import com.nex.digital.service.DroneService;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DroneServiceImpl implements DroneService {

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private DroneMedicationRepository droneMedicationRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Integer BATTERY_CAPACITY_TO_LOAD = 25;

    @Override
    public DroneDTO droneRegistration(RegisterDTO dto) {
        // validations
        dto.setModel(dto.getModel().toLowerCase());
        validateSerialNumberDuplicate(dto.getSerialNumber());
        validateDroneModel(dto.getModel().toString());
        validateSerialNumberSize(dto.getSerialNumber());

        CompletableFuture<DroneDTO> registrationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Drone drone = modelMapper.map(dto, Drone.class);
                setWeightLimitByModel(drone, dto.getModel());
                drone.setBatteryCapacity(dto.getBatteryCapacity() == null ? 100 : dto.getBatteryCapacity());
                drone.setState(DroneState.IDLE);
                drone.setModel(DroneModel.valueOf(dto.getModel()));
                droneRepository.save(drone);
                return modelMapper.map(drone, DroneDTO.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        try {
            return registrationFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DroneMedicationDTO loadingDrone(List<DroneMedicationDTO> dto, long droneId) {
        Optional<Drone> drone = droneRepository.findById(droneId);
        if (!drone.isPresent()) {
            throw new RuntimeException("Drone not found");
        }
        if (drone.get().getBatteryCapacity() <= BATTERY_CAPACITY_TO_LOAD) {
            throw new RuntimeException("Drone Cannot be Loaded, Battery too low. " +
                    drone.get().getBatteryCapacity() + " Percent only.");
        }

        drone.get().setState(DroneState.LOADING);
        droneRepository.save(drone.get());

        double existingWeight = calculateExistingWeight(drone.get());
        double newWeight = calculateNewWeight(dto);

        if ((newWeight + existingWeight) > drone.get().getWeightLimit()) {
            throw new RuntimeException("Weight to Load is greater than Drone Weight Capacity");
        }

        // Create CompletableFuture for each DroneMedicationDTO
        List<CompletableFuture<Void>> medicationFutures = dto.stream()
                .map(ds -> CompletableFuture.runAsync(() -> processMedication(ds, drone)))
                .collect(Collectors.toList());

        // Wait for all medication processing to complete
        CompletableFuture<Void> allMedicationFutures = CompletableFuture.allOf(
                medicationFutures.toArray(new CompletableFuture[0])
        );

        try {
            allMedicationFutures.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void processMedication(DroneMedicationDTO ds, Optional<Drone> drone) {
        Optional<Medication> medication = medicationRepository.findById(ds.getMedicationId());
        if (medication.isPresent()) {
            DroneMedication droneMedication = droneMedicationRepository.findByMedicationAndDrone(medication.get(), drone.get());
            if (droneMedication != null) {
                droneMedication.setQuantity(droneMedication.getQuantity() + ds.getQuantity());
                droneMedicationRepository.save(droneMedication);
            } else {
                droneMedication = new DroneMedication();
                droneMedication.setDrone(drone.get());
                droneMedication.setMedication(medication.get());
                droneMedication.setQuantity(ds.getQuantity());
                droneMedicationRepository.save(droneMedication);
            }
            drone.get().setState(DroneState.LOADED);
            droneRepository.save(drone.get());
        } else {
            throw new RuntimeException("Medication Not Found");
        }
    }


    @Override
    public List<Map<String, Object>> availableDronesForLoading() {
        List<Drone> drones = droneRepository.findAll();
        double minMedicationWeight = medicationRepository.findAll()
                .stream()
                .mapToDouble(weight -> weight.getWeight())
                .min()
                .orElseThrow(() -> new RuntimeException("No medications found"));

        return drones.stream()
                .flatMap(drone -> {
                    List<DroneMedication> droneMedicationList = droneMedicationRepository.findByDrone(drone);
                    double loadedWeight = droneMedicationList.stream()
                            .mapToDouble(dm -> {
                                Optional<Medication> medication = medicationRepository.findById(dm.getMedication().getId());
                                return medication.map(m -> m.getWeight() * dm.getQuantity()).orElse(0.0);
                            })
                            .sum();
                    double availableWeight = drone.getWeightLimit() - loadedWeight;

                    if (availableWeight > minMedicationWeight && drone.getBatteryCapacity() > BATTERY_CAPACITY_TO_LOAD) {
                        BigDecimal bd = new BigDecimal(availableWeight);
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        Map<String, Object> availableDrone = new HashMap<>();
                        availableDrone.put("Serial", drone.getSerialNumber());
                        availableDrone.put("Battery Capacity", drone.getBatteryCapacity());
                        availableDrone.put("Model", drone.getModel());
                        availableDrone.put("Available Weight To Load", bd);
                        return Stream.of(availableDrone);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Integer retrieveBatteryLevel(long droneId) {
        Optional<Drone> drone = droneRepository.findById(droneId);
        if (!drone.isPresent()) {
            throw new RuntimeException("Drone not found");
        }
        return drone.get().getBatteryCapacity();
    }


    private double calculateExistingWeight(Drone drone) {
        return droneMedicationRepository.findByDrone(drone)
                .stream()
                .mapToDouble(dr -> {
                    Optional<Medication> medication = medicationRepository.findById(dr.getMedication().getId());
                    return medication.map(med -> dr.getQuantity() * med.getWeight()).orElse(0.0);
                })
                .sum();
    }

    private double calculateNewWeight(List<DroneMedicationDTO> dto) {
        return dto.stream()
                .mapToDouble(d -> {
                    Optional<Medication> medication = medicationRepository.findById(d.getMedicationId());
                    return medication.map(med -> d.getQuantity() * med.getWeight()).orElse(0.0);
                })
                .sum();
    }


    // Checks if the incoming request is valid enum or not.
    @Override
    public boolean isDroneModel(String type) {
        // TODO Auto-generated method stub
        return EnumUtils.isValidEnumIgnoreCase(DroneModel.class, type);
    }

    // Checks if the incoming request is valid enum or not.
    @Override
    public boolean isDroneState(String type) {
        // TODO Auto-generated method stub
        return EnumUtils.isValidEnumIgnoreCase(DroneState.class, type);
    }

    private void validateDroneModel(String model) {
        if (!isDroneModel(model)) {
            throw new RuntimeException("Error Drone Model");
        }
    }

    private void validateSerialNumberDuplicate(String serialNumber) {
        // TODO Auto-generated method stub
        Optional<Drone> findDrone = droneRepository.findBySerialNumber(serialNumber);
        if (findDrone.isPresent()) {
            throw new RuntimeException("Serial Number Exists! Can't register Drone with Same serial number");
        }
    }

    private void validateSerialNumberSize(String serialNumber) {
        if (serialNumber.length() > 100) {
            throw new RuntimeException("The Serial Number should have a size less than 100 characters");
        }
    }

    private void setWeightLimitByModel(Drone drone, String model) {
        if (model.equalsIgnoreCase(DroneModel.lightweight.toString())) {
            drone.setWeightLimit(100);
        } else if (model.equalsIgnoreCase(DroneModel.middleweight.toString())) {
            drone.setWeightLimit(250);
        } else if (model.equalsIgnoreCase(DroneModel.cruiserweight.toString())) {
            drone.setWeightLimit(350);
        } else if (model.equalsIgnoreCase(DroneModel.heavyweight.toString())) {
            drone.setWeightLimit(500);
        }
    }
}
