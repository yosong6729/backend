package backend.time.service;

import backend.time.dto.ChatDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.repository.BoardRepository;
import backend.time.repository.ChatRepository;
import backend.time.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChattingServiceImpl implements ChattingService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final BoardRepository boardRepository;

    @Override
    public List<ChatRoom> findChatRoomByMember(String email) {
        return chatRoomRepository.findChatRoomByMember(email);
    }

    @Override
    public ChatRoom findChatRoomByBuyer(Long boardId, Long buyerId) {
        return chatRoomRepository.findByBoardIdAndBuyerId(boardId, buyerId)
                .orElseGet(()-> new ChatRoom());
    }

    @Override
    @Transactional
    public Long saveChat(ChatDto chatDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatDto.getRoomId()).get(); //chatDto의 room_id로 repository에 저장되어있는 chatRoom가져오기
        ChatMessage chat = chatDto.toEntity(chatRoom); //chatDto에 정보를 가지고 리포지토리에서 찾은 chatRoom을 같이 엔티티로 전환하여 chat반환
        chatRepository.save(chat); //DB에 chat저장(spring data jpa)
        return chat.getId();
    }

    @Override
    public List<ChatMessage> findChatList(Long roomId){
        return chatRepository.findByChatRoomId(roomId);
    }

    @Override
    @Transactional
    public ChatRoom findChatRoomByName(Member member, String roomName, Long boardId) {
        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByName(roomName); //UUID랜덤으로 생성한 방이름?으로 ChatRoom 가져옴
        Board board = boardRepository.findById(boardId).get(); //게시판 아이디로 게시판 가져옴
        if(!findChatRoom.isPresent()){ //만약 가져온 ChatRoom이 존재하지않으면 새로운 채팅방으로 인식해서 DB에 저장
            ChatRoom newChatRoom = ChatRoom.builder()
                    .roomName(roomName)
                    .buyer(member)
                    .board(board).build();
            chatRoomRepository.save(newChatRoom);
            return newChatRoom;
        }
        return findChatRoom.get(); //만약 이미 생성된방이 있으면 그방을 return
    }
}
