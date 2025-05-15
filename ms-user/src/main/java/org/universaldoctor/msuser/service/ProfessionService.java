package org.universaldoctor.msuser.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Profession;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.ProfessionRepository;

@Service
@Slf4j
public class ProfessionService {

    private final ProfessionRepository professionRepository;

    public ProfessionService(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    public Profession save(Profession profession) {
        log.info("Saving profession {}", profession);
        return professionRepository.save(profession);
    }

    public Profession findByName(String name) {
        return professionRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("No Entity found with this name +" +name));
    }
}
