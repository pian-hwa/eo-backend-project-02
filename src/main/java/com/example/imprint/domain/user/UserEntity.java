package com.example.imprint.domain.user;


import com.example.imprint.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 15)
    private String nickname;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private UserRole role =  UserRole.USER;

    // PENDING, ACTIVE, BANNED
    @Builder.Default
    private String status = "PENDING";

    // 비즈니스 로직
    public void activate() {
        this.status = "ACTIVE";
    }
    public void changeRole(UserRole newRole) {
        this.role = newRole;
    }
}