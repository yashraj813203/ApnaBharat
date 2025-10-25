package com.ab.repository;

import com.ab.entity.ApiIngestionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiIngestionLogRepository extends JpaRepository<ApiIngestionLog, Long> {

	List<ApiIngestionLog> findTop10BySourceOrderByStartedAtDesc(String source);

	ApiIngestionLog findTop1BySourceOrderByStartedAtDesc(String source);
}
