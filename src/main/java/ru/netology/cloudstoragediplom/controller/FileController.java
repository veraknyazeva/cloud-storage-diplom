package ru.netology.cloudstoragediplom.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstoragediplom.dto.file.FileDto;
import ru.netology.cloudstoragediplom.dto.file.FileItemDto;
import ru.netology.cloudstoragediplom.dto.file.PutFileRequest;
import ru.netology.cloudstoragediplom.entity.FileInfo;
import ru.netology.cloudstoragediplom.exeption.FileNotReadableException;
import ru.netology.cloudstoragediplom.service.impl.FileService;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = {"http://localhost:*"})
public class FileController {
    private final FileService service;

    public FileController(FileService service) {
        this.service = service;
    }

    @PostMapping(value = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void saveFile(@RequestParam(name = "filename") String fileName,
                         @RequestHeader(name = "${service.props.required-header-name}") String authToken,
                         @RequestPart(name = "hash", required = false) String hash,
                         @RequestPart(name = "file") MultipartFile file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(fileName);
        fileInfo.setHash(hash);
        fileInfo.setSize(file.getSize());
        try {
            fileInfo.setFileContent(file.getBytes());
        } catch (Exception ex) {
            throw new FileNotReadableException("Невозможно вычитать контент файла");
        }
        service.save(fileInfo);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestParam(name = "filename") String fileName,
                           @RequestHeader(name = "${service.props.required-header-name}") String authToken) {
        service.delete(fileName);
    }

    @GetMapping("/file")
    public FileDto getFile(@RequestParam(name = "filename") String fileName,
                           @RequestHeader(name = "${service.props.required-header-name}") String authToken) {
        return service.get(fileName);
    }

    @PutMapping("/file")
    public void putFile(@RequestParam(name = "filename") String fileName,
                        @RequestHeader(name = "${service.props.required-header-name}") String authToken,
                        @RequestBody PutFileRequest putFileRequest) {
        service.put(fileName, putFileRequest);
    }

    @GetMapping("/list")
    public List<FileItemDto> getList(@RequestParam(name = "limit") Integer limit,
                                     @RequestHeader(name = "${service.props.required-header-name}") String authToken) {
        return service.getList(limit);
    }
}
