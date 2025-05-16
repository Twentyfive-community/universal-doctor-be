package org.universaldoctor.msuser.mapper;

import dto.KeycloakUser;
import model.Doctor;
import model.MsUser;
import model.Profession;
import org.mapstruct.*;
import request.keycloak.LoginMsUserReq;
import request.keycloak.TokenRequest;
import request.keycloak.UpdateMsUserReq;


@Mapper(componentModel = "spring")
public interface KeycloakMapper {

    @Mapping(target = "enabled", constant = "true")
    KeycloakUser msUserToKeycloakUser(MsUser msUser);

    @Mapping(target = "client_id", constant = "auth-server")
    @Mapping(target = "client_secret", constant = "8qhTxZdW647ouB5K6PmSWexhJHaqlOSC")
    @Mapping(target = "grant_type", constant = "password")
    TokenRequest loginRequestToTokenRequest(LoginMsUserReq loginMsUserReq);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateKeycloakUserFromUpdateMsUserReq(UpdateMsUserReq dto, @MappingTarget KeycloakUser keycloakUser);
}
