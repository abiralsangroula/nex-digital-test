package com.nex.digital.service.impl;

import com.nex.digital.domain.Drone;
import com.nex.digital.domain.DroneState;
import com.nex.digital.repository.DroneRepository;
import com.nex.digital.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class AuditServiceImpl implements AuditService {
    private static final String FILE_PATH = "src/main/resources/battery-state.txt";

    @Autowired
    private DroneRepository droneRepository;


    /*This is to generate a audit file to check battery percentage,
    the audit will check the battery every 1 minute. The battery level will also decrease by 1 percentage
    every 1 minute. We need some idea to reduce the battery as well*/
    @Scheduled(cron = "0 */1 * * * *") // Runs every 1 minutes
    @Override
    public void runBatteryAudit() {
        try {
            StringBuilder contentToAppend = new StringBuilder();
            List<Drone> drones = droneRepository.findAll();
            FileWriter fileWriter = new FileWriter(FILE_PATH, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Drone drone : drones) {
                if(drone.getBatteryCapacity()>0){
                    if (drone.getState().toString().equalsIgnoreCase(DroneState.IDLE.toString())) {
                        drone.setBatteryCapacity(drone.getBatteryCapacity() - 1);
                    } else {
                        drone.setBatteryCapacity(drone.getBatteryCapacity() - 2);
                    }
                }
                contentToAppend.append("Drone with Serial Number " + drone.getSerialNumber()
                        + " currently has the Battery Capacity of: "
                        + drone.getBatteryCapacity() + " Percentage \n");
                bufferedWriter.write(contentToAppend.toString());
                bufferedWriter.newLine();
                droneRepository.save(drone);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Charge the drone if the battery capacity/level is zero, every 5 minute
    @Scheduled(cron = "0 */5 * * * *") // Runs every 1 minutes
    @Override
    public void chargeDroneBattery() {
        try {
            StringBuilder contentToAppend = new StringBuilder();
            List<Drone> drones = droneRepository.findAll().stream().filter(
                    c -> c.getBatteryCapacity() == 0).collect(Collectors.toList());
            FileWriter fileWriter = new FileWriter(FILE_PATH, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Drone drone : drones) {
                if (drone.getState().toString().equalsIgnoreCase(DroneState.IDLE.toString()) && drone.getBatteryCapacity() > 0) {
                    drone.setBatteryCapacity(drone.getBatteryCapacity() - 1);
                } else {
                    drone.setBatteryCapacity(drone.getBatteryCapacity() - 2);
                }
                contentToAppend.append("Drone with Serial Number " + drone.getSerialNumber()
                        + " gets fully recharged : "
                        + drone.getBatteryCapacity() + " Percentage \n");
                bufferedWriter.write(contentToAppend.toString());
                bufferedWriter.newLine();
                droneRepository.save(drone);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
