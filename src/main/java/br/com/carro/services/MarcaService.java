package br.com.carro.services;

import br.com.carro.entities.Carro;
import br.com.carro.entities.Marca;
import br.com.carro.repositories.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcaService {
    @Autowired
    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public List<Marca> listar() {
        return marcaRepository.findAll();
    }

    public String cadastrar(Marca marca) {
        marcaRepository.save(marca);
        return "Marca cadastrada com sucesso!";
    }

    public String excluir(Long id) {
        marcaRepository.deleteById(id);
        return "Marca excluída com sucesso!";
    }


    // Atualizar um carro
    public String atualizar(Long id, Marca marca) throws Exception {
        marca.setId(id);
        this.marcaRepository.save(marca);
        return "Atualização feita com sucesso!";
    }

    public Marca buscarPorId(Long id) {
        return marcaRepository.findById(id).orElse(null);
    }

}
