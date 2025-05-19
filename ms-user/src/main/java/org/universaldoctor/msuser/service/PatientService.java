package org.universaldoctor.msuser.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Patient;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.PatientRepository;

@Service
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void save(Patient patient) {
        log.info("Saving patient {}", patient);
        patientRepository.save(patient);
    }

    public String findKeycloakIdByEmail(String email) {
        log.info("Find keycloakIdByEmail {}", email);
        return patientRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("KeycloakId not found with this email : " + email)).getKeycloakId();
    }

    public Patient findByEmail(String email) {
        return patientRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Patient not found with this email : " + email));
    }
}
