package br.com.carro.repositories;

import br.com.carro.entities.Carro;
import br.com.carro.entities.Marca;
import br.com.carro.entities.Proprietario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarroRepository extends JpaRepository<Carro,Long> {

    @Override
    Page<Carro> findAll(Pageable pageable);

    Page<Carro> findByModeloContainingIgnoreCase(String modelo, Pageable pageable);

    Page<Carro> findByCorContainingIgnoreCase(String cor, Pageable pageable);

    Page<Carro> findByAno(Integer ano, Pageable pageable);

    Page<Carro> findByMarca_NomeContainingIgnoreCase(String nomeMarca, Pageable pageable);

    Page<Carro> findByModeloContainingIgnoreCaseAndMarca_NomeContainingIgnoreCase(
            String modelo, String nomeMarca, Pageable pageable
    );

}
