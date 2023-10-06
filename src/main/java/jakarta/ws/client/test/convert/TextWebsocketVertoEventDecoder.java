package jakarta.ws.client.test.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Decoder;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import jakarta.ws.client.test.event.IncomingWebsocketEvent;
import jakarta.ws.client.test.event.type.IncomingEventType;
import jakarta.ws.client.test.model.IncomingChatMessage;
import jakarta.ws.client.test.util.CustomJacksonObjectMapper;


@Log4j2
public class TextWebsocketVertoEventDecoder implements Decoder.Text<IncomingWebsocketEvent> {

    private static final ObjectMapper jackson = CustomJacksonObjectMapper.getJackson();

    @Override
    @SneakyThrows
    public IncomingWebsocketEvent decode(String message) {
//        log.info("RESPONSE: {}", message);
        final String eventType = jackson.readTree(message).get("type").asText();
        final JsonNode payload = jackson.readTree(message).get("source");
        return switch (IncomingEventType.valueOf(eventType)) {
            case MESSAGE ->
                    IncomingWebsocketEvent.message(jackson.treeToValue(payload, IncomingChatMessage.class));
        };
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }
}