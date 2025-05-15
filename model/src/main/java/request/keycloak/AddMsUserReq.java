package request.keycloak;

import lombok.Data;

@Data
public class AddMsUserReq {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String sex;
    private String taxCode;
    private Boolean active;
    private String email;
    private String imgUrl;
    private String nationality;

    //Se è un dottore, compilare anche questi
    private String professionName;
    private Double hourlyRate;


    private String ruoloUtente;
    //se ruoloUtente è paziente, ma ha una professione => ERRORE!
    //se ruoloUtente è dottore, ma non ha una professione => ERRORE!
}
