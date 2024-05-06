package com.davidogbodu.usermanagement.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(  name = "user_email_unique", columnNames = "email"),
        @UniqueConstraint(  name = "user_phone_unique", columnNames = "phone")
})
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    ZonedDateTime createdAt;

    @Column(nullable = false)
    private Boolean nonLocked;

    @Column(nullable = false)
    private Boolean enabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users that = (Users) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
