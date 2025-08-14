package br.com.carro.repositories;

import br.com.carro.entities.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarroRepository extends JpaRepository<Carro,Long> {
}
