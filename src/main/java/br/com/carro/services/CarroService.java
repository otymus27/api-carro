package br.com.carro.services;

import br.com.carro.entities.Carro;
import br.com.carro.entities.CarroDTO;
import br.com.carro.entities.Marca;
import br.com.carro.entities.Proprietario;
import br.com.carro.repositories.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Carro buscarPorId(Long id) throws Exception {
        Carro carro = this.carroRepository.findById(id).get();
        return carro;
    }

    // Atualizar um carro
    public String atualizar(Long id, Carro carro) throws Exception {
        carro.setId(id);
        this.carroRepository.save(carro);
        return "Atualização feita com sucesso!";
    }

    // Excluir um carro
    public String excluir(Long id) throws Exception {
        this.carroRepository.deleteById(id);
        return "Exclusão feita com sucesso!";
    }


    // Excluir carro
    public boolean deletar(Long id) {
        Optional<Carro> optionalCarro = carroRepository.findById(id);
        if (optionalCarro.isPresent()) {
            carroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<CarroDTO> listarDto() {
        return carroRepository.findAll().stream()
                .map(c -> new CarroDTO(
                        c.getId(),
                        c.getModelo(),
                        c.getCor(),
                        c.getAno(),
                        c.getMarca(),          // envia objeto completo
                        c.getProprietarios()   // envia lista completa
                ))
                .collect(Collectors.toList());
    }

    // Listar todas as marcas com paginação
    public Page<Carro> listar(Pageable pageable) {
        return carroRepository.findAll(pageable);
    }


    // Listagem com paginação, filtro e ordenação
    public Page<CarroDTO> listar(String modelo, String cor, Integer ano, String marca, Pageable pageable) {
        Page<Carro> carros;

        if (modelo != null && !modelo.isEmpty()) {
            carros = carroRepository.findByModeloContainingIgnoreCase(modelo, pageable);
        } else if (cor != null && !cor.isEmpty()) {
            carros = carroRepository.findByCorContainingIgnoreCase(cor, pageable);
        } else if (ano != null) {
            carros = carroRepository.findByAno(ano, pageable);
        } else if (marca != null && !marca.isEmpty()) {
            carros = carroRepository.findByMarca_NomeContainingIgnoreCase(marca, pageable);
        } else {
            carros = carroRepository.findAll(pageable);
        }

        return carros.map(c -> new CarroDTO(
                c.getId(),
                c.getModelo(),
                c.getCor(),
                c.getAno(),
                c.getMarca(),
                c.getProprietarios()
        ));
    }



}
