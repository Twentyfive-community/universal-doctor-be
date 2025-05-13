package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUser extends UserRepresentation {

    private String email;
    private String username;
}
