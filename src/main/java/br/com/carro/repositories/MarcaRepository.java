package br.com.carro.repositories;

import br.com.carro.entities.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    @Override
    Page<Marca> findAll(Pageable pageable);
}
