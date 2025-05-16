package request.keycloak;

import enums.Sex;
import lombok.Data;

@Data
public class UpdateMsUserReq {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Sex sex;
    private String taxCode;
    private String nationality;

    //Se è un dottore, compilare anche questi
    private String professionName;

    private String role;
}
