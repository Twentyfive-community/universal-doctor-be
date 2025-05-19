package org.universaldoctor.msuser.mapper;

import model.Profession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import request.profession.AddProfessionReq;
import response.profession.GetAllProfessionRes;
import response.profession.GetByNameProfessionRes;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfessionMapper {

    @Mapping(target = "active", constant = "true")
    Profession mapAddProfessionReqToProfession(AddProfessionReq addProfessionReq);

    GetByNameProfessionRes mapProfessionToGetByNameProfessionReq(Profession profession);
}
