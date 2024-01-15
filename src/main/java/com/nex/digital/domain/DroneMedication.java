package com.nex.digital.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "drone_medication")
public class DroneMedication {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "drone_id")
	private Drone drone;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medication_id")
	private Medication medication;

    @Column(name = "quantity")
    private int quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Drone getDrone() {
		return drone;
	}

	public void setDrone(Drone drone) {
		this.drone = drone;
	}

	public Medication getMedication() {
		return medication;
	}

	public void setMedication(Medication medication) {
		this.medication = medication;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "DroneMedication{" +
				"id=" + id +
				", drone=" + drone +
				", medication=" + medication +
				", quantity=" + quantity +
				'}';
	}
}