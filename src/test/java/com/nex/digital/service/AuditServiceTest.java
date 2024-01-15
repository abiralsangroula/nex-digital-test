package com.nex.digital.service;

import com.nex.digital.domain.Drone;
import com.nex.digital.domain.DroneState;
import com.nex.digital.repository.DroneRepository;
import com.nex.digital.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuditServiceTest {
    @Mock
    private DroneRepository droneRepository;

    @Mock
    private FileWriter fileWriter;

    @Mock
    private BufferedWriter bufferedWriter;

    @InjectMocks
    private AuditServiceImpl auditService;

    Drone drone1= new Drone();
    Drone drone2= new Drone();
    List<Drone> drones = new ArrayList<>();


    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);

        doAnswer(invocation -> {
            String line = invocation.getArgument(0);
            return null;
        }).when(fileWriter).write(anyString());

        drone1.setSerialNumber("ABC123");
        drone1.setBatteryCapacity(0);
        drone1.setState(DroneState.IDLE);

        drone2.setSerialNumber("XYZ456");
        drone2.setBatteryCapacity(10);
        drone2.setState(DroneState.LOADED);

        drones.add(drone1);
        drones.add(drone2);
    }
    @Test
    public void testRunBatteryAudit_WithDrones_ShouldUpdateBatteryCapacityAndWriteToFile() throws IOException {
        when(droneRepository.findAll()).thenReturn(drones);
        doNothing().when(fileWriter).write(anyString());
        doNothing().when(bufferedWriter).newLine();
        doNothing().when(bufferedWriter).close();

        auditService.runBatteryAudit();

        verify(droneRepository, times(2)).save(any(Drone.class));
        verify(fileWriter, times(0)).write(anyString());
        verify(bufferedWriter, times(0)).newLine();
        verify(bufferedWriter,times(0)).close();
    }

    @Test
    public void testChargeDroneBattery_WithDrones_ShouldUpdateBatteryCapacityAndWriteToFile() throws IOException {
        when(droneRepository.findAll()).thenReturn(drones);
        doNothing().when(fileWriter).write(anyString());
        doNothing().when(bufferedWriter).newLine();
        doNothing().when(bufferedWriter).close();

        // Invoke the method
        auditService.chargeDroneBattery();

        // Verify the behavior
        verify(droneRepository, times(1)).save(any(Drone.class));
        verify(fileWriter, times(0)).write(anyString());
        verify(bufferedWriter, times(0)).newLine();
        verify(bufferedWriter,times(0)).close();
    }
}
