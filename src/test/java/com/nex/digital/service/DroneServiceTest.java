package com.nex.digital.service;

import com.nex.digital.domain.*;
import com.nex.digital.dto.DroneDTO;
import com.nex.digital.dto.RegisterDTO;
import com.nex.digital.repository.DroneMedicationRepository;
import com.nex.digital.repository.DroneRepository;
import com.nex.digital.repository.MedicationRepository;
import com.nex.digital.service.impl.DroneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private DroneMedicationRepository droneMedicationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DroneServiceImpl droneService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDroneRegistration() {
        RegisterDTO dto = new RegisterDTO();
        dto.setSerialNumber("123456");
        dto.setModel("lightweight");
        dto.setBatteryCapacity(80);

        Drone drone = new Drone();
        drone.setId(1L);
        drone.setSerialNumber(dto.getSerialNumber());
        drone.setModel(DroneModel.lightweight);
        drone.setWeightLimit(100);
        drone.setBatteryCapacity(dto.getBatteryCapacity());
        drone.setState(DroneState.IDLE);

        DroneDTO expectedDroneDTO = new DroneDTO();
        expectedDroneDTO.setSerialNumber(drone.getSerialNumber());
        expectedDroneDTO.setModel(drone.getModel());
        expectedDroneDTO.setWeightLimit(new BigDecimal(drone.getWeightLimit()));
        expectedDroneDTO.setBatteryCapacity(drone.getBatteryCapacity());
        expectedDroneDTO.setState(drone.getState());

        when(modelMapper.map(dto, Drone.class)).thenReturn(drone);
        when(droneRepository.findBySerialNumber(dto.getSerialNumber())).thenReturn(Optional.empty());
        when(droneRepository.save(drone)).thenReturn(drone);
        when(modelMapper.map(drone, DroneDTO.class)).thenReturn(expectedDroneDTO);

        DroneDTO result = droneService.droneRegistration(dto);

        assertNotNull(result);
        assertEquals(expectedDroneDTO.getSerialNumber(), result.getSerialNumber());
        assertEquals(expectedDroneDTO.getModel(), result.getModel());
        assertEquals(expectedDroneDTO.getWeightLimit(), result.getWeightLimit());
        assertEquals(expectedDroneDTO.getBatteryCapacity(), result.getBatteryCapacity());
        assertEquals(expectedDroneDTO.getState(), result.getState());
    }

    @Test
    public void testAvailableDronesForLoading() {
        Drone drone1 = new Drone();
        drone1.setSerialNumber("123");
        drone1.setBatteryCapacity(80);
        drone1.setModel(DroneModel.lightweight);
        drone1.setWeightLimit(100);
        drone1.setState(DroneState.IDLE);

        Drone drone2 = new Drone();
        drone2.setSerialNumber("456");
        drone2.setBatteryCapacity(90);
        drone2.setModel(DroneModel.middleweight);
        drone2.setWeightLimit(200);
        drone2.setState(DroneState.LOADING);

        Medication medication = new Medication();
        medication.setId(1L);
        medication.setWeight(1.5);

        DroneMedication droneMedication1 = new DroneMedication();
        droneMedication1.setDrone(drone1);
        droneMedication1.setMedication(medication);
        droneMedication1.setQuantity(5);

        DroneMedication droneMedication2 = new DroneMedication();
        droneMedication2.setDrone(drone2);
        droneMedication2.setMedication(medication);
        droneMedication2.setQuantity(2);

        when(droneRepository.findAll()).thenReturn(Arrays.asList(drone1, drone2));
        when(medicationRepository.findAll()).thenReturn(Arrays.asList(medication));
        when(droneMedicationRepository.findByDrone(drone1)).thenReturn(Arrays.asList(droneMedication1));
        when(droneMedicationRepository.findByDrone(drone2)).thenReturn(Arrays.asList(droneMedication2));

        List<Map<String, Object>> result = droneService.availableDronesForLoading();

        assertEquals(2, result.size());
        Map<String, Object> availableDrone = result.get(0);
        assertEquals(drone1.getSerialNumber(), availableDrone.get("Serial"));
        assertEquals(drone1.getBatteryCapacity(), availableDrone.get("Battery Capacity"));
        assertEquals(drone1.getModel(), availableDrone.get("Model"));

        double expectedAvailableWeight = drone1.getWeightLimit()
                - (medication.getWeight() * droneMedication1.getQuantity());
        BigDecimal bd = new BigDecimal(expectedAvailableWeight);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        assertNotNull(availableDrone.get("Available Weight To Load"));
    }

    @Test
    public void testRetrieveBatteryLevel() {
        long droneId = 1L;

        Drone drone = new Drone();
        drone.setId(droneId);
        drone.setBatteryCapacity(80);

        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));

        Integer result = droneService.retrieveBatteryLevel(droneId);

        assertEquals(drone.getBatteryCapacity(), result);
    }
}
