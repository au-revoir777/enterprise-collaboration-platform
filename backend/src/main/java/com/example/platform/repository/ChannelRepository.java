package com.example.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.platform.entity.Channel;
import com.example.platform.entity.Workspace;
import com.example.platform.entity.ChannelType;
import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByWorkspace(Workspace workspace);

    Optional<Channel> findByNameAndWorkspace(String name, Workspace workspace);

    List<Channel> findByTypeAndWorkspace(ChannelType type, Workspace workspace);

    boolean existsByNameAndWorkspace(String name, Workspace workspace);
}
