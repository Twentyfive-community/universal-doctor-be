package org.universaldoctor.msuser.mapper;

import model.Doctor;
import model.MsUser;
import model.Profession;
import org.mapstruct.*;
import request.keycloak.AddMsUserReq;
import request.keycloak.UpdateMsUserReq;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "active", constant = "false")
    @Mapping(target = "accepted", constant = "false")
    @Mapping(target = "profession", source = "profession")
    Doctor mapAddMsUserReqToDoctor(AddMsUserReq msUser, Profession profession);

    @Mapping(target = "profession", source = "profession")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDoctorFromUpdateMsUserReq(UpdateMsUserReq dto, @MappingTarget Doctor doctor, Profession profession);
}
