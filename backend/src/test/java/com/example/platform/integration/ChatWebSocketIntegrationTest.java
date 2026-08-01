package com.example.platform.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.entity.*;
import com.example.platform.repository.*;
import com.example.platform.security.JwtService;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional
class ChatWebSocketIntegrationTest {


    @LocalServerPort
    private int port;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelMemberRepository channelMemberRepository;

    @Autowired
    private JwtService jwtService;



    private WebSocketStompClient stompClient;

    private User user;

    private Channel channel;

    private String token;



    @BeforeEach
    void setup() {


        stompClient =
                new WebSocketStompClient(
                        new StandardWebSocketClient()
                );


        stompClient.setMessageConverter(
                new MappingJackson2MessageConverter()
        );


        String random =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);



        user =
                userRepository.save(
                        User.builder()
                                .username("testuser_" + random)
                                .email(random + "@test.com")
                                .password("password")
                                .displayName("Test User")
                                .role(Role.USER)
                                .status(UserStatus.ONLINE)
                                .build()
                );



        Workspace workspace =
                workspaceRepository.save(
                        Workspace.builder()
                                .name("Backend Team")
                                .description("Backend Development")
                                .type(WorkspaceType.PRIVATE)
                                .logoUrl("logo.png")
                                .owner(user)
                                .build()
                );



        channel =
                channelRepository.save(
                        Channel.builder()
                                .name("general")
                                .type(ChannelType.PUBLIC)
                                .workspace(workspace)
                                .createdBy(user)
                                .build()
                );



        channelMemberRepository.save(
                ChannelMember.builder()
                        .user(user)
                        .channel(channel)
                        .addedBy(user)
                        .build()
        );



        token = jwtService.generateToken(user);

    }



    @Test
    void shouldSendMessageThroughWebSocket() throws Exception {


        String url =
                "ws://localhost:"
                + port
                + "/ws";



        BlockingQueue<MessageResponse> messages =
                new LinkedBlockingQueue<>();



        /*
         * HTTP WebSocket handshake authentication
         */
        WebSocketHttpHeaders handshakeHeaders =
                new WebSocketHttpHeaders();

        handshakeHeaders.add(
                "Authorization",
                "Bearer " + token
        );



        /*
         * STOMP CONNECT authentication
         */
        StompHeaders stompHeaders =
                new StompHeaders();

        stompHeaders.add(
                "Authorization",
                "Bearer " + token
        );



        StompSession session =
                stompClient
                        .connectAsync(
                                url,
                                handshakeHeaders,
                                stompHeaders,
                                new StompSessionHandlerAdapter() {
                                }
                        )
                        .get(
                                5,
                                TimeUnit.SECONDS
                        );



        assertTrue(session.isConnected());



        session.subscribe(
                "/topic/channel/" + channel.getId(),

                new StompFrameHandler() {


                    @Override
                    public java.lang.reflect.Type getPayloadType(
                            StompHeaders headers) {

                        return MessageResponse.class;
                    }



                    @Override
                    public void handleFrame(
                            StompHeaders headers,
                            Object payload) {

                        messages.add(
                                (MessageResponse) payload
                        );

                    }
                }
        );



        ChatMessageRequest request =
                ChatMessageRequest.builder()
                        .channelId(channel.getId())
                        .content("Hello websocket")
                        .type(MessageType.TEXT)
                        .build();



        session.send(
                "/app/chat.send",
                request
        );



        MessageResponse response =
                messages.poll(
                        5,
                        TimeUnit.SECONDS
                );



        assertNotNull(response);


        assertEquals(
                "Hello websocket",
                response.getContent()
        );


        assertEquals(
                user.getUsername(),
                response.getSenderUsername()
        );


        session.disconnect();

    }

}