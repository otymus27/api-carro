package br.com.carro.entities.Usuario;

import br.com.carro.entities.Login.LoginRequest;
import br.com.carro.entities.Role.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "tb_usuarios")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements UserDetails {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String login;

    private String senha;

    @ManyToMany(fetch = FetchType.EAGER) // ✅ FetchType.EAGER para carregar as permissões imediatamente
    @JoinTable(
            name = "tb_usuarios_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles;

    // ✅ Métodos da interface UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Por padrão, retorne 'true' para indicar que a conta não está expirada.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Por padrão, retorne 'true' para indicar que a conta não está bloqueada.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Por padrão, retorne 'true' para indicar que as credenciais não estão expiradas.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Por padrão, retorne 'true' para indicar que a conta está habilitada.
        return true;
    }
}
