package com.hotelier.model.repository;

import com.hotelier.model.entity.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStoreRepo extends JpaRepository<FileStore, Long> {
}
