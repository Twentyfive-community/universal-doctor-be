package org.universaldoctor.msuser.service;

import lombok.extern.slf4j.Slf4j;
import model.Doctor;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.DoctorRepository;

@Service
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void save(Doctor doctor) {
        log.info("Saving doctor {}", doctor);
        doctorRepository.save(doctor);
    }
}
