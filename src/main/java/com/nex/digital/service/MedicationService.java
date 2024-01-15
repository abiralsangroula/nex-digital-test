package com.nex.digital.service;

import com.nex.digital.domain.Medication;
import com.nex.digital.dto.MedicationDTO;
import com.nex.digital.dto.RegisterMedicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MedicationService {
	List<MedicationDTO> viewDroneMedicationItems(long droneId);

	RegisterMedicationDTO addMedicationItem(Medication medication, MultipartFile file);

}
