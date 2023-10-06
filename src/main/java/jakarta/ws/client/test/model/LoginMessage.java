package jakarta.ws.client.test.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginMessage {

    private String userId;

    private String roomId;

}
