package br.com.carro.controllers;

import br.com.carro.entities.Marca;
import br.com.carro.entities.Proprietario;
import br.com.carro.services.ProprietarioService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proprietario")
@CrossOrigin("*")
public class ProprietarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProprietarioController.class);
    public record Mensagem(String mensagem) {}

    @Autowired
    private final ProprietarioService proprietarioService;

    public ProprietarioController(ProprietarioService proprietarioService) {
        this.proprietarioService = proprietarioService;
    }

    // Listar com paginação, filtro e ordenação
    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "id") String sortField, // 'id' ou 'nome'
            @RequestParam(defaultValue = "asc") String sortDir   // 'asc' ou 'desc'
            ) {

        // Cria Sort único baseado em campo e direção
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sortField);

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Proprietario> pageProprietarios;
        if (nome != null && !nome.isBlank()) {
            pageProprietarios = proprietarioService.buscarPorNome(nome, pageable);
        } else if (cpf != null && !cpf.isBlank()) {
            pageProprietarios = proprietarioService.buscarPorCpf(cpf, pageable);
        } else {
            pageProprietarios = proprietarioService.listar(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageProprietarios.getContent());
        response.put("page", pageProprietarios.getNumber() + 1);
        response.put("size", pageProprietarios.getSize());
        response.put("totalElements", pageProprietarios.getTotalElements());
        response.put("totalPages", pageProprietarios.getTotalPages());

        return ResponseEntity.ok(response);
    }


    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Proprietario proprietario = proprietarioService.buscarPorId(id);
            if (proprietario == null) {
                return new ResponseEntity<>("Proprietário não encontrado!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(proprietario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao buscar proprietário: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Cadastrar
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Proprietario proprietario) {
        try {
            Proprietario novoProprietario = proprietarioService.cadastrar(proprietario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProprietario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar proprietário: " + e.getMessage());
        }
    }

    // Excluir
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Mensagem> excluir(@PathVariable String id) {
        try {
            // Tenta converter o ID para Long
            Long idLong = Long.parseLong(id);

            logger.info("Tentando excluir registro com ID: {}", idLong);

            boolean excluido = proprietarioService.excluir(idLong);

            if (excluido) {
                return ResponseEntity.ok(new Mensagem("Registro excluído com sucesso -mensagem do controller do backend."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Mensagem("Registro com ID " + idLong + " não encontrado."));
            }

        } catch (NumberFormatException e) {
            // ID não é um número válido
            logger.warn("ID inválido fornecido: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Mensagem("ID inválido! Informe apenas números."));
        } catch (Exception e) {
            // Qualquer outra exceção inesperada
            logger.error("Erro ao tentar excluir registro com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensagem("Erro ao excluir registro: " + e.getMessage()));
        }
    }

    // Atualizar
    @PatchMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody Proprietario proprietario) {
        boolean atualizado = proprietarioService.atualizar(id, proprietario);
        if (atualizado) {
            return ResponseEntity.ok("Registro atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro com ID " + id + " não encontrado!");
        }
    }


}
