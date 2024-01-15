package com.nex.digital.service;

import com.nex.digital.domain.Drone;
import com.nex.digital.domain.DroneMedication;
import com.nex.digital.domain.Medication;
import com.nex.digital.dto.MedicationDTO;
import com.nex.digital.dto.RegisterMedicationDTO;
import com.nex.digital.repository.DroneMedicationRepository;
import com.nex.digital.repository.DroneRepository;
import com.nex.digital.repository.MedicationRepository;
import com.nex.digital.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MedicationServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private DroneMedicationRepository droneMedicationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MedicationServiceImpl medicationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testViewDroneMedicationItems() {
        long droneId = 1L;

        // Mock drone
        Drone drone = new Drone();
        drone.setId(droneId);

        // Mock drone medications
        List<DroneMedication> droneMedications = new ArrayList<>();
        DroneMedication droneMedication = new DroneMedication();
        droneMedication.setMedication(new Medication());
        droneMedication.setQuantity(10);
        droneMedications.add(droneMedication);

        // Mock medication
        Medication medication = new Medication();
        medication.setId(1L);
        medication.setName("Medication 1");

        // Mock medication DTO
        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setName("Medication 1");
        medicationDTO.setQuantity(10);

        // Mock repository calls
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));
        when(droneMedicationRepository.findByDrone(drone)).thenReturn(droneMedications);
        when(medicationRepository.findById(droneMedication.getMedication().getId())).thenReturn(Optional.of(medication));
        when(modelMapper.map(any(), any())).thenReturn(medicationDTO);

        // Invoke the method
        List<MedicationDTO> result = medicationService.viewDroneMedicationItems(droneId);

        // Verify the result
        assertEquals(1, result.size());
        MedicationDTO resultDTO = result.get(0);
        assertEquals(medicationDTO.getName(), resultDTO.getName());
        assertEquals(medicationDTO.getQuantity(), resultDTO.getQuantity());
    }

    @Test
    void testAddMedicationItem_ValidInput_ShouldReturnDTO() throws IOException {
        // Arrange
        Medication medication = new Medication();
        medication.setName("Medication_Name");
        medication.setCode("ABC123");
        medication.setWeight(50.0);

        MultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes()) ;

        Medication savedMedication = new Medication();
        savedMedication.setId(1L);
        savedMedication.setName("Medication_Name112");
        savedMedication.setCode("ABC123");
        savedMedication.setWeight(50.0);
        savedMedication.setImage("test.jpg");

        RegisterMedicationDTO expectedDto = new RegisterMedicationDTO();
        expectedDto.setName("Medication_Name112");
        expectedDto.setCode("ABC123");
        expectedDto.setWeight(50.0);
        expectedDto.setImage("test.jpg");

        when(medicationRepository.save(any(Medication.class))).thenReturn(savedMedication);
        when(modelMapper.map(savedMedication, RegisterMedicationDTO.class)).thenReturn(expectedDto);

        // Act
        RegisterMedicationDTO result = medicationService.addMedicationItem(medication, any());

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getCode(), result.getCode());
        assertEquals(expectedDto.getWeight(), result.getWeight());
        assertEquals(expectedDto.getImage(), result.getImage());

        verify(medicationRepository, times(1)).save(any(Medication.class));
        verify(modelMapper, times(1)).map(savedMedication, RegisterMedicationDTO.class);
    }

    @Test
    void testAddMedicationItem_InvalidName_ThrowsException() throws IOException {
        // Arrange
        Medication medication = new Medication();
        medication.setName("Medication Name!");
        medication.setCode("ABC123");
        medication.setWeight(50.0);

        MultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> medicationService.addMedicationItem(medication, imageFile));
        verify(medicationRepository, never()).save(any(Medication.class));
        verify(modelMapper, never()).map(any(Medication.class), any(Class.class));
    }

    @Test
    void testAddMedicationItem_InvalidCode_ThrowsException() throws IOException {
        // Arrange
        Medication medication = new Medication();
        medication.setName("Medication Name");
        medication.setCode("abc_123");
        medication.setWeight(50.0);

        MultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> medicationService.addMedicationItem(medication, imageFile));
        verify(medicationRepository, never()).save(any(Medication.class));
        verify(modelMapper, never()).map(any(Medication.class), any(Class.class));
    }

    @Test
    void testAddMedicationItem_WeightTooHigh_ThrowsException() throws IOException {
        // Arrange
        Medication medication = new Medication();
        medication.setName("Medication Name");
        medication.setCode("ABC123");
        medication.setWeight(150.0);

        MultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> medicationService.addMedicationItem(medication, imageFile));
        verify(medicationRepository, never()).save(any(Medication.class));
        verify(modelMapper, never()).map(any(Medication.class), any(Class.class));
    }
}

