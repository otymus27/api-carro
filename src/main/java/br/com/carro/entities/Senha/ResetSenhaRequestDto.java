package br.com.carro.entities.Senha;

public record ResetSenhaRequestDto(
        Long id,
        String senhaProvisoria,
        String novaSenha
) {
}