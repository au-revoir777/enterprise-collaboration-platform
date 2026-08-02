package com.example.platform.integration;

import org.springframework.core.env.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.ExecutionException;

import java.lang.reflect.Type;

import com.example.platform.repository.ChannelMemberRepository;
import com.example.platform.repository.ChannelRepository;
import com.example.platform.repository.WorkspaceRepository;
import com.example.platform.repository.MessageRepository;
import com.example.platform.repository.UserRepository;
import com.example.platform.security.JwtService;

import jakarta.websocket.DeploymentException;

import com.example.platform.entity.User;
import com.example.platform.entity.UserStatus;
import com.example.platform.entity.Workspace;
import com.example.platform.entity.WorkspaceType;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.entity.Channel;
import com.example.platform.entity.ChannelMember;
import com.example.platform.entity.ChannelType;
import com.example.platform.entity.MessageType;
import com.example.platform.entity.Role;
import com.example.platform.entity.Message;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// import org.springframework.transaction.annotation.Transactional;

// @Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatWebSocketIntegrationTest {
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
        private MessageRepository messageRepository;

        @Autowired
        private JwtService jwtService;

        private WebSocketStompClient stompClient;

        private User user;
        private Workspace workspace;
        private Channel channel;
        private ChannelMember channelMember;

        private String jwt;

        @Autowired
        private Environment environment;

        @BeforeEach
        void checkProfile() {
                System.out.println(environment.getProperty("test.message"));
        }

        @BeforeEach
        void setUp() {

                stompClient = new WebSocketStompClient(
                                new StandardWebSocketClient());

                stompClient.setMessageConverter(
                                new JacksonJsonMessageConverter());

                String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

                user = User.builder()
                                .username("roshan-" + suffix)
                                .displayName("Roshan")
                                .email("roshan-" + suffix + "@example.com")
                                .password("dummy-password")
                                .role(Role.USER)
                                .status(UserStatus.OFFLINE)
                                .active(true)
                                .build();

                user = userRepository.save(user);

                workspace = Workspace.builder()
                                .name("Backend Team " + suffix)
                                .description("Backend Development")
                                .type(WorkspaceType.PRIVATE)
                                .logoUrl("logo.png")
                                .owner(user)
                                .build();

                workspace = workspaceRepository.save(workspace);

                channel = Channel.builder()
                                .name("backend")
                                .description("Backend discussions")
                                .type(ChannelType.PUBLIC)
                                .workspace(workspace)
                                .createdBy(user)
                                .build();

                channel = channelRepository.save(channel);

                channelMember = ChannelMember.builder()
                                .user(user)
                                .channel(channel)
                                .addedBy(user)
                                .build();

                channelMember = channelMemberRepository.save(channelMember);

                jwt = jwtService.generateToken(user);
        }

        @Test
        void printProperties() {
                System.out.println(environment.getActiveProfiles()[0]);
                System.out.println(environment.getProperty("spring.datasource.url"));
        }

        @Test
        void shouldEstablishAuthenticatedWebSocketSession() throws Exception {
                String url = "ws://localhost:" + port + "/ws?token=" + jwt;

                StompSession session = stompClient
                                .connectAsync(
                                                url,
                                                new StompSessionHandlerAdapter() {
                                                })
                                .get(5, TimeUnit.SECONDS);

                assertNotNull(session);
                assertTrue(session.isConnected());
                session.disconnect();
        }

        @Test
        void shouldBroadcastMessageToSubscribedClient() throws Exception {
                String url = "ws://localhost:" + port + "/ws?token=" + jwt;

                StompSession session = stompClient
                                .connectAsync(url, new StompSessionHandlerAdapter() {
                                })
                                .get(5, TimeUnit.SECONDS);

                try {

                        BlockingQueue<MessageResponse> responses = new LinkedBlockingQueue<>();

                        session.subscribe(
                                        "/topic/channel/" + channel.getId(),
                                        new StompFrameHandler() {

                                                @Override
                                                public Type getPayloadType(StompHeaders headers) {
                                                        return MessageResponse.class;
                                                }

                                                @Override
                                                public void handleFrame(
                                                                StompHeaders headers,
                                                                Object payload) {

                                                        responses.add((MessageResponse) payload);
                                                }
                                        });

                        ChatMessageRequest request = ChatMessageRequest.builder()
                                        .channelId(channel.getId())
                                        .content("Hello everyone!")
                                        .type(MessageType.TEXT)
                                        .build();

                        session.send("/app/chat.send", request);

                        MessageResponse response = responses.poll(5, TimeUnit.SECONDS);
                        assertNotNull(response);
                        assertEquals("Hello everyone!", response.getContent());
                        assertEquals(user.getUsername(), response.getSenderUsername());
                        assertEquals(MessageType.TEXT, response.getType());
                        assertEquals(user.getDisplayName(),
                                        response.getSenderDisplayName());
                        assertFalse(response.isEdited());

                        List<Message> messages = messageRepository.findByChannelOrderByCreatedAtAsc(channel);
                        assertEquals(1, messages.size());
                        assertEquals("Hello everyone!", messages.get(0).getContent());
                        assertEquals(user.getId(),
                                        messages.get(0).getSender().getId());
                        assertEquals(channel.getId(),
                                        messages.get(0).getChannel().getId());
                } finally {
                        if (session.isConnected()) {
                                session.disconnect();
                        }
                }
        }

        @Test
        void shouldBroadcastMessageToSubscribedClients() throws Exception {
                String url = "ws://localhost:" + port + "/ws?token=" + jwt;

                StompSession sessionA = stompClient
                                .connectAsync(url, new StompSessionHandlerAdapter() {
                                })
                                .get(5, TimeUnit.SECONDS);
                StompSession sessionB = stompClient
                                .connectAsync(url, new StompSessionHandlerAdapter() {
                                })
                                .get(5, TimeUnit.SECONDS);

                try {

                        BlockingQueue<MessageResponse> responsesA = new LinkedBlockingQueue<>();
                        BlockingQueue<MessageResponse> responsesB = new LinkedBlockingQueue<>();

                        sessionA.subscribe(
                                        "/topic/channel/" + channel.getId(),
                                        new StompFrameHandler() {

                                                @Override
                                                public Type getPayloadType(StompHeaders headers) {
                                                        return MessageResponse.class;
                                                }

                                                @Override
                                                public void handleFrame(
                                                                StompHeaders headers,
                                                                Object payload) {

                                                        responsesA.add((MessageResponse) payload);
                                                }
                                        });

                        sessionB.subscribe(
                                        "/topic/channel/" + channel.getId(),
                                        new StompFrameHandler() {

                                                @Override
                                                public Type getPayloadType(StompHeaders headers) {
                                                        return MessageResponse.class;
                                                }

                                                @Override
                                                public void handleFrame(
                                                                StompHeaders headers,
                                                                Object payload) {

                                                        responsesB.add((MessageResponse) payload);
                                                }
                                        });

                        ChatMessageRequest request = ChatMessageRequest.builder()
                                        .channelId(channel.getId())
                                        .content("Hello everyone!")
                                        .type(MessageType.TEXT)
                                        .build();

                        sessionA.send("/app/chat.send", request);

                        MessageResponse responseA = responsesA.poll(5, TimeUnit.SECONDS);
                        MessageResponse responseB = responsesB.poll(5, TimeUnit.SECONDS);
                        assertNotNull(responseA);
                        assertNotNull(responseB);

                        assertEquals("Hello everyone!", responseA.getContent());
                        assertEquals("Hello everyone!", responseB.getContent());

                        assertEquals(user.getUsername(), responseA.getSenderUsername());
                        assertEquals(user.getUsername(), responseB.getSenderUsername());

                        assertEquals(MessageType.TEXT, responseA.getType());
                        assertEquals(MessageType.TEXT, responseB.getType());

                        assertEquals(user.getDisplayName(),
                                        responseA.getSenderDisplayName());
                        assertEquals(user.getDisplayName(),
                                        responseB.getSenderDisplayName());

                        assertFalse(responseA.isEdited());
                        assertFalse(responseB.isEdited());

                        assertEquals(responseA.getContent(), responseB.getContent());
                        assertEquals(responseA.getSenderUsername(), responseB.getSenderUsername());
                        assertEquals(responseA.getSenderDisplayName(), responseB.getSenderDisplayName());
                        assertEquals(responseA.getType(), responseB.getType());
                        assertEquals(responseA.getId(), responseB.getId());
                        assertEquals(responseA.getCreatedAt(), responseB.getCreatedAt());

                        List<Message> messages = messageRepository.findByChannelOrderByCreatedAtAsc(channel);
                        assertEquals(1, messages.size());
                        assertEquals("Hello everyone!", messages.get(0).getContent());
                        assertEquals(user.getId(),
                                        messages.get(0).getSender().getId());
                        assertEquals(channel.getId(),
                                        messages.get(0).getChannel().getId());
                } finally {
                        if (sessionA.isConnected()) {
                                sessionA.disconnect();
                        }
                        if (sessionB.isConnected()) {
                                sessionB.disconnect();
                        }
                }
        }

        @Test
        void shouldRejectWebSocketConnectionWhenJwtIsInvalid() {

                String url = "ws://localhost:" + port + "/ws?token=invalid-token";

                ExecutionException exception = assertThrows(
                                ExecutionException.class,
                                () -> stompClient
                                                .connectAsync(
                                                                url,
                                                                new StompSessionHandlerAdapter() {
                                                                })
                                                .get(5, TimeUnit.SECONDS));

                assertTrue(exception.getCause() instanceof DeploymentException);
        }

        @Test
        void shouldNotBroadcastMessageWhenUserIsNotChannelMember() throws Exception {
                String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                User userB = User.builder()
                                .username("parveen-" + suffix)
                                .displayName("Parveen")
                                .email("parveen-" + suffix + "@example.com")
                                .password("dummy-password")
                                .role(Role.USER)
                                .status(UserStatus.OFFLINE)
                                .active(true)
                                .build();

                userB = userRepository.save(userB);
                String jwtUserB = jwtService.generateToken(userB);
                String url = "ws://localhost:" + port + "/ws?token=" + jwtUserB;

                StompSession session = stompClient
                                .connectAsync(url, new StompSessionHandlerAdapter() {
                                })
                                .get(5, TimeUnit.SECONDS);

                try {

                        BlockingQueue<MessageResponse> responses = new LinkedBlockingQueue<>();

                        session.subscribe(
                                        "/topic/channel/" + channel.getId(),
                                        new StompFrameHandler() {

                                                @Override
                                                public Type getPayloadType(StompHeaders headers) {
                                                        return MessageResponse.class;
                                                }

                                                @Override
                                                public void handleFrame(
                                                                StompHeaders headers,
                                                                Object payload) {

                                                        responses.add((MessageResponse) payload);
                                                }
                                        });

                        ChatMessageRequest request = ChatMessageRequest.builder()
                                        .channelId(channel.getId())
                                        .content("Hello everyone!")
                                        .type(MessageType.TEXT)
                                        .build();

                        session.send("/app/chat.send", request);

                        MessageResponse response = responses.poll(2, TimeUnit.SECONDS);
                        assertNull(response);
                        assertTrue(messageRepository.findByChannelOrderByCreatedAtAsc(channel).isEmpty());
                        List<Message> messages = messageRepository.findByChannelOrderByCreatedAtAsc(channel);
                        assertTrue(messages.isEmpty());
                        assertTrue(session.isConnected());
                } finally {
                        if (session.isConnected()) {
                                session.disconnect();
                        }
                }
        }

        @Test
        void shouldNotBroadcastMessageWhenChannelDoesNotExist() throws Exception {
                String url = "ws://localhost:" + port + "/ws?token=" + jwt;

                ChatMessageRequest request = ChatMessageRequest.builder()
                                .channelId(999999L)
                                .content("Hello everyone!")
                                .type(MessageType.TEXT)
                                .build();

                StompSession session = stompClient
                                .connectAsync(url, new StompSessionHandlerAdapter() {
                                })
                                .get(5, TimeUnit.SECONDS);

                try {

                        BlockingQueue<MessageResponse> responses = new LinkedBlockingQueue<>();

                        session.subscribe(
                                        "/topic/channel/" + request.getChannelId(),
                                        new StompFrameHandler() {

                                                @Override
                                                public Type getPayloadType(StompHeaders headers) {
                                                        return MessageResponse.class;
                                                }

                                                @Override
                                                public void handleFrame(
                                                                StompHeaders headers,
                                                                Object payload) {

                                                        responses.add((MessageResponse) payload);
                                                }
                                        });

                        session.send("/app/chat.send", request);

                        MessageResponse response = responses.poll(2, TimeUnit.SECONDS);
                        assertNull(response);
                        assertTrue(session.isConnected());

                } finally {
                        if (session.isConnected()) {
                                session.disconnect();
                        }
                }
        }

}
