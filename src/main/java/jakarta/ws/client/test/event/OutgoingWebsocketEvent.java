package jakarta.ws.client.test.event;

import jakarta.ws.client.test.event.type.OutgoingEventType;
import jakarta.ws.client.test.model.LoginMessage;
import jakarta.ws.client.test.model.OutgoingClientMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OutgoingWebsocketEvent<T> {

    /**
     * Event type
     */
    private OutgoingEventType type;

    /**
     * Event payload
     */
    private T source;

    public static OutgoingWebsocketEvent<LoginMessage> login(LoginMessage loginMessage) {
        return OutgoingWebsocketEvent.<LoginMessage>builder()
                .type(OutgoingEventType.LOGIN)
                .source(loginMessage)
                .build();
    }

    public static OutgoingWebsocketEvent<OutgoingClientMessage> message(OutgoingClientMessage incomingCallMessage) {
        return OutgoingWebsocketEvent.<OutgoingClientMessage>builder()
                .type(OutgoingEventType.MESSAGE)
                .source(incomingCallMessage)
                .build();
    }

}
