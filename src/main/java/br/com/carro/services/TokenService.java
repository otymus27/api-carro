package br.com.carro.services;

import br.com.carro.autenticacao.CustomTokenClaims; // ✅ Importe CustomTokenClaims
import br.com.carro.entities.Login.LoginRequest;
import br.com.carro.entities.Login.LoginResponse;
import br.com.carro.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;

    // Construtor sem o CustomTokenClaims
    public TokenService(
            JwtEncoder jwtEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse authenticateAndGenerateToken(LoginRequest loginRequest) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);

        var user = (UserDetails) authentication.getPrincipal();

        var now = Instant.now();
        var expiresIn = 3600L;

        // ✅ Coleta as permissões do usuário
        var scopes = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("meuBackend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(user.getUsername())
                .claim("scope", scopes) // ✅ Adiciona as permissões como a claim 'scope'
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
    }


}