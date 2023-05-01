# 42Partner-Backend

### 프로젝트 내용
- 42Partner는 42Seoul생활을 하면서 학습과 식사를 함께할 파트너를 매칭해주는 프로그램입니다.
- **학습과 식사** 두 카테고리에 대해서 게임처럼 방 생성 혹은 랜덤 매칭을 통해 조건에 맞는 교육생들을 매칭해주는 기능을 제공합니다.

## Backend API Swagger Link

### [API 명세 링크](https://api.v2.42partner.com/swagger-ui/index.html#/)

## 배포 구조
### Database Design

<img width="1177" alt="image" src="https://user-images.githubusercontent.com/47822403/234504770-59fcb9bc-1c98-4b42-8eea-4f60d85d7a7a.png">

### Architecture Design
<img width="1031" alt="image" src="https://user-images.githubusercontent.com/47822403/234498210-e681f1b8-fd42-4e7c-a4aa-e6c0a7c534ae.png">

<img width="800" alt="image" src="https://user-images.githubusercontent.com/47822403/234505501-0215da77-36f0-41a1-b5aa-5fbafcf22759.png">


## Architecture Design Point
### [아키텍처 설계 과정](https://velog.io/@xogml951/AWS-HAHigh-Availability-%EA%B5%AC%EC%B6%95-%EA%B8%B0%EB%A1%9D)

- High Availavility
    - 적어도 둘 이상의 Availavility Zone에 instance가 분포 하도록 함.
    - WAS의 경우 Application Load Balancer, 와 Auto Scaling Group활용.
    - NAT Gateway의 경우 각각의 Availability Zone에 적어도 하나 위치.
    - RDS의 경우 Multi-az로 설정 하고 하나의 RDS만 Active하도록 설정
        - 여러 RDS를 Active로 설정할 정도의 부하가 없는 상황.
        - 여러 RDS를 Active로 설정해도 DB Storage 사이에 병목이 발생할 수 있기 때문에 조회 요청이 높아지면 Replication을 하는 것도 고려해야함.
- AWS VPC Custom설정(IGW, NAT Gateway, Public/Private/DB Subnet, Route Table)
    - 보안성 강화를 위한 조치
        - WAS를 실행중인 EC2
            - Private Subnet에 위치하게 하여 외부 Network에서의 직접 접근 방지
            - NAT gateway를 Route table에 추가하여 EC2에서 외부로 요청 시작(주로 외부 API호출 목적)은 가능하게함.
        - RDS
            - DB Subnet에 위치하게 하여 외부 Network에서의 직접 접근 방지
            - Private Subnet과의 차이점은 DB Subnet은 NAT Gateway와 연결하지 않음.
                - DB에서 외부로 먼저 요청할 일이 없기 때문.
        - Bastion Host
            - Public Subnet에 위치하여 EC2, RDS로 SSH, 3306포트로 접속할 수 있게함.
            - 같은 VPC내에 있기 때문에 SG만 설정해주면 접속 가능.
- Load Balancer, Auto Scaling Group
    - Load Balancer
        - 외부에서 Web Application Server로 들어오는 요청을 앞단에서 받아 EC2에 적절하게 부하를 분산.
        - HTTPS 인증을 수행하고 SSL Termination을 수행하여 WAS부터 내부 통신을 할 때에 HTTP로 내부 통신을 수행함으로서 성능을 개선할 수 있음.
    - Auto Scaling Group
        - EC2 Health Check를 통해 비정상적인 EC2 발견시 Termination
        - CloudWatch를 통해 서버 부하를 체크하여(예. CPU 사용률 70퍼이상) 필요 시 등록해 놓은 Launch Template을 통해 자동 EC2 배포.
        - 트래픽이 몰리거나 EC2내 장애가 발생했을때 기본적인 자동 대처 가능.
- Route53, Certificate Mange
    - Route53을 통해 Domain을 제공 받음.
    - AWS Certificate Manger를 통해 SSL 인증서를 발급 받음.
- Nginx
    - Nginx를 Reverse Proxy로 활용.
    - WAS 서버가 정적 파일을 직접 다루는것은 자원 낭비이기 때문에 Web Server를 앞단에 두어 처리하게 하는것이 바람직하다고 판단.
    - 추가적으로 캐싱, 로드밸런싱, 보안 강화 등의 역할을 할 수 있고 아키텍처 디자인 면에서 유연성을 확보할 수 있음.
    - Docker Compose를 활용하여 EC2내부에서 컴포넌트들을 Container화하면 더 좋을 수 있을 수 있지만 아직 구현하지 않음.

### CICD
* 배포 중 서비스를 정상 동작 시키기 위해 Blue/Green 무중단으로 배포
[Blue/Green 무중단 배포 구현 링크](https://velog.io/@xogml951/CICD-%EA%B5%AC%EC%B6%95-Github-action-code-deploy-s3)

<img width="630" alt="image" src="https://user-images.githubusercontent.com/47822403/234505615-16f8b5b8-64a4-494f-9e64-1d40df3e7326.png">


# 개발 과정 상세

## Application 개발

### SSE

[https://velog.io/@xogml951/Server-Sent-EventsSSE-Redis-pubsub-Kafka로-알림-기능-개선하기](https://velog.io/@xogml951/Server-Sent-EventsSSE-Redis-pubsub-Kafka%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EA%B8%B0%EB%8A%A5-%EA%B0%9C%EC%84%A0%ED%95%98%EA%B8%B0)

- 기존의 한계가 있던 알림 기능을 SSE와 Redis pub/sub, Kafka를 통해 해결했습니다. 첫 번째 문제는 사용자가 새로 고침을 하지 않으면 알림 내역이 갱신되지 않기 때문에 브라우저에서 주기적으로 polling을 해야 한다는 점이었습니다. 이를 해결하기 위해 Server Sent Events를 활용하여 알림 생성 시 서버에서 응답을 주는 방향으로 변경할 수 있었지만 WAS가 여러대 있기 때문에 SSE연결이 수립된 객체로 Load Balancing이 되지 않을 경우 SSE 응답을 하지 못하는 문제가 있었습니다. Redis pub/sub을 활용하여 각각의 WAS가 채널을 sub하도록 하고 SSE응답을 해야하는 경우 pub을 하여 모든 WAS에 메시지를 송신하고 응답 대상 클라이언트와 연결이 수립된 WAS가 응답을 할 수 있도록 구성하였습니다. 두 번째 문제는 특정 API호출 시 알림을 생성하고 보내야 하는 경우가 있다면 핵심기능과 알림과 관련된 로직이 동기적으로 모두 수행되어야 응답을 할 수 있다는 점입니다. 이 문제를 해결하기 위해 kafka를 활용하여 알림 이벤트를 발행하고 소비할 때 알림 생성 밑 SSE응답을 하는 방향으로 구조를 변경했습니다.

### JWT

[https://velog.io/@xogml951/Refresh-Token을-어디에-저장해야-할까Feat.-XSS-CSRF-CORS](https://velog.io/@xogml951/Refresh-Token%EC%9D%84-%EC%96%B4%EB%94%94%EC%97%90-%EC%A0%80%EC%9E%A5%ED%95%B4%EC%95%BC-%ED%95%A0%EA%B9%8CFeat.-XSS-CSRF-CORS)

[https://velog.io/@xogml951/Spring-Security-OAuth-JWT를-활용한-인증-과정-개념-및-구현-총-정리1-OAuth-개념과-로그인-및-회원가입에-활용하기](https://velog.io/@xogml951/Spring-Security-OAuth-JWT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B3%BC%EC%A0%95-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%ED%98%84-%EC%B4%9D-%EC%A0%95%EB%A6%AC1-OAuth-%EA%B0%9C%EB%85%90%EA%B3%BC-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%B0%8F-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%EC%97%90-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0)

[https://velog.io/@xogml951/Spring-Security-OAuth-JWT를-활용한-인증-과정-개념-및-구현-총-정리2-JWT개념과-회원가입-및-로그인-로직-구현](https://velog.io/@xogml951/Spring-Security-OAuth-JWT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B3%BC%EC%A0%95-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%ED%98%84-%EC%B4%9D-%EC%A0%95%EB%A6%AC2-JWT%EA%B0%9C%EB%85%90%EA%B3%BC-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%A1%9C%EC%A7%81-%EA%B5%AC%ED%98%84)

[https://velog.io/@xogml951/Spring-Security-OAuth-JWT를-활용한-인증-과정-개념-및-구현-총-정리3-OAuth-JWT-회원가입-및-로그인-로직-구현](https://velog.io/@xogml951/Spring-Security-OAuth-JWT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B3%BC%EC%A0%95-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%ED%98%84-%EC%B4%9D-%EC%A0%95%EB%A6%AC3-OAuth-JWT-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%A1%9C%EC%A7%81-%EA%B5%AC%ED%98%84)

- 인증과 인가를 JWT를 통해서 구현했습니다. 여러대의 WAS가 동작하는 환경에서 세션 방식으로 구현할 경우 Clustered Session은 API요청마다 Redis를 조회해야 하는 비용이 있고 향후 앱으로 개발할 경우 호환이 안될 수 있다는 문제가 있습니다. JWT는 token 탈취 시 이를 무력화 할 수 없다는 단점이 있지만 이를 보완하기 위해 만료시간을 짧게 유지하고 Refresh Token을 발급했습니다.

### DB 격리성

[https://velog.io/@xogml951/트랜잭션-Isolation총-정리](https://velog.io/@xogml951/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-Isolation%EC%B4%9D-%EC%A0%95%EB%A6%AC)

[https://velog.io/@xogml951/API동시성-문제-해결-기록-Optimistic-Lock과-AOP활용](https://velog.io/@xogml951/API%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%EA%B8%B0%EB%A1%9D-Optimistic-Lock%EA%B3%BC-AOP%ED%99%9C%EC%9A%A9)

- 방 매칭에서 참여 인원 제한이 있지만 동시에 서로 다른 유저가 매칭을 신청 할 경우 Mysql Repetable Read 격리수준에서 트랜잭션 이상현상인 lost update가 발생하였고 참여 인원보다 많은 인원이 참여해 버리는 문제가 발생했습니다. 이를 해결하기 위해 비관적 배타 락을 고려했으나 충돌 상황이 자주 발생하지 않고 수정 시점에 조회가 불가능해져 성능이 저하될 수 있기 때문에 낙관적 락을 활용하였습니다. 또한, 충돌이 발생하여 요청이 취소 되면  Retry하는 관심사를 분리하여 AOP를 적용하는 방식으로 해결 하였고 이를 테스트 코드를 작성하여 확인했습니다.

### 테스트 코드

[https://velog.io/@xogml951/JUnit5-Mockito와-Spring-boot-테스트-코드-작성법-총-정리-1](https://velog.io/@xogml951/JUnit5-Mockito%EC%99%80-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EB%B2%95-%EC%B4%9D-%EC%A0%95%EB%A6%AC-1)

[https://velog.io/@xogml951/JUnit5과-Spring-boot-테스트-코드-작성법-Repository-Layer-단위테스트-방법2](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EB%B2%95-Repository-Layer-%EB%8B%A8%EC%9C%84%ED%85%8C%EC%8A%A4%ED%8A%B8-%EB%B0%A9%EB%B2%952)

[https://velog.io/@xogml951/JUnit5과-Spring-boot-테스트-코드-작성-Test-Double-사용에-따른-Trade-Off와-Service-Layer-단위-테스트3](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-Test-Double-%EC%82%AC%EC%9A%A9%EC%97%90-%EB%94%B0%EB%A5%B8-Trade-Off%EC%99%80-Service-Layer-%EB%8B%A8%EC%9C%84-%ED%85%8C%EC%8A%A4%ED%8A%B83)

[https://velog.io/@xogml951/JUnit5과-Spring-boot-테스트-코드-작성-Controller-Layer-단위-테스트와-인증-인가-테스트-4](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-Controller-Layer-%EB%8B%A8%EC%9C%84-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%ED%85%8C%EC%8A%A4%ED%8A%B8-4)

[https://velog.io/@xogml951/JUnit5과-Spring-boot-테스트-코드-작성-테스트-커버리지와-Jacoco-5](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BB%A4%EB%B2%84%EB%A6%AC%EC%A7%80%EC%99%80-Jacoco-5)

- 테스트를 작성하지 않으면 더 빨리 개발할 수 있다고 느껴져도 프로젝트가 진행 될 수록 지속적으로 비용이 발생한다는 것을 이전 프로젝트 경험에서 깨닫고 단위 테스트를 잘 작성하기 위한 고민과 이를 적용해보려고 했습니다. 리팩토링 내성, 회귀 방지, 빠른 테스트의 요소가 중요하다는 점을 인지하고 각 요소가 배타적인 성격을 가지고 있기 때문에 테스트 코드 작성 방식에 따른 트레이드 오프를 생각하면서 작성했습니다. 요소들의 우선순위를 리팩토링 내성, 회귀 방지, 빠른 테스트 순으로 만족시키려고 하였습니다. Test Double을 남발 할 시 해당 인터페이스가 변경 될 경우 거짓 음성이 발생하고 리팩토링 내성이 저하되기 때문에 외부 API요청, 랜덤 성격의 로직 등 테스트 시 컨트롤 할 수 없는 모듈인 경우에만 적용 하려고 했습니다. 또한 이와 같은 요소를 최대한 상위 모듈쪽에서 호출 되도록 하였는데 테스트 할 수 없는 요소가 있는 모듈을 사용하는 모듈 또한 테스트 할 수 없는 요소가 포함되어 테스트하기 힘든 코드가 되기 때문입니다. 프로젝트에서 적용한 코드의 예시로 사용자 요청에서 현재 시간이 필요한 경우 이를 Controller에서 LocalDateTime.now()를 통해 결정하고 Service, Repository, Domain Layer에서는 Parameter로 전달하는 방식으로 코드를 작성하여야 해당 계층 들에서 현재 시간과 상관없이 실행 되는 방식으로 테스트 코드를 작성할 수 있었습니다.

### 트랜잭션 분리 및 Event Queue

[https://velog.io/@xogml951/랜덤-매칭-polling-배치-트랜잭션-분리-및-리팩토링4](https://velog.io/@xogml951/%EB%9E%9C%EB%8D%A4-%EB%A7%A4%EC%B9%AD-polling-%EB%B0%B0%EC%B9%98-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EB%B6%84%EB%A6%AC-%EB%B0%8F-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%814)

- 랜덤 매칭 도메인에서 사용자가 매칭을 신청할 경우 매칭 알고리즘 또한 적어도 한 번 실행되어야 합니다. 이 때 매칭이 동작하는 방식에 문제가 있어 이를 개선하였습니다. 첫 번째 문제는 하나의 트랜잭션으로 매칭 알고리즘이 실행 되어 비 효율적일 수 있다는 점입니다. 매칭 확정 내역을 최종적으로 DB에 commit할 때 랜덤 매칭 신청 취소 등의 사용자 요청과 충돌하여 전체 트랜잭션이 Rollback될 수 있고 이와 상관없는 유저들의 정상적인 매칭까지 확정 될 수 없는 문제가 있습니다. 또한 매칭 맺어주는 로직내에는 DB와 상관없는 정렬과 같은 CPU연산이 주로 포함되어 있기 때문에 DB Connection을 불 필요하게 오래 가지고 있을 수 있습니다. 이를 해결하기 위해 트랜잭션을 분리하는 방향으로 코드를 리팩토링 하였습니다. 두 번째 문제는 매칭 알고리즘은 동시에 여러 스레드에서 실행 되서는 안되고 동시에 하나의 스레드에서만 실행되는 것이 보장되어야 한다는 점입니다. 그렇지 못할 경우 매칭이 중복 해서 여러번 발생할 수 있고 이를 방지하기 위해 낙관적 락을 활용 하여도 Rollback될 매칭 알고리즘이 불 필요하게 실행되기 때문입니다. 세 번째 문제는 매칭 신청 사용자 요청과 동기적으로 매칭 알고리즘이 동작하여 지연률이 증가하는 문제입니다. 이를 해결하기 위해서는 매칭 알고리즘을 비동기적으로  실행하여 매칭 신청에 대한 응답을 바로 받을 수 있도록 해야합니다. 네 번째 문제는 매칭 알고리즘이 Infra등의 문제로 정상 동작하지 못하면 재 실행 주는 것을 보장해주어야 했습니다. 두 번째, 세 번째, 네 번재 문제를 해결해 주기 위해서 Kafka를 활용하여 이벤트를 프로듀스, 컨슘하는 방식으로 변경하고 이벤트 생성 시 key값을 주어 하나의 파티션에만 생성되도록 보장하여 여러 consumer들이 동시에 매칭 알고리즘을 실행하는 경우를 방지했습니다.

### N+1 문제

[https://velog.io/@xogml951/JPA-N1-문제-해결-총정리](https://velog.io/@xogml951/JPA-N1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%EC%B4%9D%EC%A0%95%EB%A6%AC)

- N+1문제를 해결하기 위해 페이징이 포함된 Select 쿼리에 Fetch Join을 사용했을 때 warn log가 발생했습니다. 페이징 Select 쿼리에서 Fetch Join을 사용할 경우 전체 테이블을 메모리에 로딩해야 하며 OutOfMemory와 같은 심각한 장애의 원인이 될 수 있음을 깨닫게 되었습니다. 이 이후에 Fetch Join의 제약과 올바른 사용법 그리고 N+1문제를 해결할 수 있는 다른 방법들에 대해서 정리하고 각 방법의 장 단점을 고려하여 프로젝트에 적용할 수 있었습니다.

### 객체지향

[https://velog.io/@xogml951/오브젝트조영호-요약-정리](https://velog.io/@xogml951/%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8%EC%A1%B0%EC%98%81%ED%98%B8-%EC%9A%94%EC%95%BD-%EC%A0%95%EB%A6%AC)

[https://velog.io/@xogml951/비지니스-로직을-Service에서-Domain-Model로-옮기기-오브젝트조영호-2](https://velog.io/@xogml951/%EB%B9%84%EC%A7%80%EB%8B%88%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%9D%84-Service%EC%97%90%EC%84%9C-Domain-Model%EB%A1%9C-%EC%98%AE%EA%B8%B0%EA%B8%B0-%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8%EC%A1%B0%EC%98%81%ED%98%B8-2)

[https://velog.io/@xogml951/프로젝트를-데이터-중심설계에서-책임주도설계로-변경해보자](https://velog.io/@xogml951/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EB%A5%BC-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A4%91%EC%8B%AC%EC%84%A4%EA%B3%84%EC%97%90%EC%84%9C-%EC%B1%85%EC%9E%84%EC%A3%BC%EB%8F%84%EC%84%A4%EA%B3%84%EB%A1%9C-%EB%B3%80%EA%B2%BD%ED%95%B4%EB%B3%B4%EC%9E%90)

- 핵심 비지니스 로직을 객체지향 설계원리와 Service Layer에서 Domain Model로 모듈화 하는 형태로 리팩토링하였습니다. 이 과정에서 ‘오브젝트’라는 책을 읽게 되었는데 좋은 객체지향 설계는 객체가 스스로 자신의 상태에 대해서 책임지고 이러한 객체들이 서로 협력하는 방향으로 구성된 것이며 다형성을 활용하여 같은 메시지에 다르게 책임을 질 수 있도록 하여 유연한 구성을 할 수 있음을 알게 되었습니다. 또한 Solid 원칙과 객체지향의 4대요소들이 왜 중요한지 깊이 있게 이해할 수 있었고 이러한 이해 과정과 리팩토링 과정중 깨달은 점들을 정리하였습니다.

### 캐싱

- 강남구 주변 맛집 추천 기능을 구현하기 위해 주변 맛집 데이터를 DB로 부터 읽어들어와야 했습니다. 맛집 데이터 조회를 모니터링 시스템을 통해 테스트 해본 결과 DB에 큰 부하를 줄 뿐만 아니라 서버 TPS및 응답 시간도 저하시키는 요인이라는 것을 알게 되었습니다. 맛집 데이터는 수정이 거의 발생하지 않기 때문에 Cache를 사용하기 적합하다고 판단 하였고 WAS 간의 데이터 일관성 문제가 발생할 일도 거의 없기 때문에 로컬 캐시와 분산 캐시 중 로컬 캐시를 선택했습니다. 로컬 캐시를 활용할 경우 조회 속도가 월등히 빠르지만 WAS Heap 메모리에 데이터를 로딩하고 사용해야하기 때문에 최초 조회 시점에 어느정도의 부하가 생기는지 테스트 하였고 적절한 만료정책을 적용했습니다.

## Dev-Ops

### 무중단 배포 자동화

[https://velog.io/@xogml951/CICD-구축-Github-action-code-deploy-s3](https://velog.io/@xogml951/CICD-%EA%B5%AC%EC%B6%95-Github-action-code-deploy-s3)

- 이전에 했던 프로젝트를 운영하면서 배포를 자동화 하지 않다 보니 배포 시에 서비스를 중지 시켜야했고 팀원 간에 서로 배포를 진행하면서 충돌이 발생하는 문제와 배포 시 실수를 하는 경우가 발생했습니다. 이 프로젝트에서는 Github Action과 Codedeploy를 활용하여 Auto Scaling Group의 Blue/Green 배포 자동화를 구축하여 해결 할 수 있었습니다.

### AWS High Availability Architecture 구성

[https://velog.io/@xogml951/AWS-HAHigh-Availability-구축-기록](https://velog.io/@xogml951/AWS-HAHigh-Availability-%EA%B5%AC%EC%B6%95-%EA%B8%B0%EB%A1%9D)

- Architecture 구성 시 가용성과 확장성을 만족할 수 있는 방향으로 구성했습니다. Load Balancer와 Auto Scaling Group을 설정하여 사용자 요청이 증가할 경우 부하를 분산하고 WAS 인스턴스가 조정될 수 있도록 하였습니다. DataSource들은 적어도 둘 이상의 node가 구성되도록 하여 Failover 할 수 있도록 구성했으며 서로 다른 Availability Zone에 위치 시켰습니다. 보안성 향상을 위해 VPC를 구성하여 Private Subnet에 WAS, DataSource등을 위치 시키고 Public Subnet에 Bastion host를 구성하여 SSH접속을 할 수 있도록 합니다.

### 성능 모니터링

[https://velog.io/@xogml951/AWS-Auto-Scaling-Group-EC2-WAS-대상Actuator-Prometheus-Grafana-Micrometer-모니터링-적용](https://velog.io/@xogml951/AWS-Auto-Scaling-Group-EC2-WAS-%EB%8C%80%EC%83%81Actuator-Prometheus-Grafana-Micrometer-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%A0%81%EC%9A%A9)

- JVM, DBCP, Thread Pool등의 실제 어플리케이션이 동작할 때 영향을 미치는 요소들에 대해서 학습하게 되면서 현재 어플리케이션이 동작하고 있는 상태를 확인하고 조정해 볼 필요를 느끼게 되었습니다. Grafana, Prometheus를 활용하여 성능 모니터링 시스템을 구축하고 JMeter를 통해 부하 테스트를 진행하여 리소스 사용을 관찰하면서 DBCP, Thread Pool size, GC종류등의 설정을 조정했습니다.

### 로깅

- 이전에 했었던 구해줘 카뎃 프로젝트를 운영할 때 API요청 시 에러가 발생하게 되면 그때마다 EC2에 접속하여 리다이렉션된 로그파일을 확인하는 방식으로 대응했습니다. 로그 발생 기록을 확인하기 힘들었던 경험이 있어 이를 해결하기 위해 API요청 시 발생한 에러 로그를 쉽게 확인하고 통계 데이터를 제공해주는 Sentry를 활용하게 되었습니다. 또한 Logback 설정을 RollingFile 방식으로 구성하여 특정 시기가 지나면 자동 삭제될 수 있도록 하고 쌓아놓은 로그를 활용할 경우를 생각해 로그 포맷을 Custom하였습니다.


## 프로젝트 수행 배경 및 필요성

### 프로젝트 수행 배경
- 42Seoul이라는 교육 과정에 참여하면서 교육생 끼리 서로 교류할 필요성이 많은 과정임에도 기회가 적다고 느끼게 되었습니다.
- 또한 식당이 아니라 교육장으로 배달을 시켜서 먹는 경우가 많은데 배달비가 부담이 되어 Slack을 통해 글을 올리는 분들이 많습니다.

### 프로젝트 내용
- 42Partner는 42Seoul생활을 하면서 학습과 식사를 함께할 파트너를 매칭해주는 프로그램입니다.
- **학습과 식사** 두 카테고리에 대해서 게임처럼 방 생성 혹은 랜덤 매칭을 통해 조건에 맞는 교육생들을 매칭해주는 기능을 제공합니다.

### 프로젝트 기대효과
- 42Seoul과정은 동료학습을 핵심으로 하는 교육과정으로 교육생 교류 정도에 따라 학습 능률이 올라가기 때문에 이에 기여할 것으로 기대 됩니다.
- 식사할 때 배달비도 아끼면서, 동료간에 소통을 할 수 있는 연결 창구가 될 수 있습니다.

## 프로젝트 협업 과정

- 협업 툴로는 Git을 활용, Git Flow 전략 수립. [Gitflow 전략](https://techblog.woowahan.com/2553/)
- Issue template, Pull request convention, commit convention을 정하여 일관된 형식으로 협업 및 정보 공유가 이루어질 수 있도록 함.
- [Git Convention](https://www.notion.so/Github-Convention-2386de41d1de41fabc7b10cf2ab235b0)
- Issue 관리를 위해 Jira와 Gihub Issue를 고민하던중  프로젝트 규모를 고려하여 Github Issue활용.
- Notion을 활용하여 전체적인 프로젝트의 일정관리 및 필요한 문서들을 정리.
- 1주일 단위로 Sprint 진행하여 MVP단위로 개발을 수행.
## 프로젝트 기능 소개
1. 사이트에 로그인이 되어 있지 않을 경우 로그인 페이지로 리다이렉트 됩니다. 로그인이 되어 있는 상태면 식사와 공부 선택 페이지로 이동이 됩니다.
<img width="703" alt="image" src="https://user-images.githubusercontent.com/47822403/234506550-a1a9b6e9-ee50-482d-9600-521961689f54.png">

<img width="703" alt="image" src="https://user-images.githubusercontent.com/47822403/234506478-e9febdfd-2508-4600-be89-53118325718d.png">
2. 랜덤 매칭 페이지에서는 매칭 조건을 입력하여 매칭에 참여할 수 있습니다.
<img width="701" alt="image" src="https://user-images.githubusercontent.com/47822403/234506590-335307ce-5a5a-4d3a-a996-e8756b25e82e.png">
3. 방 매칭 목록 페이지에서는 매칭 참여 방 목록과 조건등이 표현됩니다.
4. 방 매칭 신청 페이지에서는 조건을 입력하여 방을 생성할 수 있습니다..
<img width="425" alt="image" src="https://user-images.githubusercontent.com/47822403/234506868-ad29d362-9390-407b-944a-b3295be1d8eb.png">
5. 방 매칭 상세 페이지에서는 매칭 완료, 신청을 할 수 있고 댓글을 남길 수 있습니다.
<img width="417" alt="image" src="https://user-images.githubusercontent.com/47822403/234507060-232e7dc9-1b77-4f42-9c77-9479f07b3ac5.png">
6. 매칭이 완료되면 Slack을 통해 알림을 받을 수 있습니다.
<img width="709" alt="image" src="https://user-images.githubusercontent.com/47822403/234507261-f2b74d8c-aa5d-4860-bb01-3553e3e554ed.png">


    


