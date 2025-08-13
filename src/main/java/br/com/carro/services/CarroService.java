package br.com.carro.services;

import br.com.carro.entities.Carro;
import br.com.carro.repositories.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarroService {
    @Autowired
    private final CarroRepository carroRepository;

    public CarroService(CarroRepository carroRepository) {
        this.carroRepository = carroRepository;
    }

    // Listar todos os carros
    public List<Carro> listar() {
        return carroRepository.findAll();
    }

}
