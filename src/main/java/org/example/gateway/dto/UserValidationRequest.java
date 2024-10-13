package org.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationRequest {

    private String username;
    private String password;

    private Set<Role> roles;

    public enum Role implements GrantedAuthority {
        ROLE_ADMIN, ROLE_USER;

        @Override
        public String getAuthority() {
            return this.name();
        }
    }

}
