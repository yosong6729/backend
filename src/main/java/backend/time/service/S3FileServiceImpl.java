package backend.time.service;

import backend.time.dto.FileUploadDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileServiceImpl implements S3FileSerivce{
    // AmazonS3 주입받기
    private final AmazonS3 amazonS3;

    // S3 bucket 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3 base URL
    @Value("${cloud.aws.s3.bucket.url}")
    private String baseUrl;

    // MultipartFile 과 transcation, roomId 를 전달받는다.
    // 이때 transcation 는 파일 이름 중복 방지를 위한 UUID 를 의미한다.
    @Override
    public FileUploadDto uploadFile(MultipartFile file, String transaction, String roomId) {
        try{

            String filename = file.getOriginalFilename(); // 파일원본 이름
            String key = roomId+"/"+transaction+"/"+filename; // S3 파일 경로

            // 매개변수로 넘어온 multipartFile 을 File 객체로 변환 시켜서 저장하기 위한 메서드
            File convertedFile = convertMultipartFileToFile(file, transaction + filename);

            // 아마존 S3 에 파일 업로드를 위해 사용하는 TransferManagerBuilder
            TransferManager transferManager = TransferManagerBuilder
                    .standard()
                    .withS3Client(amazonS3)
                    .build();

            // bucket 에 key 와 converedFile 을 이용해서 파일 업로드
            Upload upload = transferManager.upload(bucket, key, convertedFile);
            upload.waitForUploadResult();

            // 변환된 File 객체 삭제
            removeFile(convertedFile);

            // uploadDTO 객체 빌드
            FileUploadDto uploadReq = FileUploadDto.builder()
                    .transaction(transaction)
                    .chatRoom(roomId)
                    .originFileName(filename)
                    .fileDir(key)
                    .s3DataUrl(baseUrl+"/"+key)
                    .build();

            // uploadDTO 객체 리턴
            return uploadReq;

        } catch (Exception e) {
            log.error("fileUploadException {}", e.getMessage());
            return null;
        }
    }

}
