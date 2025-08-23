package br.com.carro.controllers;
import br.com.carro.entities.Login.LoginRequest;
import br.com.carro.entities.Login.LoginResponse;
import br.com.carro.services.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.senha());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

        String token = tokenService.gerarToken(authentication);

        return new LoginResponse(token, 36000L); // Retorna o token e o tempo de expiração
    }
}
