package com.example.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.platform.entity.Message;
import com.example.platform.entity.Channel;
import java.util.List;
import java.time.LocalDateTime;

public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findByChannelAndCreatedAtAfter(Channel channel,LocalDateTime createdAt);
    List<Message> findByChannelOrderByCreatedAtAsc(Channel channel);   
    Page<Message> findByChannel(Channel channel,Pageable pageable);
}
