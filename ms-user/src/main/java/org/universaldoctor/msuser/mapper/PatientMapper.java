package org.universaldoctor.msuser.mapper;

import model.MsUser;
import model.Patient;
import org.mapstruct.*;
import request.keycloak.AddMsUserReq;
import request.keycloak.UpdateMsUserReq;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "active", constant = "true")
    Patient mapAddMsUserReqToPatient(AddMsUserReq msUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMsUserFromUpdateMsUserReq(UpdateMsUserReq dto, @MappingTarget Patient entity);
}
