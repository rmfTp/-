package com.example.demo.file.conrollers;

import com.example.demo.file.entities.FileInfo;
import com.example.demo.file.services.FileUploadService;
import com.example.demo.member.test.libs.MockMember;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mockMvc;
    private MockMultipartFile file1;
    private MockMultipartFile file2;

    @BeforeEach
    void init() throws Exception {
        FileInputStream fis = new FileInputStream("C:/data/1.png");
        FileInputStream fis2 = new FileInputStream("C:/data/2.jpg");
        file1 = new MockMultipartFile("file", "image1.png", MediaType.IMAGE_PNG_VALUE, fis);
        file2 = new MockMultipartFile("file", "image2.jpg", MediaType.IMAGE_PNG_VALUE, fis2);
    }

    @Test
    @MockMember
    @DisplayName("파일 업로드 테스트")
    void fileUploadTest() throws Exception {
        String body = mockMvc.perform(multipart("/ap1/v1/file/upload")
                .file(file1)
                .file(file2)
                .param("gid", "testGid")
                .param("location", "testLocation"))
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(body);
        List<FileInfo> items = om.readValue(body, new TypeReference<>() {});
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(i -> StringUtils.hasText(i.getCreatedBy())));
    }
}
