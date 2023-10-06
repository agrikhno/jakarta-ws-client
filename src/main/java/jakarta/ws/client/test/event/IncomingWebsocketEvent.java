package jakarta.ws.client.test.event;

import jakarta.ws.client.test.event.type.IncomingEventType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.ws.client.test.model.IncomingChatMessage;


@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IncomingWebsocketEvent {

    /**
     * Event type
     */
    private IncomingEventType type;

    /**
     * Event payload
     */
    private IncomingChatMessage event;


    public static IncomingWebsocketEvent message(IncomingChatMessage incomingCallMessage) {
        return IncomingWebsocketEvent.builder()
                .type(IncomingEventType.MESSAGE)
                .event(incomingCallMessage)
                .build();
    }

}
