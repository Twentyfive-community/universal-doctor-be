package org.universaldoctor.msuser.mapper;

import enums.Sex;
import model.Patient;
import org.springframework.stereotype.Service;
import request.keycloak.AddMsUserReq;

@Service
public class PatientMapper {

    public Patient mapAddMsUserReqToPatient(AddMsUserReq msUser) {
        Patient patient = new Patient();
        patient.setFirstName(msUser.getFirstName());
        patient.setLastName(msUser.getLastName());
        patient.setPhoneNumber(msUser.getPhoneNumber());
        patient.setAddress(msUser.getAddress());
        patient.setSex(Sex.valueOf(msUser.getSex()));
        patient.setTaxCode(msUser.getTaxCode());
        patient.setActive(true);
        patient.setEmail(msUser.getEmail());
        patient.setNationality(msUser.getNationality());
        return patient;
    }
}
