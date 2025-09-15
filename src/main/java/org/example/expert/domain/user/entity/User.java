package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String nickname;
    private String password;
    // 프로필 이미지 경로를 저장함
    private String profile;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String email, String nickname, String password, UserRole userRole) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.userRole = userRole;
        profile = null;
    }

    public User(String email, String nickname, String password, UserRole userRole, String profile) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.userRole = userRole;
        this.profile = profile;
    }

    private User(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeProfile(String profile) {
        this.profile = profile;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
