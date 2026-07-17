package com.example.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.platform.entity.Workspace;
import com.example.platform.entity.User;
import java.util.List;


public interface WorkspaceRepository extends JpaRepository<Workspace,Long>{
    List<Workspace> findByOwner(User owner);
}
