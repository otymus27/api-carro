package br.com.carro.repositories;

import br.com.carro.entities.Proprietario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByCpf(String cpf);

    @Override
    Page<Proprietario> findAll(Pageable pageable);

    Page<Proprietario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Proprietario> findByCpfContaining(String cpf, Pageable pageable);

    Page<Proprietario> findByNomeContainingIgnoreCaseOrCpfContainingIgnoreCase(String nome, String cpf, Pageable pageable);


}
