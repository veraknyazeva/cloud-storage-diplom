package ru.netology.cloudstoragediplom.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.netology.cloudstoragediplom.dto.file.FileDto;
import ru.netology.cloudstoragediplom.dto.file.FileItemDto;
import ru.netology.cloudstoragediplom.dto.file.PutFileRequest;
import ru.netology.cloudstoragediplom.entity.FileInfo;
import ru.netology.cloudstoragediplom.exeption.FileConstraintException;
import ru.netology.cloudstoragediplom.exeption.FileNotFoundException;
import ru.netology.cloudstoragediplom.repository.FileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileRepository repository;

    public FileService(FileRepository repository) {
        this.repository = repository;
    }

    public void save(FileInfo fileInfo) {
        try {
            repository.save(fileInfo);
        } catch (Exception ex) {
            throw new FileConstraintException("Файл с таким именем уже существует");
        }
    }

    @Transactional
    public void delete(String fileName) {
        repository.deleteByName(fileName);
    }

    public FileDto get(String fileName) {
        Optional<FileInfo> fileInfoOptional = repository.findByName(fileName);

        if (fileInfoOptional.isPresent()) {
            FileInfo fileInfo = fileInfoOptional.get();
            FileDto dto = new FileDto();
            dto.setFile(fileInfo.getFileContent());
            dto.setHash(fileInfo.getHash());
            return dto;
        } else {
            throw new FileNotFoundException("Файл не найден");
        }
    }

    @Transactional
    public void put(String fileName, PutFileRequest putFileRequest) {
        Optional<FileInfo> fileInfoOptional = repository.findByName(fileName);
        if (fileInfoOptional.isPresent()) {
            FileInfo fileInfo = fileInfoOptional.get();
            fileInfo.setName(putFileRequest.getFilename());
            save(fileInfo);
        } else {
            throw new FileNotFoundException("Файл не найден");
        }
    }

    public List<FileItemDto> getList(Integer limit) {
        List<FileItemDto> fileItemDto = new ArrayList<>();
        Page<FileInfo> pageLimit = repository.findAll(Pageable.ofSize(limit));
        List<FileInfo> fileInfoList = pageLimit.toList();
        if (!fileInfoList.isEmpty()) {
            for (FileInfo fileInfo : fileInfoList) {
                FileItemDto dto = new FileItemDto();
                dto.setFilename(fileInfo.getName());
                dto.setSize(fileInfo.getSize());
                fileItemDto.add(dto);
            }
        }

        return fileItemDto;
    }
}
