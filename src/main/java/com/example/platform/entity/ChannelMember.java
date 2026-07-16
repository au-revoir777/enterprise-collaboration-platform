package com.example.platform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channel_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id","channel_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="added_by")
    private User addedBy;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean active=true;

    @PrePersist
    void prePersist(){
        joinedAt=LocalDateTime.now();
    }
}
