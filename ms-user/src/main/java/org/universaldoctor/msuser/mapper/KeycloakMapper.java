package org.universaldoctor.msuser.mapper;

import dto.KeycloakUser;
import model.Doctor;
import model.MsUser;
import model.Profession;
import org.mapstruct.*;
import request.keycloak.UpdateMsUserReq;


@Mapper(componentModel = "spring")
public interface KeycloakMapper {

    @Mapping(target = "enabled", constant = "true")
    KeycloakUser msUserToKeycloakUser(MsUser msUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateKeycloakUserFromUpdateMsUserReq(UpdateMsUserReq dto, @MappingTarget KeycloakUser keycloakUser);
}
