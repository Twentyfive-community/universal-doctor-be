package request.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshLoginReq {
    private String refresh_token;

    private String client_id;

    private String client_secret;

    private String grant_type;
}
