package jakarta.ws.client.test;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@RequiredArgsConstructor
@ApplicationScoped
@Startup
public class Bootstrap {

    private final List<QuarkusWebsocketClient> quarkusWebsocketClientList = new ArrayList<>();
    private final WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);


    void onStart(@Observes StartupEvent event) throws Exception {
        final URI uri = new URI("ws://127.0.0.1:8080/wstest");

        /* TEST CASE VARS
         *
         * roomCount - total room count in test
         * clientsPerRoom - number of clients connected the room in the next step
         * nextClientPeriodInMs - time interval after which the next {clientsPerRoom} clients will connect
         *
         */
        final int roomCount = 10;
        final int clientsPerRoom = 5;
        final int nextClientPeriodInMs = 15000;

        final AtomicLong atomicLong = new AtomicLong();

        executorService.scheduleAtFixedRate(() -> {
                    for (int i = 1; i <= roomCount; i++) {
                        for (int k = 0; k < clientsPerRoom; k++) {
                            QuarkusWebsocketClient client = QuarkusWebsocketClient.builder()
                                    .room("room_" + i)
                                    .serverURI(uri)
                                    .userId(UUID.randomUUID().toString())
                                    .webSocketContainer(webSocketContainer)
                                    .executorService(executorService)
                                    .build();
                            quarkusWebsocketClientList.add(client);
                            client.connect();
                        }
                    }

                    final long totalClientsPerRoom = atomicLong.incrementAndGet() * clientsPerRoom;
                    System.out.println(LocalDateTime.now() + " Clients per room connected: " + totalClientsPerRoom +  " Total clients connected: " + roomCount * totalClientsPerRoom);                },
                0,
                nextClientPeriodInMs,
                TimeUnit.MILLISECONDS);
    }
}
