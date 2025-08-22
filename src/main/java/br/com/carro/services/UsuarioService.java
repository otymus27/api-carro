package br.com.carro.services;

import br.com.carro.entities.Role.Role;
import br.com.carro.entities.Role.RoleDto;
import br.com.carro.entities.Usuario.Usuario;
import br.com.carro.entities.Usuario.UsuarioCadastroDto;
import br.com.carro.entities.Usuario.UsuarioDto;
import br.com.carro.repositories.RoleRepository;
import br.com.carro.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDto cadastrar(UsuarioCadastroDto dto) {
        // Buscar e validar roles
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.roleIds()));
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma role válida fornecida.");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRoles(roles);

        usuarioRepository.save(usuario);

        // Converter para DTO de resposta
        return new UsuarioDto(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getRoles().stream()
                        .map(role -> new RoleDto(role.getId(), role.getNome()))
                        .collect(Collectors.toSet())
        );
    }

    public List<UsuarioDto> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(usuario -> {
            Set<RoleDto> rolesDto = usuario.getRoles().stream()
                    .map(role -> new RoleDto(role.getId(), role.getNome()))
                    .collect(Collectors.toSet());

            return new UsuarioDto(
                    usuario.getId(),
                    usuario.getLogin(),
                    rolesDto
            );
        }).collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            Set<RoleDto> rolesDto = usuario.getRoles().stream()
                    .map(role -> new RoleDto(role.getId(), role.getNome()))
                    .collect(Collectors.toSet());

            return new UsuarioDto(
                    usuario.getId(),
                    usuario.getLogin(),
                    rolesDto
            );
        }
        return null;
    }

    public UsuarioDto atualizar(Long id, UsuarioCadastroDto dto) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            usuario.setLogin(dto.login());

            if (dto.senha() != null && !dto.senha().isBlank()) {
                String senhaCriptografada = passwordEncoder.encode(dto.senha());
                usuario.setSenha(senhaCriptografada);
            }

            // Atualiza as roles
            if (dto.roleIds() != null) {
                Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.roleIds()));
                usuario.setRoles(roles);
            }

            usuarioRepository.save(usuario);

            // Retorna DTO com roles atualizadas
            Set<RoleDto> rolesDto = usuario.getRoles().stream()
                    .map(role -> new RoleDto(role.getId(), role.getNome()))
                    .collect(Collectors.toSet());

            return new UsuarioDto(usuario.getId(), usuario.getLogin(), rolesDto);
        }

        return null;
    }

    public boolean excluir(Long id) {
        if (id == 1L) {
            return false; // Não permite exclusão do admin principal
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuarioRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public UsuarioDto buscarUsuarioLogado() {
        // Obtém o login (username) do usuário autenticado no contexto de segurança
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Usuario> optionalUsuario = usuarioRepository.findByLogin(login);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            Set<RoleDto> rolesDto = usuario.getRoles().stream()
                    .map(role -> new RoleDto(role.getId(), role.getNome()))
                    .collect(Collectors.toSet());

            return new UsuarioDto(
                    usuario.getId(),
                    usuario.getLogin(),
                    rolesDto
            );
        }

        return null;
    }

    public List<UsuarioDto> buscarPorNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.findByLoginContaining(nome);
        if (!usuarios.isEmpty()) {
            return usuarios.stream()
                    .map(usuario -> {
                        // Convertendo roles para DTOs, supondo que tenha RoleDto com id e nome
                        Set<RoleDto> rolesDto = usuario.getRoles().stream()
                                .map(role -> new RoleDto(role.getId(), role.getNome()))
                                .collect(Collectors.toSet());

                        return new UsuarioDto(
                                usuario.getId(),
                                usuario.getLogin(),
                                rolesDto
                        );
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }



    private UsuarioDto toDto(Usuario usuario) {
        Set<RoleDto> roles = usuario.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getNome()))
                .collect(Collectors.toSet());

        return new UsuarioDto(usuario.getId(), usuario.getLogin(), roles);
    }

    public List<UsuarioDto> listarPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioRepository.findAll(pageable)
                .stream()
                .map(u -> {
                    Set<RoleDto> rolesDto = u.getRoles().stream()
                            .map(role -> new RoleDto(role.getId(), role.getNome()))
                            .collect(Collectors.toSet());

                    return new UsuarioDto(
                            u.getId(),
                            u.getLogin(),
                            rolesDto
                    );
                })
                .collect(Collectors.toList());
    }




}
