package org.universaldoctor.msuser.mapper;

import model.Doctor;
import model.Profession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import request.keycloak.AddMsUserReq;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "active", constant = "false")
    @Mapping(target = "accepted", constant = "false")
    @Mapping(target = "sex", expression = "java(enums.Sex.valueOf(msUser.getSex()))")
    @Mapping(target = "profession", source = "profession")
    Doctor mapAddMsUserReqToDoctor(AddMsUserReq msUser, Profession profession);
}
