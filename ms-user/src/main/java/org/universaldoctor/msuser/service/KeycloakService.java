package org.universaldoctor.msuser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.KeycloakUser;
import exception.TokenRetrievalException;
import lombok.extern.slf4j.Slf4j;
import model.Doctor;
import model.MsUser;
import model.Patient;
import model.Profession;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.client.KeycloakClient;
import org.universaldoctor.msuser.mapper.DoctorMapper;
import org.universaldoctor.msuser.mapper.KeycloakMapper;
import org.universaldoctor.msuser.mapper.PatientMapper;
import request.keycloak.AddMsUserReq;
import request.keycloak.TokenRequest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakService {

    private final KeycloakClient keycloakClient;
    private final PatientService patientService;
    private final ProfessionService professionService;
    private final DoctorService doctorService;
    private final KeycloakMapper keycloakMapper;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;

    @Value("${keycloak.clientId}")
    protected String clientId;
    @Value("${keycloak.credentials.secret}")
    protected String clientSecret;
    @Value("${keycloak.username}")
    protected String username;
    @Value("${keycloak.password}")
    protected String password;
    @Value("${keycloak.password}")
    protected String grantType;

    public KeycloakService(KeycloakClient keycloakClient, PatientService patientService, ProfessionService professionService, DoctorService doctorService, KeycloakMapper keycloakMapper, PatientMapper patientMapper, DoctorMapper doctorMapper) {
        this.keycloakClient = keycloakClient;
        this.patientService = patientService;
        this.professionService = professionService;
        this.doctorService = doctorService;
        this.keycloakMapper = keycloakMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
    }

    public String getAdminBearerToken() {
        TokenRequest tokenRequest = new TokenRequest(clientId, clientSecret, grantType, username, password);
        return "Bearer " + getToken(tokenRequest);
    }

    public String getToken(TokenRequest tokenRequest) {
        log.info("request: {}", tokenRequest);
        try {
            Object response = keycloakClient.getToken(tokenRequest);
            ObjectMapper objectMapper = new ObjectMapper();
            Map responseMap = objectMapper.convertValue(response, Map.class);
            return (String) responseMap.get("access_token");
        } catch (Exception e) {
            log.error("error retrieving access token", e);
            throw new TokenRetrievalException(e.getMessage());
        }

    }

    public void resetPasswordFromEmail(String email){
        log.info("sending reset password request to email: {}", email);
        String keycloakId = patientService.findKeycloakIdByEmail(email);
        List<String> actions = Collections.singletonList("UPDATE_PASSWORD");
        keycloakClient.resetPassword(getAdminBearerToken(),keycloakId,actions);
    }


    public void addMsUser(AddMsUserReq msUser) {
        log.info("request: {}", msUser);

        String role = inferAndValidateRole(msUser);

        switch (role) {
            case "patient" -> {
                log.info("Adding patient: {}", msUser);
                Patient patient = patientMapper.mapAddMsUserReqToPatient(msUser);
                addMsUserToRealm(patient, role);
                patientService.save(patient);
            }
            case "doctor" -> {
                log.info("Adding doctor: {}", msUser);
                Profession profession = professionService.findByName(msUser.getProfessionName());
                Doctor doctor = doctorMapper.mapAddMsUserReqToDoctor(msUser, profession);
                addMsUserToRealm(doctor, role);
                doctorService.save(doctor);
            }
            default -> throw new IllegalStateException("Unexpected role: " + role);
        }
    }


    public void addMsUserToRealm(MsUser msUser, String role) {
        log.info("adding user to realm: {}", msUser);
        String bearerToken = getAdminBearerToken();
        KeycloakUser keycloakUser = keycloakMapper.createOrUpdateMsUserToRealm(msUser);
        ResponseEntity<Object> response = keycloakClient.add(bearerToken, keycloakUser);
        String keycloakId = keycloakMapper.getKeycloakIdFromResponse(response);
        addRoleToUser(bearerToken, keycloakId,role);
        sendPasswordResetEmail(keycloakId);
        msUser.setKeycloakId(keycloakId);
    }

    private void addRoleToUser(String bearerToken,String keycloakId,String role) {
        log.info("adding role to user with keycloakId: {}", keycloakId);
        List<LinkedHashMap<String, String>> rawRoles = keycloakClient.getRoles(bearerToken);
        List<RoleRepresentation> keycloakRoles = rawRoles.stream()
                .map(KeycloakMapper::convertToRoleRepresentation)
                .filter(keycloakRole -> keycloakRole.getName().equals(role))
                .toList();
        keycloakClient.addRoleToUser(bearerToken, keycloakId, keycloakRoles);
    }

    private void sendPasswordResetEmail(String keycloakId) {
        log.info("sending password reset email for keycloakId: {}", keycloakId);
        String bearerToken = getAdminBearerToken();
        List<String> actions = Collections.singletonList("UPDATE_PASSWORD");
        keycloakClient.resetPassword(bearerToken, keycloakId, actions);
    }

    private String inferAndValidateRole(AddMsUserReq msUser) {
        boolean hasProfession = msUser.getProfessionName() != null && !msUser.getProfessionName().isBlank();

        boolean isDoctorCandidate = hasProfession;
        boolean isPatientCandidate = !hasProfession;

        String declaredRole = msUser.getRole() != null ? msUser.getRole().trim().toLowerCase() : null;

        if (declaredRole != null) {
            switch (declaredRole) {
                case "doctor":
                    if (!isDoctorCandidate)
                        throw new IllegalArgumentException("doctor needs to have a profession");
                    return "doctor";
                case "patient":
                    if (!isPatientCandidate)
                        throw new IllegalArgumentException("patient doesn't have a profession");
                    return "patient";
                default:
                    throw new IllegalArgumentException("not recognizable role: " + declaredRole);
            }
        }

        if (isDoctorCandidate) return "doctor";
        if (isPatientCandidate) return "patient";

        throw new IllegalArgumentException("not handled error in registering user");
    }

}
