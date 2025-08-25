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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // ✅ Habilita o uso de @PreAuthorize e @PostAuthorize
public class SecurityConfigurations {

    // Aqui injetamos o valor de 'jwt.secret' do application.properties/yml
    @Value("${jwt.secret}")
    private String jwtSecret;

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

        // ✅ Diagnóstico opcional: loga a chave para verificar a consistência
        System.out.println("--- Diagnóstico JWT ---");
        System.out.println("jwt.secret (string) length: " + (jwtSecret == null ? 0 : jwtSecret.length()));
        System.out.println("secretBytes length: " + this.secretBytes.length + " bytes");
        // Convertendo para String Base64 para visualização (NÃO FAÇA EM PROD SE FOR MUITO SENSIBIL)
        System.out.println("SecretKey (Base64 encoded for debug): " + Base64.getEncoder().encodeToString(this.secretBytes));
        System.out.println("--- Fim Diagnóstico JWT ---");
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // ✅ Adiciona o conversor customizado
                        )
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // ✅ BEAN ATUALIZADO: Configura como as autoridades são extraídas do JWT para Spring Security 6.x+
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 👉 Ajusta o prefixo do claim de roles (senão fica "SCOPE_" por padrão)
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    // ⚠️ Método para configurar as políticas CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 🚨 MUITO IMPORTANTE: Defina a origem do seu frontend Angular
        // Se a porta do Angular mudar, você precisará atualizar isso.
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // ✅ Permitir requisições desta origem

        // Métodos HTTP permitidos, incluindo OPTIONS
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH")); // ✅ Métodos HTTP permitidos

        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // ✅ Cabeçalhos permitidos
        config.setAllowCredentials(true); // ✅ Permite cookies e cabeçalhos de autenticação
        config.setMaxAge(3600L); // ✅ Tempo em segundos que a requisição preflight pode ser cacheada

        source.registerCorsConfiguration("/**", config); // Aplica esta configuração a todos os paths
        return source;
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
        // ✅ ADICIONADO: Log para verificar a chave sendo usada pelo encoder
        System.out.println("--- Diagnóstico JWT (JwtEncoder) ---");
        System.out.println("JwtEncoder usando SecretKey (Base64 encoded): " + Base64.getEncoder().encodeToString(this.hmacKey.getEncoded()));
        System.out.println("--- Fim Diagnóstico JWT (JwtEncoder) ---");
        // ✅ CORREÇÃO: Usa a 'hmacKey' já resolvida para garantir consistência
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.hmacKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // ✅ ADICIONADO: Log para verificar a chave sendo usada pelo decoder
        System.out.println("--- Diagnóstico JWT (JwtDecoder) ---");
        System.out.println("JwtDecoder usando SecretKey (Base64 encoded): " + Base64.getEncoder().encodeToString(this.hmacKey.getEncoded()));
        System.out.println("--- Fim Diagnóstico JWT (JwtDecoder) ---");
        // Usa a mesma chave no decoder
        return NimbusJwtDecoder.withSecretKey(hmacKey).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
