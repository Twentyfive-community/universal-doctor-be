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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private KeycloakClient keycloakClient;

    @Autowired
    private PatientService patientService;
    @Autowired
    private ProfessionService professionService;
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private KeycloakMapper keycloakMapper;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private DoctorMapper doctorMapper;

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
        String role ="";
        if ((msUser.getProfessionName() == null || msUser.getProfessionName().isBlank()) && msUser.getHourlyRate() == null) {
            log.info("adding a patient: {}", msUser);
            role = "patient";
            Patient patient = patientMapper.mapAddMsUserReqToPatient(msUser);
            addMsUserToRealm(patient,role);
            patientService.save(patient);
        } else {
            log.info("adding a doctor: {}", msUser);
            //role ="doctor";
            Profession profession = professionService.findByName(msUser.getProfessionName());
            Doctor doctor = doctorMapper.mapAddMsUserReqToDoctor(msUser, profession);
            //addMsUserToRealm(doctor,role);
            doctorService.save(doctor);
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

    public void addRoleToUser(String bearerToken,String keycloakId,String role) {
        log.info("adding role to user with keycloakId: {}", keycloakId);
        List<LinkedHashMap<String, String>> rawRoles = keycloakClient.getRoles(bearerToken);
        List<RoleRepresentation> keycloakRoles = rawRoles.stream()
                .map(KeycloakMapper::convertToRoleRepresentation)
                .filter(keycloakRole -> keycloakRole.getName().equals(role)) // Filtra il ruolo specifico
                .toList();
        keycloakClient.addRoleToUser(bearerToken, keycloakId, keycloakRoles);
    }

    public boolean sendPasswordResetEmail(String keycloakId) {
        log.info("sending password reset email for keycloakId: {}", keycloakId);
        String bearerToken = getAdminBearerToken();
        // Define the actions to be executed, in this case, UPDATE_PASSWORD
        List<String> actions = Collections.singletonList("UPDATE_PASSWORD");

        // Call the Feign client method to send the reset email
        keycloakClient.resetPassword(bearerToken, keycloakId, actions);
        return true;
    }
}
