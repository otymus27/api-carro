package br.com.carro.services;

import br.com.carro.entities.Carro;
import br.com.carro.repositories.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarroService {
    @Autowired
    private final CarroRepository carroRepository;

    public CarroService(CarroRepository carroRepository) {
        this.carroRepository = carroRepository;
    }

    // Cadastrar um novo carro diretamente com a entidade sem DTO's
    public String cadastrar(Carro carro) {
        // Salva o carro no banco de dados
        this.carroRepository.save(carro);
        return "Cadastro feito com sucesso!";
    }

    // Listar todos os carros
    public List<Carro> listar() {
        List<Carro> lista = carroRepository.findAll();
        return lista;
    }

    // Buscar carro por ID
    public Carro buscarPorId(Integer id) throws Exception {
        Carro carro = this.carroRepository.findById(id).get();
        return carro;
    }

    // Atualizar um carro
    public String atualizar(Integer id, Carro carro) throws Exception {
        carro.setId(id);
        this.carroRepository.save(carro);
        return "Atualização feita com sucesso!";
    }

    // Excluir um carro
    public String excluir(Integer id) throws Exception {
        this.carroRepository.deleteById(id);
        return "Exclusão feita com sucesso!";
    }

}
