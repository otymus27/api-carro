package br.com.carro.controllers;

import br.com.carro.entities.Marca;
import br.com.carro.entities.Proprietario;
import br.com.carro.services.MarcaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marca")
@CrossOrigin("*")
public class MarcaController {

    private static final Logger logger = LoggerFactory.getLogger(CarroController.class);
    public record Mensagem(String mensagem) {}

    @Autowired
    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }


//    @GetMapping
//    public List<Marca> listar() {
//        return marcaService.listar();
//    }

//    @GetMapping
//    public ResponseEntity<Page<Marca>> listar(
//            @RequestParam(required = false) String filtro,
//            @PageableDefault(size = 5, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
//
//        Page<Marca> marcas = marcaService.listar(filtro, pageable);
//        return ResponseEntity.ok(marcas);
//    }


    // Listar marcas com paginação e ordenação
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMarcas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "id") String sortField, // 'id' ou 'nome'
            @RequestParam(defaultValue = "asc") String sortDir   // 'asc' ou 'desc'
    ) {
        // Cria Sort único baseado em campo e direção
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sortField);

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Marca> pageMarcas = (nome != null && !nome.isBlank())
                ? marcaService.findByNomeContainingIgnoreCase(nome, pageable)
                : marcaService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageMarcas.getContent());
        response.put("page", pageMarcas.getNumber());
        response.put("size", pageMarcas.getSize());
        response.put("totalElements", pageMarcas.getTotalElements());
        response.put("totalPages", pageMarcas.getTotalPages());

        return ResponseEntity.ok(response);
    }


    // Buscar carro por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            // Chama o service que retorna o carro ou lança exceção se não existir
            Marca marca = marcaService.buscarPorId(id);
            return new ResponseEntity<>(marca, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Cadastrar
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Marca marca) {
        try {
            Marca novaMarca = marcaService.cadastrar(marca);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaMarca);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar registro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        try {
            String msg = marcaService.excluir(id);
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao excluir marca: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Atualizar um carro
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody Marca marca) {
        try {
            // Atualiza o carro usando o service; se não existir, lança exceção
            String mensagem = this.marcaService.atualizar(id, marca);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao atualizar registro: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/paginado/")
    public ResponseEntity<Page<Marca>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Marca> marcas = marcaService.listarPaginado(page, size);
        return ResponseEntity.ok(marcas);
    }



}
