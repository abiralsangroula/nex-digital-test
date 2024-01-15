package com.nex.digital.service.impl;

import com.nex.digital.domain.Drone;
import com.nex.digital.domain.DroneMedication;
import com.nex.digital.domain.Medication;
import com.nex.digital.dto.MedicationDTO;
import com.nex.digital.dto.RegisterMedicationDTO;
import com.nex.digital.repository.DroneMedicationRepository;
import com.nex.digital.repository.DroneRepository;
import com.nex.digital.repository.MedicationRepository;
import com.nex.digital.service.MedicationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MedicationServiceImpl implements MedicationService {

	@Autowired
	private DroneRepository droneRepository;
	
	@Autowired
	private MedicationRepository medicationRepository;

	@Autowired
	private DroneMedicationRepository droneMedicationRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<MedicationDTO> viewDroneMedicationItems(long droneId) {
		Drone drone = droneRepository.findById(droneId)
				.orElseThrow(() -> new RuntimeException("Drone not found"));

		List<DroneMedication> droneMedications = droneMedicationRepository.findByDrone(drone);

		return droneMedications.stream()
				.map(droneMedication -> medicationRepository.findById(droneMedication.getMedication().getId())
						.map(medication -> {
							MedicationDTO medicationDTO = modelMapper.map(medication, MedicationDTO.class);
							medicationDTO.setQuantity(droneMedication.getQuantity());
							return medicationDTO;
						})
						.orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public RegisterMedicationDTO addMedicationItem(Medication medication, MultipartFile imageFile) {
		try {
			if(isValidated(medication)){
				String imageName = UUID.randomUUID()+ "-" + imageFile.getOriginalFilename();

				File destination = new ClassPathResource("src/main/resources" + imageName).getFile();
				FileCopyUtils.copy(imageFile.getBytes(), destination);

				medication.setImage(imageName);
				return modelMapper.map(medicationRepository.save(medication),RegisterMedicationDTO.class);
			}else{
				throw new RuntimeException("Issue with Input Data");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isValidated(Medication medication){
		if(medication.getName() != null){
			String pattern = "^[a-zA-Z0-9\\-_]+$";
			Pattern regex = Pattern.compile(pattern);
			Matcher matcher = regex.matcher(medication.getName());
			if (!matcher.matches()) {
				throw new RuntimeException("Name is Invalid!");
			}
		}

		if(medication.getCode() != null){
			String pattern="^[A-Z0-9_]+$";
			Pattern regex = Pattern.compile(pattern);
			Matcher matcher = regex.matcher(medication.getCode());
			if (!matcher.matches()) {
				throw new RuntimeException("Code is Invalid!");
			}
		}
		if(medication.getWeight()==null){
			medication.setWeight(00.0);
		}
		if(medication.getWeight()>100.0){
			throw new RuntimeException("Weight too high");
		}
		return true;
	}
}
