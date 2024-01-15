package com.nex.digital.repository;

import com.nex.digital.domain.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone, Long>{
	
	Optional<Drone> findBySerialNumber(String serial);

}
