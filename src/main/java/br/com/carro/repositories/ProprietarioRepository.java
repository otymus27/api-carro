package br.com.carro.repositories;

import br.com.carro.entities.Proprietario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByCpf(String cpf);
}
