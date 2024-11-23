package ru.netology.cloudstoragediplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstoragediplom.entity.FileInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, Long> {
    void deleteByName(String fileName);

    Optional<FileInfo> findByName(String fileName); //если не найдено, чтобы не возвращать null возвращаем пустой Optional

    @Query(nativeQuery = true, value = "SELECT * FROM cloud_storage.file_info LIMIT :size")
    List<FileInfo> findAllWithLimit(@Param("size") Integer limit);
}
