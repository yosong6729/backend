# 틈새시장 Spring

![Cover (8)](https://github.com/user-attachments/assets/02830004-148b-4140-90cc-f08185a60e84)

## 1. 아이디어 소개

---

> **당신의 틈새시간을 활용해보세요.**

틈새시장은 사용자들이 **서로의 시간을 돈으로 거래**할 수 있게 하는 플랫폼입니다
자투리 시간을 효율적으로 활용하여 시간 관리의 효율성을 높일 수 있습니다. 
사용자는 카테고리별로 글을 게시하여 원하는 거래자를 쉽게 찾고, 자신의 시간과 능력을 경제적 가치로 전환할 수 있습니다.
또한, 근처에 있는 거래자의 위치 정보와 실시간 채팅 기능을 제공하여 사용자가 더욱 편리하게 앱을 사용할 수 있도록 돕습니다.
카카오페이를 통한 포인트 충전 방식의 틈새페이 서비스를 통해 더욱 안전한 거래를 지원합니다.
> 

## **2. 개발 기간**

---

전체 개발 기간 : 24.03.14 - 24.06.02

## 3. 팀원 소개

---

| 임지민 | 장소현 | 강병훈 |
| :---: | :---: | :---: |
| [@jimmy0524](https://github.com/jimmy0524) | [@Jang-SoHyeon](https://github.com/Jang-SoHyeon) | [@yosong6729](https://github.com/yosong6729) |
| EC2 서버 배포 및 DB 연결, 게시글 기능(게시글 CRUD, 페이징, 이미지, 검색), 결제 기능(만나서 결제, 계좌이체, 틈새페이) | 로그인 기능(카카오 간편 로그인, JWT 토큰 발급), 프로필 조회, 평가(매너 평가,서비스 평가, 틈새시간, 평가내역), 이의 신청 | 채팅 기능(1:1 채팅, 이미지 전송, 실시간 거래 기능), 실시간 알림 기능(키워드 알림, 활동 알림) |

## 4. 개발 환경

---

- Java 17
- JDK 17.0.6
- **IDE** : IntelliJ IDEA 2024.1
- **Framework** : Springboot(3.2.4)
- **Database** : MySQL, redis
- **ORM** : Spring Data JPA
- **배포** : EC2 nginx 서버에 배포
- **API 툴** : Postman

## 5. 브랜치 전략 및 협업 규칙

---

- main, feat 브랜치로 나누어 개발을 진행하였습니다.
    - main 브랜치는 최종 개발 완료된 feat 브랜치의 내용을 merge하는 브랜치입니다.
    - feat 브랜치는 각 기능에 대하여 브랜치를 생성하고, 개발을 진행한 후, 각 진행중인 feat 브랜치에 개발 완료된 feat 브랜치를 pull하는 방식으로 운영하였습니다.

## 6. 프로젝트 구조

---

<details>
  <summary>프로젝트 구조</summary>

  ```xml
  src
  ├─main
  │  ├─generated
  │  ├─java
  │  │  └─backend
  │  │      └─time
  │  │          │  TimeApplication.java
  │  │          │
  │  │          ├─config
  │  │          │  │  SecurityConfig.java
  │  │          │  │  WebConfig.java
  │  │          │  │  WebSocketConfig.java
  │  │          │  │
  │  │          │  ├─auth
  │  │          │  │      PrincipalDetail.java
  │  │          │  │      PrincipalDetailService.java
  │  │          │  │
  │  │          │  └─jwt
  │  │          │          JwtRequestFilter.java
  │  │          │          JwtTokenUtil.java
  │  │          │
  │  │          ├─controller
  │  │          │      BoardApiController.java
  │  │          │      BoardController.java
  │  │          │      ChatController.java
  │  │          │      ChatRoomController.java
  │  │          │      KeywordController.java
  │  │          │      MemberApiController.java
  │  │          │      MyPageController.java
  │  │          │      NotificationController.java
  │  │          │      ObjectionApiController.java
  │  │          │      PayController.java
  │  │          │      ReportApiController.java
  │  │          │      ScrapApiController.java
  │  │          │
  │  │          ├─dto
  │  │          │  │  ActivityNotificationDto.java
  │  │          │  │  ActivityNotificationListDto.java
  │  │          │  │  BoardDistanceDto.java
  │  │          │  │  BoardListResponseDto.java
  │  │          │  │  ChatDto.java
  │  │          │  │  ChatResponseDto.java
  │  │          │  │  ChatRoomDetailDto.java
  │  │          │  │  ChatRoomResponseDto.java
  │  │          │  │  EvaluationDto.java
  │  │          │  │  KakaoDto.java
  │  │          │  │  KeywordDto.java
  │  │          │  │  KeywordNotificationDto.java
  │  │          │  │  KeywordNotificationListDto.java
  │  │          │  │  MannerEvaluationDto.java
  │  │          │  │  MemberDto.java
  │  │          │  │  NicknameDto.java
  │  │          │  │  ObjectionDto.java
  │  │          │  │  PayResponseDto.java
  │  │          │  │  ResponseDto.java
  │  │          │  │  RoomEnterDto.java
  │  │          │  │  ServiceEvaluationDto.java
  │  │          │  │  ServiceEvaluationStarDto.java
  │  │          │  │  TokenDto.java
  │  │          │  │  UnfinishedMemberDto.java
  │  │          │  │
  │  │          │  ├─request
  │  │          │  │      BoardDto.java
  │  │          │  │      BoardSearchDto.java
  │  │          │  │      BoardUpdateDto.java
  │  │          │  │      PayDto.java
  │  │          │  │      PayMethDto.java
  │  │          │  │      PointDto.java
  │  │          │  │      ReportDto.java
  │  │          │  │      ScrapDto.java
  │  │          │  │
  │  │          │  └─response
  │  │          │          EvaluationResponseDto.java
  │  │          │          MemberResponseDto.java
  │  │          │          ObjectionResponseDto.java
  │  │          │          ServiceEvaluationResponseDto.java
  │  │          │
  │  │          ├─exception
  │  │          │      MemberNotFoundException.java
  │  │          │
  │  │          ├─handler
  │  │          │      GlobalExceptionHandler.java
  │  │          │      KakaoLoginFailureHandler.java
  │  │          │      KakaoLoginSuccessHandler.java
  │  │          │
  │  │          ├─model
  │  │          │  │  ActivityNotification.java
  │  │          │  │  ActivityType.java
  │  │          │  │  ChatImage.java
  │  │          │  │  ChatMessage.java
  │  │          │  │  ChatRoom.java
  │  │          │  │  ChatType.java
  │  │          │  │  Keyword.java
  │  │          │  │  KeywordNotification.java
  │  │          │  │  Report.java
  │  │          │  │  ReportCategory.java
  │  │          │  │  Scrap.java
  │  │          │  │
  │  │          │  ├─board
  │  │          │  │      Board.java
  │  │          │  │      BoardCategory.java
  │  │          │  │      BoardState.java
  │  │          │  │      BoardType.java
  │  │          │  │      Image.java
  │  │          │  │
  │  │          │  ├─Member
  │  │          │  │      MannerEvaluation.java
  │  │          │  │      MannerEvaluationCategory.java
  │  │          │  │      Member.java
  │  │          │  │      Member_Role.java
  │  │          │  │      ServiceEvaluation.java
  │  │          │  │      ServiceEvaluationCategory.java
  │  │          │  │      ServiceStar.java
  │  │          │  │
  │  │          │  ├─Objection
  │  │          │  │      Objection.java
  │  │          │  │      ObjectionImage.java
  │  │          │  │      ObjectionStatus.java
  │  │          │  │
  │  │          │  └─pay
  │  │          │          Account.java
  │  │          │          PayCharge.java
  │  │          │          PayMethod.java
  │  │          │          PayStorage.java
  │  │          │
  │  │          ├─repository
  │  │          │      AccountRepository.java
  │  │          │      ActivityNotificationRepository.java
  │  │          │      BoardRepository.java
  │  │          │      ChatImageRepository.java
  │  │          │      ChatRepository.java
  │  │          │      ChatRoomRepository.java
  │  │          │      CustomBoardRepository.java
  │  │          │      CustomBoardRepositoryImpl.java
  │  │          │      ImageRepository.java
  │  │          │      KeywordNotificationRepository.java
  │  │          │      KeywordRepository.java
  │  │          │      MannerEvaluationRepository.java
  │  │          │      MemberRepository.java
  │  │          │      ObjectionImageRepository.java
  │  │          │      ObjectionRepository.java
  │  │          │      PayChargeRepository.java
  │  │          │      PayStorageRepository.java
  │  │          │      ReportRepository.java
  │  │          │      ScrapRepository.java
  │  │          │      ServiceEvaluationRepository.java
  │  │          │      ServiceStarRepository.java
  │  │          │
  │  │          ├─service
  │  │          │      BoardService.java
  │  │          │      BoardServiceImpl.java
  │  │          │      ChattingService.java
  │  │          │      ChattingServiceImpl.java
  │  │          │      ImageManager.java
  │  │          │      KeywordService.java
  │  │          │      MemberService.java
  │  │          │      MemberServiceImpl.java
  │  │          │      NotificationService.java
  │  │          │      ObjectionService.java
  │  │          │      PayService.java
  │  │          │      ReportService.java
  │  │          │      ScrapService.java
  │  │          │
  │  │          └─specification
  │  │                  BoardSpecification.java
  │  │                  ScrapSpecification.java
  │  │
  │  └─resources
  │      │  application-mysql.properties
  │      │  application.properties
  │      │
  │      └─static
  │          └─images
  │              ├─jpeg
  │              ├─jpg
  │              └─png
  └─test
      └─java
          └─backend
              └─time
                  │  TimeApplicationTests.java
                  │
                  └─chat
                          BoardControllerTest.java
                          ChatRoomControllerTest.java
                          ChattingServiceTest.java
  ```
</details>

## 7. API 명세서

---

[![image](https://github.com/user-attachments/assets/8a76b1e4-c48f-4ee9-b8da-cdc798ee801a)](https://documenter.getpostman.com/view/23052522/2sA3Bg9FFe)


## 8. 상세 기능

---

![Group 142](https://github.com/user-attachments/assets/16e2fc36-b1af-49fd-bcc1-41d249d9db77)

---

### 로그인

카카오 간편 로그인을 이용하여 회원가입 및 사용자 인증을 진행하였고, 첫 카카오 로그인 후 틈새 시장만의 가입 정보를 수집하였습니다. 해당 과정을 마친 회원들에게는 accessToken과 refreshToken을 발급하였고 **Spring Security**와 **JWT**를 이용하여 사용자 인가/인증 기능을 구현하였습니다. 

### 게시글

- 검색 : 먼저 SQL 문에서 하버사인 공식을 활용하여 10km 반경 내의 데이터를 필터링한 후, 얻어진 게시글의 ID, 카테고리, 제목, 내용을 바탕으로 **Specification** 객체를 사용하여 추가적인 필터링을 수행했습니다. 또한, 페이지 번호(pageNum)를 받아 8개씩 게시글을 조회할 수 있도록 **Pageable** 인터페이스를 활용하여 페이징 기능을 구현하였습니다.
- 이미지 : 이미지를 **MultipartFile** 형태로 받아 UUID를 이용해 고유한 파일 이름을 생성한 후, DB에 기존 파일명과 함께 해당 이름과 확장자 형식으로 저장하였습니다. 또한, Nginx 서버의 특정 폴더에 이미지를 저장하여 프론트에서 해당 주소로 이미지에 접근할 수 있도록 하였습니다.

### 알림

실시간 알림 기능은 사용자에게 중요한 활동을 놓치지 않도록 도와줄 수 있고 **Server-Sent Events (SSE)** 방식을 사용하여 실시간으로 푸시알림을 제공합니다.

- 알림 유형
    - 활동 알림
        - 좋아요 : 사용자가 작성한 게시글에 누군가 좋아요를 누르면 실시간으로 푸시 알림이 전송됩니다.
        - 채팅 : 거래를 위해 상대방이 채팅을 시작하면 실시간 푸시 알림을 받게 됩니다.
        - 거래 완료 : 거래가 성공적으로 완료 되었으면 거래가 완료 되었다는 알림이 전송됩니다.
    
    이러한 활동 알림은 활동 알림 목록에서 확인할 수 있습니다.
    
    - 키워드 알림 : 사용자가 설정한 특정 키워드(예: 산책)가 포함된 게시글이 작성되면, 즉시 실시간 푸시 알림이 전송됩니다. 이러한 알림은 키워드 알림 목록에서 확인 가능하며, 사용자가 관심 있는 게시글을 빠르게 찾을 수 있도록 돕습니다.

### 프로필

사용자에 대한 정보를 조회하는 기능입니다.

- 틈새 시간 : 서비스 평가 점수와 매너 평가 점수를 평균하여 시간으로 환산한 기능입니다. 서로의 시간을 거래하는 심부름 앱 특성상 신뢰가 중요하므로, 사용자들에게 신뢰를 주기 위해 도입되었습니다.
- 나의 프로필 : 틈새시간을 확인할 수 있고, 중복 검사 후 닉네임을 변경할 수 있습니다.
- 타인의 프로필 : 그 사람의 틈새시간과 그 사람이 받은 평가 내역들을 확인할 수 있습니다.

### 결제

결제 방법은 총 3가지로 만나서 결제, 계좌이체, 틈새페이가 있고, 채팅에서 결제 방법 선택 시 거래 중 게시글로 변경됩니다. 최종적으로 거래 완료 버튼 클릭 시 거래 완료로 변경됩니다. (3일 후에 자동으로 거래 완료 상태로 변경)

- 만나서 결제 : 만나서 결제 후 시간 판매자가 거래 완료 또는 거래 취소를 선택할 수 있습니다.
- 틈새페이
    - 마이페이지에서 포인트 충전 시 → **포트원 API** 사용하여 프론트에서 거래고유번호를 받아 카카오페이 결제 검증이 이루어지고 검증 로직에 통과하면 포인트 충전이 완료됩니다.
    - 틈새페이 방법 선택 시 → 바로 시간 구매자의 포인트가 차감되고, 거래완료 시 포인트가 거래자에게 이동되고, 거래 취소 시 다시 포인트가 복구됩니다.
        
        (안전한 거래를 위하여 모든 거래자가 거래 완료를 눌러야 포인트가 이동됩니다.)
        
- 계좌이체 : 시간 판매자의 은행, 계좌번호 데이터를 입력받아 DB에 저장하고, 거래 후 시간 판매자가 거래 완료 또는 거래 취소를 선택할 수 있습니다.

### 평가

평가는 시간을 사고 파는 앱의 특성 상 신뢰를 위한 더 상세한 평가가 필요하여, 서비스에 대한 평가와 매너에 대한 평가 두 가지로 나누어 진행하였습니다.

- 평가 확인 : 사용자가 받은 평가는 마이페이지의 ‘받은 매너 평가’에서 확인할 수 있습니다. 여기에는 매너 평가와 서비스 카테고리로 구분된 평가가 포함됩니다.
- 매너 평가 : 사용자가 받은 각 매너 평가의 개수를 나타내며, 이를 통해 사용자 간의 상호작용에 대한 신뢰도를 제공합니다.
- 서비스 평가 : 재능기부, 운동, 심부름 등 각 카테고리 별 평가 점수를 별점으로 기록되어 있으며,  각 카테고리로 들어가면 해당 카테고리에서 받은 평가 내역을 확인할 수 있습니다.

### 이의신청

거래가 완료되고, 시간에 대한 비용이 지불된 후, 돈을 받은 사람이 일을 제대로 처리하지 못했을 경우 이용할 수 있는 기능입니다. 사용자는 증거 사진과 함께 이의신청을 보낼 수 있습니다.

- 이의신청 내역 : 이의 신청한 내역의 진행 상황은 마이페이지의 '이의신청 내역'에서 확인할 수 있습니다. 이곳에서 누구에게 이의신청을 했는지와 해당 이의신청의 진행 상태를 확인할 수 있습니다.

### 채팅

채팅 기능은 주기적으로 서버에 데이터를 요청하여 새로운 메시지를 받아오는 **Polling** 방식을 이용하여 게시글 작성자와 사용자 간의 원활한 소통과 거래를 지원하기 위해서 구현되었습니다. 이 기능을 통해 사용자는 상대방과 대화하며 거래를 진행할 수 있습니다.

- 주요기능
    - 1:1 채팅 : 게시글 작성자와 거래자가 1:1 채팅을 통해 거래 조건 등을 논의하고 대화할 수 있습니다.
    - 이미지 전송 : 채팅 중 이미지 전송이 가능하여, 거래와 관련된 추가적인 정보를 시각적으로 공유할 수 있습니다.
    - 실시간 거래 기능 : 채팅 창에서 [만나서 결제], [틈새페이], [계좌이체] 버튼을 전송하여 상대방과의 거래가 가능합니다.
<br><br><br>

## 9. ERD

---

![35c81d6e-1048-4b0f-b48a-d0da91e8a7be](https://github.com/user-attachments/assets/58253a4f-d2fb-4627-ac4b-7dee8a7792e0)
