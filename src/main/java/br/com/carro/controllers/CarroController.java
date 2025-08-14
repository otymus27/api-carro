package br.com.carro.controllers;

import br.com.carro.entities.Carro;
import br.com.carro.services.CarroService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/carro")
public class CarroController {

    public record Mensagem(String mensagem) {}

    @Autowired
    private final CarroService carroService;

    public CarroController(CarroService carroService) {
        this.carroService = carroService;
    }

    @GetMapping
    public ResponseEntity<List<Carro>> listar() {
        try {
            List<Carro> lista = this.carroService.listar();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Buscar carro por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            // Chama o service que retorna o carro ou lança exceção se não existir
            Carro carro = carroService.buscarPorId(id);
            return new ResponseEntity<>(carro, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(   )
    public ResponseEntity<String> cadastrar(@RequestBody Carro carro) {
        try {
            String mensagem = this.carroService.cadastrar(carro);
            return new ResponseEntity<>(mensagem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao cadastrar registro: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Atualizar um carro
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Integer id, @RequestBody Carro carro) {
        try {
            // Atualiza o carro usando o service; se não existir, lança exceção
            String mensagem = this.carroService.atualizar(id, carro);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao atualizar registro: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Excluir um carro
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Integer id) {
        try {
            // Chama o service que já verifica se o carro existe e lança exceção se não existir
            String mensagem = this.carroService.excluir(id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao excluir registro: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
