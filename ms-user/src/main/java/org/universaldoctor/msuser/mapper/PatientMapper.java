package org.universaldoctor.msuser.mapper;

import model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import request.keycloak.AddMsUserReq;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "sex", expression = "java(enums.Sex.valueOf(msUser.getSex()))")
    Patient mapAddMsUserReqToPatient(AddMsUserReq msUser);
}
