package com.style.approval.user.entity;

import com.style.approval.auth.dto.SignupDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키
    @Column(length = 30, unique = true)
    private String username; // 사용자 아이디
    @Column(length = 255)
    private String password; //password
    @Column(length = 50)
    private String email; //email
    @Column(length = 50)
    private String role;

    public UserEntity(SignupDto.Request request) {
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.username = request.getUsername().toLowerCase();
        this.role = request.getRole();
    }

    public List<String> getRole(){
        return Arrays.asList(role.split(","));
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
