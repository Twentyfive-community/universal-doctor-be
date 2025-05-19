package org.universaldoctor.msuser.mapper;

import dto.KeycloakUser;
import model.MsUser;
import org.mapstruct.*;
import request.keycloak.*;


@Mapper(componentModel = "spring")
public interface KeycloakMapper {

    @Mapping(target = "enabled", constant = "true")
    KeycloakUser msUserToKeycloakUser(MsUser msUser);

    @Mapping(target = "client_id", constant = "auth-server")
    @Mapping(target = "client_secret", constant = "8qhTxZdW647ouB5K6PmSWexhJHaqlOSC")
    @Mapping(target = "grant_type", constant = "password")
    TokenRequest loginRequestToTokenRequest(LoginMsUserReq loginMsUserReq);

    @Mapping(target = "client_id", constant = "auth-server")
    @Mapping(target = "client_secret", constant = "8qhTxZdW647ouB5K6PmSWexhJHaqlOSC")
    @Mapping(target = "grant_type", constant = "refresh_token")
    RefreshLoginReq refreshTokenRequestToRefreshLoginRequest(RefreshTokenReq refreshTokenReq);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateKeycloakUserFromUpdateMsUserReq(UpdateMsUserReq dto, @MappingTarget KeycloakUser keycloakUser);

}
