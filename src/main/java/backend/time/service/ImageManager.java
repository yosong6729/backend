package backend.time.service;

import backend.time.model.ChatImage;
import backend.time.model.ChatMessage;
import backend.time.model.Objection.Objection;
import backend.time.model.Objection.ObjectionImage;
import backend.time.repository.ChatImageRepository;
import backend.time.repository.ImageRepository;
import backend.time.model.board.Board;
import backend.time.model.board.Image;
import backend.time.repository.ObjectionImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageManager {

    final private ImageRepository imageRepository;

    final private ObjectionImageRepository objectionImageRepository;

    private final ChatImageRepository chatImageRepository;

    @Value("${file.dir}")
    private String storePath; //파일 저장할 경로

    //확장자 추출
    private String extractExt(String uploadFileName) {
        int pos = uploadFileName.lastIndexOf(".");
        return uploadFileName.substring(pos + 1);
    }

    //저장되는 파일 이름 결정 ( 같은 사진을 올리면 중복 오류
    public String createStoreFileName(String uploadFileName) {
        String ext = extractExt(uploadFileName); //확장자 가져오기
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //저장되는 경로 반환
    public String getImagePath(String storedFileName, String ext) {
        String imagePath = storePath + ext + "/" + storedFileName;
        return imagePath;
    }

    //이미지 저장
    public Image saveImage(MultipartFile multipartFile, Board board) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        String uploadFileName = multipartFile.getOriginalFilename();
        String storedFileName = createStoreFileName(uploadFileName);
        String ext = extractExt(uploadFileName);

        multipartFile.transferTo(new File(getImagePath(storedFileName, ext)));

        Image savedImage = Image.builder()
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .board(board)
                .build();

        return imageRepository.save(savedImage);
    }

    //전체 이미지 저장
    public List<Image> saveImages(List<MultipartFile> multipartFiles, Board board) throws IOException {
        List<Image> imageList = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                imageList.add(saveImage(multipartFile, board));
            }
        }
        return imageList;
    }

    @Transactional
    public ObjectionImage saveObjectionImage(MultipartFile multipartFile, Objection objection) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        String uploadFileName = multipartFile.getOriginalFilename();
        String storedFileName = createStoreFileName(uploadFileName);
        String ext = extractExt(uploadFileName);
        System.out.println("ext "+ext);
        multipartFile.transferTo(new File(getImagePath(storedFileName,ext)));
        ObjectionImage objectionImage = ObjectionImage.builder()
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .objection(objection)
                .build();
        return objectionImageRepository.save(objectionImage);
    }
    public List<ObjectionImage> saveObjectionImages(List<MultipartFile> multipartFiles, Objection objection) throws IOException {
        List<ObjectionImage> objectionImageList = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                objectionImageList.add(saveObjectionImage(multipartFile, objection));
            }
        }
        return objectionImageList;
    }


    public ChatImage saveChatImage(MultipartFile multipartFile, ChatMessage chatMessage) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }

        String uploadFileName = multipartFile.getOriginalFilename();
        String storedFileName = createStoreFileName(uploadFileName);
        String ext = extractExt(uploadFileName);

        multipartFile.transferTo(new File(getImagePath(storedFileName,ext)));

        ChatImage chatImage = ChatImage.builder()
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .chatMessage(chatMessage)
                .build();
        return chatImageRepository.save(chatImage);
    }

    @Transactional
    public List<ChatImage> saveChatImages(List<MultipartFile> multipartFiles, ChatMessage chatMessage) throws IOException {
        List<ChatImage> chatMessageList = new ArrayList<>();
        for(MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                chatMessageList.add(saveChatImage(multipartFile, chatMessage));
            }
        }
        return chatMessageList;
    }





}
