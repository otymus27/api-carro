package br.com.carro.controllers;

import br.com.carro.entities.Carro;
import br.com.carro.services.CarroService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
        List<Carro> lista = carroService.listar();
        return ResponseEntity.ok(lista);
    }

}
