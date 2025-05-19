package org.universaldoctor.msuser.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Profession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.universaldoctor.msuser.mapper.ProfessionMapper;
import org.universaldoctor.msuser.repository.ProfessionRepository;
import request.profession.AddProfessionReq;
import response.profession.GetAllProfessionRes;
import response.profession.GetByNameProfessionRes;
import request.profession.UpdateProfessionReq;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProfessionService {

    private final ProfessionRepository professionRepository;
    private final ProfessionMapper professionMapper;

    public ProfessionService(ProfessionRepository professionRepository, ProfessionMapper professionMapper) {
        this.professionRepository = professionRepository;
        this.professionMapper = professionMapper;
    }

    @Transactional
    public void save(AddProfessionReq addProfessionReq) {
        log.info("Saving profession with name {}", addProfessionReq);
        Profession profession = professionMapper.mapAddProfessionReqToProfession(addProfessionReq);
        professionRepository.save(profession);
    }

    public Profession findByName(String name) {
        return professionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("No Entity found with this name +" +name));
    }

    @Transactional
    public void update(UpdateProfessionReq updateProfessionReq) {
        Profession profession = findByName(updateProfessionReq.getOldName());

        profession.setName(updateProfessionReq.getNewName());
        professionRepository.save(profession);
    }

    public Profession toggleStatus(String professionName) {
        Profession profession = findByName(professionName);
        profession.setActive(!profession.getActive());
        return professionRepository.save(profession);
    }

    public GetByNameProfessionRes getByName(String name) {
        Profession profession = findByName(name);
        return professionMapper.mapProfessionToGetByNameProfessionReq(profession);
    }

    public GetAllProfessionRes getAll(boolean active) {
        List<String> professionNames = new ArrayList<>();

        List<Profession> professions = professionRepository.findAllByActive(active);

        for (Profession profession : professions) {
            professionNames.add(profession.getName());
        }

        return new GetAllProfessionRes(professionNames);
    }
}
