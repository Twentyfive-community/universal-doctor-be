package org.universaldoctor.msuser.service;

import dto.KeycloakUser;
import exception.DoctorAlreadyAcceptedException;
import exception.TokenRetrievalException;
import lombok.extern.slf4j.Slf4j;
import model.Doctor;
import model.MsUser;
import model.Patient;
import model.Profession;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.universaldoctor.msuser.client.KeycloakClient;
import org.universaldoctor.msuser.mapper.DoctorMapper;
import org.universaldoctor.msuser.mapper.KeycloakMapper;
import org.universaldoctor.msuser.mapper.PatientMapper;
import request.keycloak.*;
import response.keycloak.LoginRes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

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
        LoginMsUserReq loginMsUserReq = new LoginMsUserReq("adminrealm","password");
        return "Bearer " + getToken(loginMsUserReq).getAccessToken();
    }

    public LoginRes getToken(LoginMsUserReq loginMsUserReq) {
        log.info("request: {}", loginMsUserReq);
        try {
            TokenRequest tokenRequest = keycloakMapper.loginRequestToTokenRequest(loginMsUserReq);
            return keycloakClient.getToken(tokenRequest);
        } catch (Exception e) {
            log.error("error retrieving access token", e);
            throw new TokenRetrievalException(e.getMessage());
        }

    }

    public LoginRes refreshToken(RefreshTokenReq refreshTokenReq) {
        log.info("request: {}", refreshTokenReq);
        try {
            RefreshLoginReq refreshLoginReq = keycloakMapper.refreshTokenRequestToRefreshLoginRequest(refreshTokenReq);
            return keycloakClient.getRefreshToken(refreshLoginReq);
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


    @Transactional
    public void addMsUser(AddMsUserReq msUser) {
        log.info("request: {}", msUser);

        String role = inferAndValidateRole(msUser.getRole(), msUser.getProfessionName());

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

    @Transactional
    public void updateUser(UpdateMsUserReq msUser) {
        log.info("request: {}", msUser);

        switch (msUser.getRole()) {
            case "patient" -> {
                log.info("Updating patient: {}", msUser);
                Patient patient = patientService.findByEmail(msUser.getEmail());
                patientMapper.updateMsUserFromUpdateMsUserReq(msUser,patient);
                updateMsUserToRealm(patient);
                patientService.save(patient);
            }
            case "doctor" -> {
                Profession profession;
                log.info("Updating doctor: {}", msUser);
                Doctor doctor = doctorService.findByEmail(msUser.getEmail());
                if(msUser.getProfessionName()== null || msUser.getProfessionName().isBlank()){
                    profession = doctor.getProfession();
                } else {
                    profession = professionService.findByName(msUser.getProfessionName());
                }
                doctorMapper.updateDoctorFromUpdateMsUserReq(msUser,doctor, profession);
                updateMsUserToRealm(doctor);
                doctorService.save(doctor);
            }
            default -> throw new IllegalStateException("Unexpected role: " + msUser.getRole());
        }
    }


    @Transactional
    public void acceptDoctor(String email) {
        log.info("request: {}", email);
        Doctor doctor = doctorService.findByEmail(email);
        if (doctor.getAccepted().equals(false)) {
            doctor.setAccepted(true);
            doctor.setActive(true);
            updateMsUserToRealm(doctor);

            doctorService.save(doctor);
        } else {
            throw new DoctorAlreadyAcceptedException("this doctor: "+doctor+" is already accepted. Did you mean set him active?");
        }
    }

    @Transactional
    public void toggleStatus(ToggleStatusMsUserReq toggleStatusMsUserReq) {
        log.info("request: {}", toggleStatusMsUserReq);
        switch (toggleStatusMsUserReq.getRole()) {
            case "patient" -> {
                log.info("toggle status patient: {}", toggleStatusMsUserReq);
                Patient patient = patientService.findByEmail(toggleStatusMsUserReq.getEmail());
                patient.setActive(!patient.getActive());
                updateMsUserToRealm(patient);
                patientService.save(patient);
            }
            case "doctor" -> {
                log.info("toggle status doctor: {}", toggleStatusMsUserReq);
                Doctor doctor = doctorService.findByEmail(toggleStatusMsUserReq.getEmail());

                if (doctor.getAccepted().equals(false) && doctor.getActive().equals(false)) {
                    throw new IllegalStateException("can't set active a doctor whom is not accepted!");
                }

                doctor.setActive(!doctor.getActive());
                updateMsUserToRealm(doctor);
                doctorService.save(doctor);
            }
            default -> throw new IllegalStateException("Unexpected role: " + toggleStatusMsUserReq.getRole());
        }
    }

    private void addMsUserToRealm(MsUser msUser, String role) {
        log.info("adding user to realm: {}", msUser);
        String bearerToken = getAdminBearerToken();
        KeycloakUser keycloakUser = keycloakMapper.msUserToKeycloakUser(msUser);
        ResponseEntity<Object> response = keycloakClient.add(bearerToken, keycloakUser);
        String keycloakId = getKeycloakIdFromResponse(response);
        addRoleToUser(bearerToken, keycloakId,role);
        sendPasswordResetEmail(keycloakId);
        msUser.setKeycloakId(keycloakId);
    }

    private void updateMsUserToRealm(MsUser msUser) {
        log.info("updating user to realm: {}", msUser);
        String bearerToken = getAdminBearerToken();
        KeycloakUser keycloakUser = keycloakMapper.msUserToKeycloakUser(msUser);
        keycloakClient.update(bearerToken, msUser.getKeycloakId(), keycloakUser);
    }

    private void addRoleToUser(String bearerToken,String keycloakId,String role) {
        log.info("adding role to user with keycloakId: {}", keycloakId);
        List<LinkedHashMap<String, String>> rawRoles = keycloakClient.getRoles(bearerToken);
        List<RoleRepresentation> keycloakRoles = rawRoles.stream()
                .map(this::convertToRoleRepresentation)
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

    private String inferAndValidateRole(String role, String professionName) {
        boolean hasProfession = professionName != null && !professionName.isBlank();

        boolean isDoctorCandidate = hasProfession;
        boolean isPatientCandidate = !hasProfession;

        String declaredRole = role != null ? role.trim().toLowerCase() : null;

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

    private String getKeycloakIdFromResponse(ResponseEntity<Object> response) {
        String[] stringArray = response.getHeaders().get("location").get(0).split("/");
        return stringArray[stringArray.length - 1];
    }

    private RoleRepresentation convertToRoleRepresentation(LinkedHashMap<String, String> rawRole) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(rawRole.get("name"));
        role.setId(rawRole.get("id"));
        return role;
    }
}
