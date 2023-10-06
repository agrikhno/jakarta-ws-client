package jakarta.ws.client.test.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Encoder;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import jakarta.ws.client.test.event.OutgoingWebsocketEvent;
import jakarta.ws.client.test.util.CustomJacksonObjectMapper;

@Log4j2
public class TextWebsocketVertoEventEncoder implements Encoder.Text<OutgoingWebsocketEvent<?>> {

    private static final ObjectMapper jackson = CustomJacksonObjectMapper.getJackson();

    @Override
    @SneakyThrows
    public String encode(OutgoingWebsocketEvent<?> outgoingWebsocketEvent) {
        return jackson.writeValueAsString(outgoingWebsocketEvent);
    }

}
