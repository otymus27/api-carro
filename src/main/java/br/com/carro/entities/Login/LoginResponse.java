package br.com.carro.entities.Login;

import java.time.LocalDate;

public record LoginResponse(String accessToken, Long expiresIn) {

}
