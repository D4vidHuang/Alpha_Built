package com.ecadi.alphabuiltbackend.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Lob;
import jakarta.persistence.Convert;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an account in the system.
 * It uses Lombok's @Data annotation to automatically generate
 * getters, setters, equals(), hashCode(), and toString() methods.
 */
@Entity
@Data
@Component
@Table(name = "accounts")
@NoArgsConstructor
public class Account {

    /**
     * The unique identifier of the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    /**
     * The username of the account.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * The password of the account.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The email address associated with the account.
     */
    @Column(name = "email", nullable = false)
    private String email;

    // <Project id, user id in that project>
    @Lob
    @Column(name = "projects")
    @Convert(converter = ProjectListConverter.class)
    private List<ProjectIdAndUserIdPair> userRolesInProjects;

    /**
     * Constructs an Account with the specified username, password, and email.
     *
     * @param username The username of the account.
     * @param password The password of the account.
     * @param email The email address associated with the account.
     */
    public Account(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRolesInProjects = new ArrayList<>();
    }

    public List<ProjectIdAndUserIdPair> getUserRolesInProjects() {
        return userRolesInProjects;
    }

    public void setUserRolesInProjects(List<ProjectIdAndUserIdPair> projects) {
        this.userRolesInProjects = projects;
    }
}
