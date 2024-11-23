package ru.netology.cloudstoragediplom.units;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.cloudstoragediplom.dto.file.FileDto;
import ru.netology.cloudstoragediplom.dto.file.FileItemDto;
import ru.netology.cloudstoragediplom.dto.file.PutFileRequest;
import ru.netology.cloudstoragediplom.entity.FileInfo;
import ru.netology.cloudstoragediplom.exeption.FileNotFoundException;
import ru.netology.cloudstoragediplom.repository.FileRepository;
import ru.netology.cloudstoragediplom.service.impl.FileService;
import ru.netology.cloudstoragediplom.units.config.MockConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FileService.class, MockConfig.class})
public class FileServiceTest {
    private static final int LIMIT = 5;
    private static final String TEST_FILE_NAME = "1.txt";
    private static final long SIZE = 1000L;
    public static final String TEST_HASH = "test_hash";
    private List<FileInfo> fileInfos = new ArrayList<>();

    @Autowired
    private FileService fileService;
    @Autowired
    private FileRepository fileRepository;

    @Test
    public void get_list_test() {
        FileInfo info1 = new FileInfo();
        info1.setId(1L);
        info1.setName("1.txt");
        info1.setSize(1000L);
        FileInfo info2 = new FileInfo();
        info1.setId(2L);
        info1.setName("2.txt");
        info1.setSize(2000L);
        FileInfo info3 = new FileInfo();
        info1.setId(3L);
        info1.setName("3.txt");
        info1.setSize(3000L);

        fileInfos.add(info1);
        fileInfos.add(info2);
        fileInfos.add(info3);

        when(fileRepository.findAllWithLimit(LIMIT)).thenReturn(fileInfos);

        List<FileItemDto> fileItemDtos = fileService.getList(LIMIT);

        assertThat(fileItemDtos.size()).isLessThanOrEqualTo(LIMIT);
    }

    @Test
    public void put_test() {
        when(fileRepository.findByName(TEST_FILE_NAME)).thenReturn(Optional.empty());
        Assertions.assertThrows(FileNotFoundException.class,
                () -> fileService.put(TEST_FILE_NAME, new PutFileRequest()),
                "Файл не найден");
    }

    @Test
    public void get_test() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1L);
        fileInfo.setName(TEST_FILE_NAME);
        fileInfo.setSize(SIZE);
        fileInfo.setHash(TEST_HASH);
        when(fileRepository.findByName(TEST_FILE_NAME)).thenReturn(Optional.of(fileInfo));

        FileDto fileDto = fileService.get(TEST_FILE_NAME);

        assertThat(fileDto.getHash()).isEqualTo(TEST_HASH);
    }
}
