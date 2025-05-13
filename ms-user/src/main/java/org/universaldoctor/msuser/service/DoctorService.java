package org.universaldoctor.msuser.service;

import model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.DoctorRepository;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public Boolean save(Doctor doctor) {
        return doctorRepository.save(doctor) != null;
    }
}
