package com.example.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspaces")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(length=200)
    private String description;

    @Column(length=500)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private WorkspaceType type;

    @Builder.Default
    @Column(nullable=false)
    private boolean active=true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    //many workspaces under one owner
    @ManyToOne(fetch = FetchType.LAZY) // load the owner only when accessed
    @JoinColumn(name="owner_id",nullable=false)
    private User owner;

    @PrePersist
    void prePersist() {
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate(){
        updatedAt=LocalDateTime.now();
    }
}
