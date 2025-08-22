package br.com.carro.controllers;

import br.com.carro.entities.Usuario.UsuarioCadastroDto;
import br.com.carro.entities.Usuario.UsuarioDto;
import br.com.carro.exceptions.ErrorMessage;
import br.com.carro.services.UsuarioService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/usuario") // Padroniza o caminho base da sua API
public class UsuarioController {
    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    public record Mensagem(String mensagem) {}

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping()
    @Transactional
    // Apenas ADMIN pode cadastrar usuários
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Object> cadastrar(@RequestBody UsuarioCadastroDto dados) {
        logger.info("Cadastrando novo usuário: {}", dados.login());
        try {
            UsuarioDto resposta = usuarioService.cadastrar(dados);
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
        } catch (Exception e) {
            logger.error("Erro ao cadastrar usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar usuário.");
        }
    }

    @GetMapping
    @Transactional
    // Apenas ADMIN e GERENTE pode listar usuários
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_GERENTE')")
    public ResponseEntity<List<UsuarioDto>> listar() {
        List<UsuarioDto> usuarios = usuarioService.listar();
        return ResponseEntity.ok(usuarios);
    }


    @GetMapping("/{id}")
    @Transactional
    // Apenas ADMIN e GERENTE pode listar usuários por id
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_GERENTE')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        UsuarioDto usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Usuário com ID " + id + " não encontrado"));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    // Apenas ADMIN pode atualizar usuários
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody UsuarioCadastroDto dto) {
        logger.info("Atualizando usuário com ID: {}", id);
        try {
            UsuarioDto atualizado = usuarioService.atualizar(id, dto);
            if (atualizado != null) {
                return ResponseEntity.ok(atualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorMessage("Usuário com ID " + id + " não encontrado"));
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Erro ao atualizar usuário"));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    // Apenas ADMIN pode excluir usuários
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        logger.info("Tentativa de exclusão do usuário com ID: {}", id);

        if (id == 1L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("O usuário administrador principal (ID 1) não pode ser excluído."));
        }

        boolean excluido = usuarioService.excluir(id);
        if (excluido) {
            return ResponseEntity.ok(new Mensagem("Usuário excluído com sucesso."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensagem("Usuário com ID " + id + " não encontrado."));
        }
    }

    // Método para buscar o usuário logado
    @GetMapping("/logado")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUsuarioLogado() {
        UsuarioDto usuario = usuarioService.buscarUsuarioLogado();
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Usuário autenticado não encontrado"));
        }
    }

    @GetMapping("/paginado/")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_GERENTE')")
    public List<UsuarioDto> listarPaginado(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size) {
        return usuarioService.listarPaginado(page, size);
    }





}
