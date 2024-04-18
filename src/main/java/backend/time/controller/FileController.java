package backend.time.controller;

import backend.time.dto.FileUploadDto;
import backend.time.service.S3FileSerivce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final S3FileSerivce fileService;

    // 프론트에서 /upload 로 MultipartFile 형태로 파일과 roomId 를 전달받는다.
    // 전달받은 file 를 uploadFile 메서드를 통해 업로드한다.
    @PostMapping("/upload")
    public FileUploadDto uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("roomId")String roomId){

        FileUploadDto fileReq = fileService.uploadFile(file, UUID.randomUUID().toString(), roomId);
        log.info("최종 upload Data {}", fileReq);

        // fileReq 객체 리턴
        return fileReq;
    }

}
