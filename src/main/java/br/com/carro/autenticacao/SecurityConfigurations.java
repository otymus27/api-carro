package br.com.carro.autenticacao;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Value("${jwt.secret}")
    private String jwtSecret; // pode ser base64-url, base64 comum ou texto puro

    private byte[] secretBytes; // armazenamos a chave já resolvida
    private SecretKey hmacKey;

    @PostConstruct
    public void initSecret() {
        this.secretBytes = resolveSecretBytes(jwtSecret);
        if (this.secretBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret deve ter pelo menos 32 bytes (256 bits) após o processamento. " +
                            "Use um segredo mais longo."
            );
        }
        this.hmacKey = new SecretKeySpec(this.secretBytes, "HmacSHA256");

        // Diagnóstico opcional
        System.out.println("--- Diagnóstico JWT ---");
        System.out.println("jwt.secret (string) length: " + (jwtSecret == null ? 0 : jwtSecret.length()));
        System.out.println("secretBytes length: " + this.secretBytes.length + " bytes");
    }

    private byte[] resolveSecretBytes(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("jwt.secret não definido");
        }

        // 1) tenta Base64-URL
        try {
            return Base64.getUrlDecoder().decode(raw);
        } catch (IllegalArgumentException ignored) {}

        // 2) tenta Base64 padrão
        try {
            return Base64.getDecoder().decode(raw);
        } catch (IllegalArgumentException ignored) {}

        // 3) usa como texto puro (UTF-8)
        return raw.getBytes(StandardCharsets.UTF_8);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // Usa bytes diretamente no ImmutableSecret (forma recomendada)
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretBytes));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Usa a mesma chave no decoder
        return NimbusJwtDecoder.withSecretKey(hmacKey).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
