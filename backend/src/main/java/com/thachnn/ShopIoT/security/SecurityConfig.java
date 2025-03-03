package com.thachnn.ShopIoT.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.exception.ErrorApp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig {
    private final String[] ENDPOINTS_PUBLIC = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**",
            "/auth/login", "/auth/logout", "/auth/refresh", "/auth/introspect",
            "/auth/oauth/authorization/google",
            "/ws",
            "/payment/vn-pay/IPN", "/payment/zalo-pay/call-back",

    };

    @Autowired
    private CustomerDecode customerDecode;

    @Bean
    public CorsFilter corsFilter() {
        List<String> ALLOWED_ORIGINS = List.of(
                "http://localhost:5173",
                "https://shop-iot-fe.vercel.app",
                "https://a03b-2401-d800-7fe1-70c4-2cbd-e47f-537c-ae7b.ngrok-free.app"
                );

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi thông tin xác thực như cookie
        config.setAllowedOrigins(ALLOWED_ORIGINS); // Thêm domain React
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setExposedHeaders(List.of("Authorization")); // Cho phép client đọc các header cụ thể

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Áp dụng cho tất cả các endpoint

        return new CorsFilter(source);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(ENDPOINTS_PUBLIC).permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/brands").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/solutions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/overall/{productId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/forProductByRating/**").permitAll()

                        .requestMatchers("/brands/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        customizer -> customizer.accessDeniedHandler(customAccessDeniedHandler())
                );

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customerDecode)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())

        );


        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    AccessDeniedHandler customAccessDeniedHandler(){
        return (request, response, accessDeniedException) -> {
            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(ErrorApp.ACCESS_DENIED.getMessage())
                    .build();

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        };
    }
}
