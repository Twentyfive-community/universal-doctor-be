package request.keycloak;

import lombok.Data;

@Data
public class AddRealmRoleReq {
    private String name;
    private String description;
}
