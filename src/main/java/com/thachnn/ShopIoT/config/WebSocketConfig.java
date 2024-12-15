package com.thachnn.ShopIoT.config;

import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.security.CustomerDecode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.*;


@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@Order(2)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    CustomerDecode customerDecode;

    @Autowired
    JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic", "/notifications");
        registry.setUserDestinationPrefix("/notifications");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:5173")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Authenticate user or CONNECT
                if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
                    //Extract JWT token from header, validate it and extract user authorities
                    var authHeader = accessor.getFirstNativeHeader("Authorization");
                    if(authHeader == null || !authHeader.startsWith("Bearer" + " ")){

                        //If there is no token present then we should interrupt handshake process
                        //and throw an AccessDeniedException
                        throw new AccessDeniedException(ErrorApp.UNAUTHENTICATION.getMessage());
                    }
                    var token = authHeader.substring("Bearer".length() + 1);
                    Jwt jwt;
                    try {
                        jwt = customerDecode.decode(token);
                    } catch (JwtException exception) {
                        log.warn(exception.getMessage());
                        throw new AccessDeniedException(ErrorApp.UNAUTHENTICATION.getMessage());
                    }
                    JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) jwtAuthenticationConverter.convert(jwt);
                    accessor.setUser(authenticationToken);
                }

                if(accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
                    String destination = accessor.getDestination();
                    JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) accessor.getUser();
                    if(!hasPermission(destination, authenticationToken)){
                        throw new AccessDeniedException("Unauthorized access to destination: " + destination);
                    }
                }

                return message;
            }

            private boolean hasPermission(String destination, JwtAuthenticationToken authenticationToken){
                if(authenticationToken == null){
                    throw new AccessDeniedException(ErrorApp.UNAUTHENTICATION.getMessage());
                }

                if( destination.equals("/topic/admin")
                ){
                    return authenticationToken.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                }
                if(destination.contains("/notifications/user/")) {
                    String username = destination.substring("/notifications/user/".length());
                    boolean checkRole = authenticationToken.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

                    boolean checkUser = username.equals(
                            (String) authenticationToken.getToken().getClaimAsMap("data").get("username")
                    );
                    return checkRole && checkUser;
                }

                return false;
            }
        });

    }
}
