package jakarta.ws.client.test.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class IncomingChatMessage {

    private ChatMessage message;

}
