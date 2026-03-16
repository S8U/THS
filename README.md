# THS

마인크래프트 풀잎서버 시즌 5까지 사용한 커스텀 플러그인 프로젝트입니다.

BungeeCord 네트워크 환경에서 동작하는 다수의 플러그인으로 구성된 모노레포로, 서버 운영에 필요한 핵심 프레임워크부터 게임 컨텐츠까지 전체 시스템을 자체 설계/개발했습니다.

## 주요 기능

- **코어 프레임워크** — Bukkit/BungeeCord 크로스플랫폼 지원, DB 커넥션, 명령어/GUI/이벤트, 플레이어 추상화 등 전 플러그인이 공통으로 사용하는 기반 플러그인
- **크로스서버 채널 시스템** — Plugin Messaging Channel 기반 실시간 채널 동기화, 채널 간 플레이어 이동 및 밸런싱
- **권한/재화 시스템** — Vault Provider로 등록되는 MySQL 기반 권한 그룹 관리 + 게임 재화 API
- **능력자 PvP 시스템** — 플레이어에게 랜덤 능력을 부여하고 PvP를 진행하는 핵심 컨텐츠. 능력 추첨/재추첨, 킬·어시스트·멀티킬 보상, 보급품 드롭, 킷 선택 등 지원

## 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Java 8 |
| **Platform** | Spigot 1.8.8, BungeeCord |
| **Database** | MySQL 8.0, SQLite |
| **Library** | Lombok 1.18, Gson 2.8, Javassist, Commons Pool 2 |
| **연동** | Vault, PlaceholderAPI, ProtocolLib, Citizens, HolographicDisplays, AdvancedReplay |
| **Scripting** | JavaScript (Nashorn Engine) |
| **Build** | IntelliJ IDEA Artifacts, Gradle (일부 플러그인) |

## 아키텍처

```
U-Core (Common) ─── 전 플러그인 공용 프레임워크 (DB, Config, Player API)
│
├── U-Core (Bukkit) ─── Spigot 1.8.8 Game Server
│   ├── U-Permission                  # 등급/권한 관리 (Vault Provider)
│   │   └── U-BuyRank                 # 등급 상점 (Vault 연동)
│   ├── U-Channel (Bukkit)            # 서버 채널 관리
│   │   ├── U-ChannelGUI              # 채널 선택 GUI
│   │   └── U-ChannelNPC              # 채널 선택 NPC (Citizens, HolographicDisplays 연동)
│   ├── U-Ability                     # 능력자 PvP 미니게임
│   │   └── U-PhysicalFightersPack    # 염료 능력자 확장팩
│   ├── U-MiniGameCore                # 미니게임 프레임워크 (미완성/미사용)
│   ├── U-Cash                        # 게임 재화 시스템
│   ├── U-Effect                      # 파티클/이펙트
│   ├── U-LobbySystem                 # 로비 서버 관리
│   ├── U-PVPStats                    # PVP 킬/데스/어시스트 통계
│   ├── U-Prefixer                    # 등급별 칭호/접두사
│   ├── U-ItemTools                   # 커스텀 아이템 속성 (공격력, 방어력)
│   ├── U-GEssentials (Bukkit)        # 크로스서버 유틸리티
│   ├── U-GLogin (Bukkit)             # 크로스서버 로그인
│   ├── U-OptionGUI                   # GUI 기반 플레이어 설정
│   ├── U-OnlineModeGift              # 정품 유저 보상
│   ├── U-AFKMover                    # 잠수 플레이어 자동 이동
│   ├── U-GameReplay                  # 게임 리플레이 녹화 (AdvancedReplay 연동)
│   ├── U-BlockHideNSeek              # 블록 숨바꼭질 미니게임
│   ├── U-CMDLocker                   # 명령어 잠금
│   ├── U-AntiRecipe                  # 조합법 비활성화
│   ├── U-SaveDisabler                # 월드 자동저장 비활성화
│   ├── U-NoKeepInventory             # keepInventory 강제 해제
│   └── U-WorldReset                  # 월드 백업/초기화
│
└── U-Core (Bungee) ─── BungeeCord Proxy
    ├── U-Channel (Bungee)            # 크로스서버 채널 라우팅
    ├── U-GLogin (Bungee)             # 크로스서버 로그인/인증
    ├── U-GEssentials (Bungee)        # 크로스서버 유틸리티
    ├── U-GParty / U-GParty2          # 크로스서버 파티 시스템
    ├── U-GFriend                     # 크로스서버 친구 시스템
    ├── U-GBroadcaster                # 공지사항
    ├── U-GCMDLocker                  # 명령어 잠금
    ├── U-GCCULogger                  # 동시접속자 로깅
    ├── U-CrackWhiteList              # 비정품 계정 화이트리스트
    └── FmlLogger                     # Forge 모드 로깅
```

## 트러블슈팅

### 크로스서버 실시간 동기화
- **문제**: BungeeCord 네트워크의 여러 Spigot 서버 간 채널, 파티, 권한 등을 실시간으로 동기화해야 함
- **해결**: Plugin Messaging Channel로 Proxy-Server 간 통신하는 계층 구성

### 플러그인 의존성 관리
- **문제**: 플러그인이 늘어나면서 로드 순서 충돌과 선택적 연동 처리가 복잡해짐
- **해결**: U-Core를 공통 기반으로 두고 `depend`(필수) / `softdepend`(선택적)를 분리. 플러그인 간 직접 호출 대신 이벤트 기반으로 통신하여 느슨한 결합 유지

### 능력자 시스템 확장성
- **문제**: 새로운 능력을 추가할 때마다 기존 코드를 수정해야 하는 구조
- **해결**: `AbilityPluginManager`를 통해 외부 플러그인이 능력을 주입할 수 있는 구조로 설계. 확장팩(PhysicalFightersPack)을 별도 플러그인으로 분리하여 코어 변경 없이 능력 추가 가능
