package com.nex.digital.repository;

import com.nex.digital.domain.Drone;
import com.nex.digital.domain.DroneMedication;
import com.nex.digital.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DroneMedicationRepository extends JpaRepository<DroneMedication, Long> {
	
    List<DroneMedication> findByDrone(Drone drone);

    DroneMedication findByMedicationAndDrone(Medication medication, Drone drone);

}
