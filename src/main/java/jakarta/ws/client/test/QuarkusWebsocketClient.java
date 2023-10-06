package jakarta.ws.client.test;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.SessionException;
import jakarta.websocket.WebSocketContainer;
import jakarta.ws.client.test.convert.TextWebsocketVertoEventDecoder;
import jakarta.ws.client.test.convert.TextWebsocketVertoEventEncoder;
import jakarta.ws.client.test.event.IncomingWebsocketEvent;
import jakarta.ws.client.test.event.OutgoingWebsocketEvent;
import jakarta.ws.client.test.model.LoginMessage;
import jakarta.ws.client.test.model.OutgoingClientMessage;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@Log4j2
@Builder
public class QuarkusWebsocketClient extends Endpoint {

    private final URI serverURI;
    private final String room;
    private final WebSocketContainer webSocketContainer;
    private final ScheduledExecutorService executorService;
    private final String userId;

    public void connect() {
        try {
            final ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                    .decoders(List.of(TextWebsocketVertoEventDecoder.class))
                    .encoders(List.of(TextWebsocketVertoEventEncoder.class))
                    .build();

            webSocketContainer.connectToServer(this, clientEndpointConfig, serverURI);
        } catch (Exception e) {
            log.error("Error connecting to server. message: {}", e.getMessage());
        }
    }

    /**
     * Developers must implement this method to be notified when a new conversation has just begun.
     * <p>
     * Note:
     * <ul>
     * <li>It is permitted to send messages from this method.</li>
     * <li>It is permitted to add {@link MessageHandler}s from this method. No messages will be
     *     mapped to the appropriate {@link MessageHandler} until this method has completed.</li>
     * </ul>
     *  @param session the session that has just been activated.
     *
     * @param config the configuration used to configure this endpoint.
     */
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        log.info("Successfully connected to: {}", serverURI.toString());
        session.getAsyncRemote().sendObject(OutgoingWebsocketEvent.login(
                        LoginMessage.builder()
                                .roomId(room)
                                .userId(userId)
                                .build()
                )
        );

        session.addMessageHandler(new MessageHandler.Whole<IncomingWebsocketEvent>() {
            /**
             * Called when the message has been fully received.
             *
             * @param response the message data.
             */
            @Override
            public void onMessage(IncomingWebsocketEvent response) {
//                IncomingChatMessage incomingChatMessage = response.getEvent();
//                log.info(incomingChatMessage.toString());
            }
        });

        executorService.scheduleAtFixedRate(() -> session.getAsyncRemote()
                .sendObject(OutgoingWebsocketEvent.message(
                                OutgoingClientMessage.builder()
                                        .id(userId)
                                        .msg(UUID.randomUUID() + " Traditional Java stacks were engineered for monolithic applications with long startup times and large memory requirements in a world where the cloud, containers, and Kubernetes did not exist. Java frameworks needed to evolve to meet the needs of this new world. Quarkus was created to enable Java developers to create applications for a modern, cloud-native world. Quarkus is a Kubernetes-native Java framework tailored for GraalVM and HotSpot, crafted from best-of-breed Java libraries and standards. The goal is to make Java the leading platform in Kubernetes and serverless environments while offering developers a framework to address a wider range of distributed application architectures.Developers are critical to the success of almost every organization and they need the tools to build cloud-native applications quickly and efficiently. Quarkus provides a frictionless development experience through a combination of tools, libraries, extensions, and more. Quarkus makes developers more efficient with tools to improve the inner loop development cycle while in dev mode.Quarkus was built from the ground up for Kubernetes making it easy to deploy applications without having to understand all of the complexities of the platform. Quarkus allows developers to automatically generate Kubernetes resources including building and deploying container images without having to manually create YAML files.Quarkus is designed to seamlessly combine the familiar imperative style code and the non-blocking, reactive style when developing applications.\nThis is helpful for both Java developers who are used to working with the imperative model and don’t want to switch things up, and those working with a cloud-native/reactive approach. The Quarkus development model can adapt itself to whatever app you’re developingAs much processing as possible is done at build time; thus, your application only contains the classes used at runtime. In traditional frameworks, all the classes required to perform the initial application deployment hang around for the application’s life, even though they are only used once. With Quarkus, they are not even loaded into the production JVM! Quarkus does not stop here. During the build-time processing, it prepares the initialization of all components used by your application. It results in less memory usage and faster startup time as all metadata processing has already been done.")
                                        .build()
                        )
                ), 5000, ThreadLocalRandom.current().nextInt(500, 1000), TimeUnit.MILLISECONDS);
    }

    /**
     * This method is called immediately prior to the session with the remote peer being closed. It is called whether
     * the session is being closed because the remote peer initiated a close and sent a close frame, or whether the
     * local websocket container or this endpoint requests to close the session. The developer may take this last
     * opportunity to retrieve session attributes such as the ID, or any application data it holds before it becomes
     * unavailable after the completion of the method. Developers should not attempt to modify the session from within
     * this method, or send new messages from this call as the underlying connection will not be able to send them at
     * this stage.
     *
     * @param session     the session about to be closed.
     * @param closeReason the reason the session was closed.
     */
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        CloseReason.CloseCode closeReasonCode = closeReason.getCloseCode();
        String closeReasonMsg = closeReason.getReasonPhrase();
        log.info("User disconnected. Code: {}, Message: {}", closeReasonCode, closeReasonMsg);
    }

    /**
     * Developers may implement this method when the web socket session creates some kind of error that is not modeled
     * in the web socket protocol. This may for example be a notification that an incoming message is too big to handle,
     * or that the incoming message could not be encoded.
     *
     * <p>
     * There are a number of categories of exception that this method is (currently) defined to handle:
     * <ul>
     * <li>connection problems, for example, a socket failure that occurs before the web socket connection can be
     * formally closed. These are modeled as {@link SessionException}s</li>
     * <li>runtime errors thrown by developer created message handlers calls.</li>
     * <li>conversion errors encoding incoming messages before any message handler has been called. These are modeled as
     * {@link DecodeException}s</li>
     * </ul>
     *  @param session the session in use when the error occurs.
     *
     * @param thr the throwable representing the problem.
     */
    @Override
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
        log.error(thr);
    }

}
