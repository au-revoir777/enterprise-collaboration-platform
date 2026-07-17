package com.example.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.platform.entity.ChannelMember;
import com.example.platform.entity.Channel;
import com.example.platform.entity.User;
import java.util.List;
import java.util.Optional;


public interface ChannelMemberRepository extends JpaRepository<ChannelMember,Long> {
    List<ChannelMember> findByChannel(Channel channel);
    boolean existsByChannelAndUser(Channel channel,User user);//check whether user belongs to a private channel
    Optional<ChannelMember> findByChannelAndUser(Channel channel,User user);//specific membership
    List<ChannelMember> findByUser(User user);//fetch all private channels user belongs to
}
