package org.universaldoctor.msuser.service;

import model.Profession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.repository.ProfessionRepository;

@Service
public class ProfessionService {
    @Autowired
    private ProfessionRepository professionRepository;

    public Profession save(Profession profession) {
        return professionRepository.save(profession);
    }
}
