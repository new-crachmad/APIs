package br.com.servicos_auto.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.servicos_auto.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Permitir acesso ao root
                .requestMatchers("/").permitAll()

                // Actuator liberado total
                .requestMatchers("/actuator/**").permitAll()

                // Autenticação e Swagger
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/docs", "/docs/**").permitAll()

                // Cadastros públicos
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/prestadores").permitAll()

                // Consultas públicas
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/prestadores/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/anuncios/**").permitAll()
                
                // CORREÇÃO: Adicionando acesso GET público para a API /servicos
                .requestMatchers(HttpMethod.GET, "/api/v1/servicos/**").permitAll() 
                
                // Regras autenticadas usando padrões corretos (sem {id})
                .requestMatchers(HttpMethod.PATCH, "/api/usuarios/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/prestadores/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/prestadores/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/**/upload-image").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/prestadores/**/upload-image").authenticated()

                // Somente prestador pode manipular anúncios
                .requestMatchers(HttpMethod.POST, "/api/anuncios/**").hasAuthority("PRESTADOR_SERVICO")
                .requestMatchers(HttpMethod.PATCH, "/api/anuncios/**").hasAuthority("PRESTADOR_SERVICO")
                .requestMatchers(HttpMethod.DELETE, "/api/anuncios/**").hasAuthority("PRESTADOR_SERVICO")
                .requestMatchers(HttpMethod.POST, "/api/anuncios/**/upload-image").hasAuthority("PRESTADOR_SERVICO")

                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtTokenFilter(jwtUtil, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}