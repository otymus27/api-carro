package br.com.carro.services;

import br.com.carro.entities.Marca;
import br.com.carro.repositories.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Marca cadastrar(Marca marca) {
        // Salvar e retornar o objeto criado
        return marcaRepository.save(marca);
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

    public Page<Marca> listarPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marcaRepository.findAll(pageable);
    }

    // Para busca com filtro por nome (case insensitive)
    public Page<Marca> listar(String filtro, Pageable pageable) {
        if (filtro != null && !filtro.isEmpty()) {
            return marcaRepository.findByNomeContainingIgnoreCase(filtro, pageable);
        } else {
            return marcaRepository.findAll(pageable);
        }
    }

    // Listar todas as marcas com paginação
    public Page<Marca> findAll(Pageable pageable) {
        return marcaRepository.findAll(pageable);
    }

    // Listar marcas filtrando por nome (com paginação)
    public Page<Marca> findByNomeContainingIgnoreCase(String nome, Pageable pageable) {
        return marcaRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

}
