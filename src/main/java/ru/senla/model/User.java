package ru.senla.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "users_id_seq",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @Column(name = "id", updatable = false)
    //@GeneratedValue(strategy = GenerationType.IDENTITY) // For tests
    private Long id;

    @Column(name = "email", unique = true)
    private String username;

    private String password;

    private String name;

    private String surname;

    private String phone;

    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "activity")
    private boolean isActive;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user")
    private List<Post> posts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>(role.getAuthorities());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
