package org.universaldoctor.msuser.mapper;

import enums.Sex;
import model.Doctor;
import model.Profession;
import org.springframework.stereotype.Service;
import request.keycloak.AddMsUserReq;

@Service
public class DoctorMapper {
    public Doctor mapAddMsUserReqToDoctor(AddMsUserReq msUser, Profession profession) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(msUser.getFirstName());
        doctor.setLastName(msUser.getLastName());
        doctor.setPhoneNumber(msUser.getPhoneNumber());
        doctor.setAddress(msUser.getAddress());
        doctor.setSex(Sex.valueOf(msUser.getSex()));
        doctor.setTaxCode(msUser.getTaxCode());
        doctor.setActive(false);
        doctor.setEmail(msUser.getEmail());
        doctor.setNationality(msUser.getNationality());
        doctor.setAccepted(false);
        doctor.setProfession(profession);
        doctor.setHourlyRate(msUser.getHourlyRate());
        return doctor;
    }
}
