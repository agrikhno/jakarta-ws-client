package jakarta.ws.client.test.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OutgoingClientMessage {

    private final String id;

    private final String msg;

}
