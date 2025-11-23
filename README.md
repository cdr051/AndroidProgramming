📱 Cospicker
--------------------------------
여행, 맛집, 숙박, 커뮤니티 기능을 한곳에 모아
사용자가 원하는 정보를 쉽고 빠르게 찾을 수 있도록 설계된 여행 통합 플랫폼 프로토타입입니다.

🌍 프로젝트 소개
-----------------------
Cospicker는 여행 숙소와 맛집 검색, 그리고 커뮤니티 기능을 통한 정보 공유를 제공하는 Android 앱입니다.
사용자는 다양한 사람들과 여행 정보를 공유하고 자신의 경험을 나누며,
여행 플래너처럼 사용할 수 있는 서비스를 제공합니다.

👥 멤버 구성
--------------
▪ 프론트엔드

팀장 : 김선욱

팀원 : 권오현

▪ 백엔드

팀원 : 최동렬

팀원 : 송지헌



개발 환경
--------------

백엔드 : FireBase  
개발 환경 : Kotlin  
IDE : Android Studio  
빌드 시스템 : Gradle  
협업 및 배포 : GitHub  


주요 기능
---------------------




📂 Cospicker 프로젝트 구조
--------------------------
```plaintext

com.example.cospicker  
│  
├── auth                                // 회원가입 / 로그인 관련 화면  
│   ├── LoginIntroActivity              // 로그인/회원가입 선택 화면  
│   ├── ProfileRegisterActivity         // 프로필 최초 등록  
│   ├── SignupActivity                  // 회원가입 입력 화면  
│   └── SignupCompleteActivity          // 회원가입 완료 화면  
│
├── chat                                // 채팅 기능 전체  
│   ├── ChatListActivity                // 채팅방 목록  
│   ├── ChatRoomActivity                // 1:1 채팅 화면  
│   ├── ChatUtil                        // 채팅 공용 함수  
│   ├── adapter
│   │   ├── ChatListAdapter             // 채팅방 목록 어댑터  
│   │   └── ChatMessageAdapter          // 메시지 목록 어댑터  
│   └── model
│       ├── ChatRoom                    // 채팅방 데이터 모델    
│       └── ChatMessage                 // 메시지 데이터 모델    
│
├── community                           // 커뮤니티 기능  
│   ├── CommunityActivity               // 커뮤니티 메인(게시글 리스트)  
│   ├── CommunityEditPostActivity       // 게시글 수정  
│   ├── CommunityPostDetailActivity     // 게시글 상세 + 댓글  
│   ├── CommunitySearchActivity         // 검색창  
│   ├── CommunitySearchResultActivity   // 검색 결과 목록  
│   ├── CommunityWritePostActivity      // 글쓰기 화면  
│   ├── adapter
│   │   ├── CommentAdapter              // 댓글 어댑터  
│   │   └── PostAdapter                 // 게시글 어댑터  
│   └── model
│       ├── Post                        // 게시글 모델  
│       └── Comment                     // 댓글 모델  
│
├── home
│   └── HomeActivity                    // 메인 + 하단탭 UI  
│
├── myinfo                               // 내정보 화면 전체    
│   ├── MyInfoActivity                   // 내 정보 메인  
│   ├── MyCommentsActivity               // 내가 쓴 댓글  
│   ├── MyPostsActivity                  // 내가 쓴 글  
│   ├── NotificationListActivity         // 알림 목록  
│   ├── ProfileActivity                  // 프로필 관리  
│   ├── LogoutActivity                   // 로그아웃  
│   ├── model
│   │   └── NotificationItem             // 알림 데이터 모델  
│   ├── MyCommentsAdapter                // 내 댓글 어댑터  
│   └── NotificationAdapter              // 알림 어댑터  
│
├── notice                               // 공지사항    
│   ├── NoticeListActivity               // 공지 목록  
│   ├── NoticeDetailActivity             // 공지 상세  
│   ├── adapter
│   │   └── NoticeAdapter                // 공지 어댑터  
│   └── model
│       └── Notice                       // 공지 모델  
│
├── splash
│   └── SplashActivity                   // 앱 시작 로딩  
│
└── stay                                 // 숙소 검색/상세 기능  
    ├── adapter
    │   └── StayAdapter                  // 숙소 리스트 어댑터  
    ├── model
    │   └── Stay                         // 숙소 데이터 모델  
    └── search
        ├── StaySearchActivity           // 숙소 검색창  
        ├── StaySearchResultActivity     // 검색 결과 목록  
        ├── StayListActivity             // 필터 적용된 리스트  
        ├── StayDetailActivity           // 숙소 상세  
        └── StayDatePeopleBottomSheet    // 날짜 + 인원 선택 BottomSheet  

```


