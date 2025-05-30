package request.keycloak;

import enums.Sex;
import lombok.Data;

@Data
public class AddMsUserReq {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Sex sex;
    private String taxCode;
    private String email;
    private String nationality;

    //Se è un dottore, compilare anche questi
    private String professionName;

    private String role;
}
