package br.com.carro.services;

import br.com.carro.entities.Login.LoginRequest;
import br.com.carro.entities.Login.LoginResponse;
import br.com.carro.entities.Role.Role;
import br.com.carro.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticateAndGenerateToken(LoginRequest loginRequest) {
        var user = usuarioRepository.findByLogin(loginRequest.login())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos"));

        if (!user.isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        var now = Instant.now();
        var expiresIn = 3600L;

        var scopes = user.getRoles().stream()
                .map(Role::getNome)
                .collect(Collectors.joining(" "));

        var claimsBuilder = JwtClaimsSet.builder()
                .issuer("meuBackend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(user.getLogin());

        if (!scopes.isEmpty()) {
            claimsBuilder.claim("scope", scopes);
            claimsBuilder.claim("roles", user.getRoles().stream().map(Role::getNome).toList());
        }

        var claims = claimsBuilder.build();
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = jwt.getSubject();
        List<String> roles = jwt.getClaim("roles");

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails userDetails = new User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }
}
