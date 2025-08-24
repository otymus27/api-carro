package br.com.carro.controllers;
import br.com.carro.entities.Login.LoginRequest;
import br.com.carro.entities.Login.LoginResponse;
import br.com.carro.services.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
// ✅ Adicione @CrossOrigin à classe ou ao método
// Isso sobrescreve a configuração global para este controlador, ou age como um fallback.
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {

        // ✅ Coloque um breakpoint aqui
        System.out.println("Tentativa de login para usuário: " + loginRequest.username());
        // ... Sua lógica de autenticação
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

        String token = tokenService.gerarToken(authentication);

        return new LoginResponse(token, 36000L); // Retorna o token e o tempo de expiração
    }
}
