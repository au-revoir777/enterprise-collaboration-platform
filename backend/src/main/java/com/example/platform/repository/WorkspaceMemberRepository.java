package com.example.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.platform.entity.WorkspaceMember;
import com.example.platform.entity.WorkspaceRole;
import com.example.platform.entity.Workspace;
import com.example.platform.entity.User;


import java.util.List;
import java.util.Optional;


public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember,Long> {
    List<WorkspaceMember> findByWorkspace(Workspace workspace); // fetch members in a workspace
    List<WorkspaceMember> findByWorkspaceAndRole(Workspace workspace, WorkspaceRole role); //fetch members with particular role
    //from a particular workspace
    boolean existsByWorkspaceAndUser(Workspace workspace,User user); //check membership
    Optional<WorkspaceMember> findByWorkspaceAndUser(Workspace workspace,User user); // get a specific membership
    
}
