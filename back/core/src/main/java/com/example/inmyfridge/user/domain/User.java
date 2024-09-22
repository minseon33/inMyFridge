package com.example.inmyfridge.user.domain;

import com.example.inmyfridge.common.abstractEntity.UserBaseEntity;
import com.example.inmyfridge.user.enums.UserRole;
import com.example.inmyfridge.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Getter
@Entity(name = "USERS")
@SQLDelete(sql = "UPDATE USERS SET userStatus = deletedUser WHERE id=?") //todo @SQLDelete 동작하는지 테스트 코드 작성하여 테스트해보기 (24.8.18)
@Where(clause = "userStatus=activeUser") //todo @Where 동작하는지 테스트 코드 작성하여 테스트해보기 (24.8.18)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends UserBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String nickName;

    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

}
