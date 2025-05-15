package org.universaldoctor.msuser.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.PatientRepository;

@Service
@Slf4j
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Boolean save(Patient patient) {
        log.info("Saving patient {}", patient);
        return patientRepository.save(patient) != null;
    }

    public String findKeycloakIdByEmail(String email) {
        log.info("Find keycloakIdByEmail {}", email);
        return patientRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("KeycloakId not found with this email : " + email)).getKeycloakId();
    }
}
