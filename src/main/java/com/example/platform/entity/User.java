package com.example.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.*;

@Entity //hibernate creates table for users, this is a jpa entity, class->db
@Table(name = "users") //this indicates the name of the table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //from lombok
public class User {

    @Id //this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //tells PostgreSQL to auto-increment the id
    private Long id;

    @Column(nullable = false, unique = true, length = 30) //configures the database column
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING) //for enums
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;


    // any default value (which can change later) that 
    // you set yourself must be annotated with Builder.Default 
    // to take effect
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // relationship was added after defining both
    // the entities
    @OneToMany(mappedBy = "owner")
    private Set<Workspace> ownedWorkspaces;

    // jpa lifecycle callbacks, execute code automatically at specific points
    // in an entity's lifecycle

    // before you perform the insert/update that you're already about
    // to do, run this method
    // it is like preprocessing before being added/updated to the db

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
