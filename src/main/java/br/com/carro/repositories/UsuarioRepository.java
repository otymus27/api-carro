package br.com.carro.repositories;

import br.com.carro.entities.Usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByUsernameContaining(String username);


    @Override
    Page<Usuario> findAll(Pageable pageable);

    // ✅ Esta query é CRUCIAL para carregar as roles junto com o usuário
    //@Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<Usuario> findByUsername(String login);
}
