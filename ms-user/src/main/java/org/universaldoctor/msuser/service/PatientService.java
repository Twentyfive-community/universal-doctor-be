package org.universaldoctor.msuser.service;

import model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Boolean save(Patient patient) {
        return patientRepository.save(patient) != null;
    }
}
