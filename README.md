# 42Partner-Backend

## 프로젝트 내용
- 42Partner는 42Seoul생활을 하면서 학습과 식사를 함께할 파트너를 매칭해주는 프로그램입니다.
- **학습과 식사** 두 카테고리에 대해서 게임처럼 방 생성 혹은 랜덤 매칭을 통해 조건에 맞는 교육생들을 매칭해주는 기능을 제공합니다.

## 사용 기술

### Backend

- Java
- Spring Boot, Spring Data JPA, Spring Security
- JPA, Querydsl
- Junit5, Mockito
- Gradle

### Infra

- AWS EC2, RDS, VPC, ELB, ASG, Code Deploy
- MySQL
- Redis
- Kafka
- Github Action
- Nginx
- Sentry
- Prometheus, Grafana

## Backend API Swagger Link

#### [API 명세 링크](https://api.v2.42partner.com/swagger-ui/index.html#/)

## 배포 구조
### Database Design

<img width="1177" alt="image" src="https://user-images.githubusercontent.com/47822403/234504770-59fcb9bc-1c98-4b42-8eea-4f60d85d7a7a.png">

## Architecture Design
<img width="1031" alt="image" src="https://user-images.githubusercontent.com/47822403/234498210-e681f1b8-fd42-4e7c-a4aa-e6c0a7c534ae.png">

<img width="800" alt="image" src="https://user-images.githubusercontent.com/47822403/234505501-0215da77-36f0-41a1-b5aa-5fbafcf22759.png">


### Architecture Design Point
#### [아키텍처 설계 과정](https://velog.io/@xogml951/AWS-HAHigh-Availability-%EA%B5%AC%EC%B6%95-%EA%B8%B0%EB%A1%9D)

- High Availavility
    - 적어도 둘 이상의 Availavility Zone에 instance가 분포 하도록 합니다.
    - Application Load Balancer를 통해 부하를 WAS로 향하는 요청을 분산시킵니다.
    - Auto Scaling Group을 적용하고 WAS의 Scaling 정책은 Dynamic Scaling cpu50% 이상인 경우로 적용했습니다..
    - NAT Gateway의 경우 각각의 Availability Zone에 적어도 하나 위치시킵니다.
    - RDS의 경우 Multi-az로 설정 하고 하나의 RDS만 Active하도록 설정합니다
        - failover를 위해 stand by DB를 다른 Availability zone에 설정합니다.
        - 여러 RDS를 Active로 설정해도 DB Storage 사이에 병목이 발생할 수 있기 때문에 성능 향상을 보장할 수 없습니다. 조회 부하가 크다면 Replication을 고려하는 것이 낫습니다.
        
- AWS VPC Custom설정(IGW, NAT Gateway, Public/Private/DB Subnet, Route Table)
    - 보안성 강화를 위한 조치
        - WAS를 실행중인 EC2
            - Private Subnet에 위치하게 하여 외부 Network에서의 직접 접근을 방지합니다.
            - NAT gateway를 Route table에 추가하여 EC2에서 외부로 요청 시작(주로 외부 API호출 목적)은 가능하게 합니다.
        - RDS
            - DB Subnet에 위치하게 하여 외부 Network에서의 직접 접근을 방지합니다
            - Private Subnet과의 차이점은 DB Subnet은 NAT Gateway와 연결하지 않습니다.
                - DB에서 외부로 먼저 요청할 일이 없기 때문입니다.
        - Bastion Host
            - Public Subnet에 위치하여 EC2, RDS로 SSH, 3306포트로 접속할 수 있게합니다.
            - Private Subnet에 주요 컴포넌트들을 위치시키고 개발자는 Bastion Host를 통해서 접근합니다.
            - SSH포트만 열어두어야합니다.
- Load Balancer, Auto Scaling Group
    - Load Balancer
        - 외부에서 Web Application Server로 들어오는 요청을 앞단에서 받아 EC2에 적절하게 부하를 분산할 수 있습니다.
        - HTTPS 인증을 수행하고 SSL Termination을 수행하여 WAS부터 내부 통신을 할 때에 HTTP로 내부 통신을 수행함으로서 성능을 개선할 수 있습니다.
    - Auto Scaling Group
        - EC2 Health Check를 통해 비정상적인 EC2 발견시 Termination 합니다.
        - CloudWatch를 통해 서버 부하를 체크하여(예. Dynamic Scaling CPU 사용률 50퍼이상) 필요 시 등록해 놓은 Launch Template을 통해 자동 EC2를 배포할 수 있습니다.
        - 트래픽이 몰리거나 EC2내 장애가 발생했을때 기본적인 자동 Scaling이 가능합니다.
- MSK(Kafka)
  - 서로 다른 Availability zone에 적어도 하나의 Broker가 존재하도록 하고 insync replicas를 2이상으로 설정하여 데이터를 이중화합니다.
- Elasticache(Redis)
  - cluster mode를 활용합니다. cluster mode에서 master node와 slave node가 각각 1, 2개씩(원래 redis cluster에서는 master가 최소 3대이상 있어야 합니다.) 존재하게 되며 master node의 경우 sharding을 통해 추가할 수 있습니다.
- Route53, Certificate Manger
    - Route53을 통해 Domain을 제공 받습니다.
    - AWS Certificate Manger를 통해 SSL 인증서를 발급 받았습니다.
- Nginx
    - Nginx를 Reverse Proxy로 활용합니다.
    - WAS 서버가 정적 파일을 직접 다루는것은 자원 낭비이기 때문에 Web Server를 앞단에 두어 처리하게 하는것이 바람직하다고 판단했습니다.
    - 추가적으로 캐싱, 로드밸런싱, 보안 강화 등의 역할을 할 수 있고 아키텍처 디자인 면에서 유연성을 확보할 수 있습니다.
    - Docker Compose를 활용하여 EC2내부에서 컴포넌트들을 Container화할 계획이 있습니다.

### CICD
* 배포 중 서비스를 정상 동작 시키기 위해 Blue/Green 무중단으로 배포

#### [Blue/Green 무중단 배포 구현 링크](https://velog.io/@xogml951/CICD-%EA%B5%AC%EC%B6%95-Github-action-code-deploy-s3)

<img width="630" alt="image" src="https://user-images.githubusercontent.com/47822403/234505615-16f8b5b8-64a4-494f-9e64-1d40df3e7326.png">


# 개발 과정 상세

# 1. JMeter Perfomance Test와 WAS, DB 옵션 튜닝.

[Perfomance Test와 병목지점 찾기](https://velog.io/@xogml951/Perfomance-Test-Web-Application-Server-%ED%8A%9C%EB%8B%9D)

[Scalability-Test-Thread-Pool-DBCP-적정-설정값-찾기](https://velog.io/@xogml951/Scalability-Test-Thread-Pool-DBCP-%EC%A0%81%EC%A0%95-%EC%84%A4%EC%A0%95%EA%B0%92-%EC%B0%BE%EA%B8%B0)

[상황 설명]

1. JMeter를 활용하여 Article 생성, 조회, 참여, 참여 취소 API를 동시에 요청하도록 시나리오를 만들고 RAMP-UP시간으로 점진적으로 부하를 증가 시킬 수 있는 테스트 환경을 구축. 조회와 수정, 삽입이 발생하는 쿼리가 약 10: 1의 비율로 발생 하도록 설정.
2. 부하를 점진적으로 늘리면서 테스트 → 병목지점 및 원인 파악 → 설정 수정 후 재배포 → 병목 지점 해소 → 부하 늘린 상태에서 다시 테스트 반복 → 아키텍처가 버틸 수 있는 한계지점에서 가용 자원들을 최대로 사용하는 지점 까지 테스트
    1. 최초 JVM, Spring 관련 옵션을 건드리지 않고 배포 → 문제점 발견 및 개선
    2. DBCP, Thread pool을 조정하면서 자원을 최대치로 활용하는 지점 찾기
3. 최종적으로 DB CPU 사용률이 100%에 도달하고 DBCP, Thread pool등 가용 자원을 모두 사용하는 상황에 도달함. TPS가 더 이상 상승하지 않고 요청 응답 시간이 5초로 정상 요청 시간(0.5s내외)에 비해 정상적으로 요청을 처리한다고 보기 힘든 수준에 도달함.
4. 해당 케이스에서 부하 테스트 도중 ASG의 Dynamic Scaling 정책에 따라 WAS가 2배로 증가 되었지만 해당 상황이 개선되지 못함.

[문제점 및 이슈]

1. 처음에는 JVM 옵션을 따로 설정하지 않고 배포하였음 이 경우 다음과 문제 현상이 발생하였음. 
    - JMeter로 부하 테스트 시 DB CPU 사용률, WAS의 Heap 메모리 used 지표등 충분히 더 높은 트래픽을 처리할 수 있음에도 TPS가 증가하지 않고 응답시간이 급격히 증가하는 문제가 발생함.
    - Thread Pool의 스레드 개수가 명확한 제한 없이 상승함.
        - DBCP가 10으로 설정되어있고, WAS는 2대인 상황에서 모든 Connection 사용되고 Connection 대기가 150까지 증가함.  반면 RDS의 CPU부하는 30%정도에 불과함.
    - EC2가 재 실행됨. 이때, JVM 메모리 모니터링 지표, 최종 로그등을 보았을 때 Out Of Memory 등의 심각한 Error는 발생하지 않았음. 응답 시간 증가로 Health Check가 하여 EC2가 재 시작한 것으로 판단됨.
2. 1의 부하테스트 이후 Thread pool Size, DBCP size를 조정하면서 테스트하였고 **병목 지점 확인을 위해 부하를 3배까지 늘렸음.** Thread pool size 18, DBCP 20, WAS 2에서 약 240TPS에 도달하였고, Response time은 5초. **해당 테스트 중 ASG에 의해서 WAS가 2배로 늘었지만 개선되지 않음. DB자원 CPU 사용률은 100% 도달.**

[원인 분석]

1. JVM, Spring, DB관련 옵션을 따로 설정하지 않고 배포할 경우 다음과 같은 문제가 발생할 수 있음.
    - Thread pool과 Thread pool Waiting Queue의 max size를 따로 지정하지 않으면 요청마다 Thread를 생성하는 방식으로 동작홤. 스레드 생성으로 자원이 낭비되고 컨텍스트 스위칭 오버헤드가 증가하며, 심각한 경우 장애 발생 요인이 될 수 있음.
    - DBCP는 10으로 되어있음. DB 성능이 좋더라도 DB Connection이 부족하여 활용을 못할 수 있음.
    - DB 옵션중 max_connections라는 Client 연결 수를 조절하는 옵션이 있는데 RDS의 경우 이 값을 스펙에 따라 동적으로 결정함.(t3.micro의 경우 대략 85 정도)
2. RDS CPU사용률이 100%이고 WAS Scaling이 성능을 개선시키지 못한 것으로 보아 결국 DB에서 병목이 발생한 것으로 판단됨.

[해결 방안]

1. RDS의 max_connection을 AWS에서 동적으로 지정해주는 값을 참고 하여 85으로 지정하고, 최대 4대의 WAS가 각각 DBCP를 20으로 쓰도록 하여 여유분 5개를 둠. Thread pool과 waiting queue가 사용될 수 있도록 Tomcat의 스레드 풀을 설정함.
2. WAS가 병목이 아니었기 때문에 Auto Scaling이 성능 개선을 이루지 못함. DB가 병목인 경우 부하를 줄일 수 있는 방법을 찾아야함. 인덱스, 쿼리등을 개선하거나 Scale out, 캐싱등의 방법이 있음.

[결과]

1. 병목 상태 기준 튜닝후 TPS 4배 상승. 조회 60 → 240TPS , 참여, 조회(수정 API) 6 → 20 TPS

[배운 점]

1. Application을 Default로 설정 없이 배포하는 것이 큰 문제임을 알게 됨.
2. Thread pool, DBCP, 데이터 베이스의 max connection, JVM max heap size 등의 옵션이 Application에 미치는 영향과 실제 병목 상황과 지점을 경험해볼 수 있었음.
3. 해당 옵션들을 튜닝하여 자원들을 충분히 활용하는 설정값을 찾을 수 있었음.
4. ‘결국 DB가 병목이다’ 라는 말이 있듯이 Web Server, WAS등의 다른 요소들에 비해 DB가 상태를 가지고 있다는 특성으로 인해 쉽게 Scale out하기 힘들다는 것을 느끼게 되었고 DB부하를 분산시킬 방법들에 대해서 공부하고 고민하였음.

[추가 개선점]

1. DB 부하를 줄일 방법에 대해서 고민해봐야함. 다음과 같은 방법들이 있음.
    1. RDBMS자체의 성능을 잘 이끌어 낼 수 있는 방향으로 쿼리를 튜닝하거나 구조를 개선한다.
        - 인덱스 적용
        - 록을 최소화 하는 방향으로 개선
        - 반정규화
        - 쿼리 튜닝
    2. RDBMS를 Scale up, Scale out한다.
        - Partitioning
        - Replication(조회부하를 Replica node를 통해 분산시킬 수 있다.)
        - Sharding(Master자체를 늘릴 수 있지만 관계형 데이터베이스는 Sharding에 의한 제한점이 큼.)
    3. Cache를 활용하여 읽기 부하를 분산시킨다.
        - Redis는 Scale out에 유리함. 특히, RDBMS와 다르게 적은 제약으로 Master node를 Sharding 할 수 있음.
2. 현재 JAVA 11을 사용하고 있고 G1 GC가 Default로 사용되고 있는데 Parallel GC로 전환하여 테스트해 볼 필요가 있음. 
3. EC2 t3.micro, RDS t3.micro 옵션의 적정성을 생각해 보고 스펙을 조정해 볼 필요가 있음. 특히, EC2의 경우 t3.micro 4대를 쓰는 것보다 같은 비용으로 CPU는 동일하지만 RAM은 2배인 t3.small 2대를 사용하는 것이 나을 수 있음.
4. EC2내 JVM의 CPU사용률이 100%에 도달하였음에도 EC2 CPU 사용률은 40%정도까지 밖에 증가하지않음. JVM 실행 옵션을 통해 CPU 할당량을 늘려야할 필요가 있음. 

# 2. 분산 캐시(Redis) 활용 DB부하 완화 및 병목 해결. 동일 테스트 조건

분산 캐시 적용 및 부하 테스트 DB 병목 개선 여부 확인

[캐시 종류 및 전략 별 트레이드 오프](https://velog.io/@xogml951/%EC%BA%90%EC%8B%9C-%EC%A0%84%EB%9E%B5%EA%B3%BC-%EA%B0%9C%EB%85%90)

[Redis 기본 내용](https://velog.io/@xogml951/Redis%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%95%8C%EC%95%84%EB%B4%85%EC%8B%9C%EB%8B%A4feat.%EA%B4%80%EA%B3%84%ED%98%95-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4%EC%99%80%EC%9D%98-%EC%B0%A8%EC%9D%B4%EC%A0%90)

[Redis 백업](https://velog.io/@xogml951/Redis-%EB%B0%B1%EC%97%85%EA%B3%BC-%EC%9E%A5%EC%95%A0-%EB%B3%B5%EA%B5%AC)

[Redis replication Sentinel vs Cluster](https://velog.io/@xogml951/Redis-replicationSentinel-Cluster)

[문제점 및 이슈]

1. 부하 테스트를 하며 DBCP, Thread pool등을 튜닝 하던 중 DB자체에 병목이 발생함. 

[해결 방안]

1. 조회 수정 요청이 약 10:1로 발생하기 때문에 조회 부하 분산을 위해 캐시를 활용하고 주요 선택 사항들을 트레이드 오프를 고려하여 다음과 같이 결정함.
    1. 분산 캐시
    2. write-around(DB에만 쓰고 Cache에는 쓰지 않음.)
    3. Redis
    4. TTL 5초(Cache데이터가 최대 5초 동안은 갱신 되지 않을 수 있지만 Cache 데이터는 방 목록 조회에서만 활용되고 Article 수정이 될만한 API가 호출 되고 난 후 방 상세 페이지로 넘어가기 때문에 사용자가 체감하기 어려움.)
    5. cache-aside(lazy loading)
    6. Least Frequently Used 

[배운 점]

1. 다양한 캐시 전략, Eviction 정책과 분산 캐시, 로컬 캐시, 분산 캐시 종류등의 장단점과 트레이드오프에 대해서 이해할 수 있었음.
2. 캐시를 통한 조회 부하 분산을 통해 DB 병목 해결을 경험해볼 수 있었음.
3. 여러 WAS인 경우 cache에 동시에 쓰는 경우가 발생할 텐데 Redis는 Single Thread이지만 동시성 문제는 발생하기 때문에 트랜잭션, Watch, 분산락 등에 대해서 추가적으로 학습하였음.

[추가 개선점]

1. 방 매칭의 참여자 수와 같은 데이터의 경우 일관성이 매우 중요해 db에서는 락을 사용해 수정해야함. row level의 락이 걸리기 때문에 해당 컬럼을 따로 분리하여 락 적용 범위를 줄일 필요가 있어 보임.
2. Consistent Hashing, Hot key와 같은 Redis 성능과 관련된 주요 내용에 대해서 추가 학습 필요.
3. @Cacheput적용 시 DB 트랜잭션 롤백 될 경우 어떤 식으로 동작하는지 체크해봐야함.

# 3. GenerationType.AUTO Entity insert 시 발생하는 HikariCP Deadlock 트러블 슈팅

[DBCP-Deadlock-트러블-슈팅과-GenerationType.AUTO](https://velog.io/@xogml951/DBCP-Deadlock-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85%EA%B3%BC-GenerationType.AUTO)

[문제점 및 이슈]

1. Thread pool과 DBCP을 조정하여 JMeter로 Scalability Test를 진행하던 중 DB connection을 획득하지 못하고 Timeout 하는 현상 발생.

[원인 분석]

1. @GeneratedValue(strategy = GenerationType.AUTO)와 Mysql을 같이 사용하는 경우 insert 시 사용할 id를 생성하기 위해 hibernate_sequence를 사용하고 락을 걸어 값을 update하기 때문에 별도의 connection과 트랜잭션을 진행함. 즉, 하나의 thread에서 2개 이상의 DB Connection을 요구하는 경우가 발생함.
2. 1번 사항 때문에 Thread pool Size가 DBCP Size보다 크거나 같으면 DB Connection 자원에 대해서 DeadLock이 발생할 수 있음.

[해결 방안]

1. GenerationType.AUTO를 GenerationType.Identity로 변경
2. DBCP size를 Thread pool Size보다 충분히 크게 두기. 이 경우 DBCP를 thread pool size보다 너무 크게 하면 idle connection이 늘어날 수 있고 반대로 약간 더 크게 하면 DB connection을 대기 받기 위한 시간이 길어질 확률이 높음.

[배운 점]

1. 예상치 못하게 DB Connection을 2개이상 요청하는 경우가 있을 수 있음을 알게 되었음.
2. GenerationType에 따른 동작 방식과 장단점을 이해할 수 있었음.

# 4. ec2_sds_config, relabel_configs를 활용하여 Prometheus, Grafana  성능 모니터링 구축.

[AWS-Auto-Scaling-Group-EC2-WAS-대상Actuator-Prometheus-Grafana-Micrometer-모니터링-적용](https://velog.io/@xogml951/AWS-Auto-Scaling-Group-EC2-WAS-%EB%8C%80%EC%83%81Actuator-Prometheus-Grafana-Micrometer-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%A0%81%EC%9A%A9)

[문제점 및 이슈]

1. ASG로 관리되는 EC2를 Prometheus로 모니터링할 때 EC2가 새로 배포된 경우 모니터링 대상에 자동으로 포함 되지 않는 문제가 발생함.
2. JVM과 Thread pool, DBCP의 개념을 학습하게 되면서 AWS상에 배포한 주요 시스템 설정들을 Default상태로 배포한 것과 모니터링 하지 않는 것에 문제의식을 느낌.

[원인 분석]

1. Prometheus의 scrape_config시 ASG로 관리되는 EC2들의 Private IP가 동적으로 변경될 수 있는데 static 한 IP지정 방식을 사용했기 때문에 자동 수정되지 못함.

[해결 방안]

1. Spring Actuator를 통해 제공되는 Metric을 Micrometer를 통해 Prometheus에게 맞는 형태로 제공하고 Grafana를 통해 수집된 지표를 시각화하여 모니터링 할 수 있는 환경을 구축함.
2. ec2_sds_config를 통해 WAS가 배포된 EC2의 Private Ip를 동적으로 수집하고 relabel_configs를 통해 대상 IP주소가 변경 될 수 있도록함.

[배운 점]

1. Spring Actuator, Micrometer, Grafana, Prometheus를 활용하여 서버 지표를 수집하고 모니터링하는 플로우를 알 수 있었으며 구축경험을 할 수 있었음 .
2. WAS를 설정하지 않고 배포 할 시 Heap size, Thread pool, DBCP등이 어떤 값으로 지정되는지 알 수 있었고 이에 대한 문제의식을 가지게 되었음.

[추가 개선점]

1. Prometheus에 지표가 쌓여 storage 용량이 부족할 경우를 대비하여야함.

# 5. 단위 테스트 무분별한 Test Double 사용 시 리팩토링 내성 하락 등의 Trade Off를 고려하여 작성, 컨트롤 할 수 없는 코드 영향 최소화.

[Test-Double-사용에-따른-Trade-Off와-Service-Layer-단위-테스트](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-Test-Double-%EC%82%AC%EC%9A%A9%EC%97%90-%EB%94%B0%EB%A5%B8-Trade-Off%EC%99%80-Service-Layer-%EB%8B%A8%EC%9C%84-%ED%85%8C%EC%8A%A4%ED%8A%B83)

[JUnit5-Mockito와-Spring-boot-테스트-코드-작성법-총-정리](https://velog.io/@xogml951/JUnit5-Mockito%EC%99%80-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EB%B2%95-%EC%B4%9D-%EC%A0%95%EB%A6%AC-1)

[Repository Layer- 단위테스트](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EB%B2%95-Repository-Layer-%EB%8B%A8%EC%9C%84%ED%85%8C%EC%8A%A4%ED%8A%B8-%EB%B0%A9%EB%B2%952)

[Controller-Layer-단위-테스트와-인증-인가-테스트](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-Controller-Layer-%EB%8B%A8%EC%9C%84-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%ED%85%8C%EC%8A%A4%ED%8A%B8-4)

[테스트-커버리지와-Jacoco](https://velog.io/@xogml951/JUnit5%EA%B3%BC-Spring-boot-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BB%A4%EB%B2%84%EB%A6%AC%EC%A7%80%EC%99%80-Jacoco-5)

[문제점 및 이슈]

1. Mocking한 Repository 객체의 인터페이스가 변하는 경우 Service Layer test에서 거짓음성이 대량 발생.
2. Service Layer 메서드 내부에서 LocalDateTime.now()이 호출되는 경우 호출 시점에 따라 테스트 결과가 달라짐.

[원인 분석]

1. 단위 테스트에서 Mocking을 하면서 테스트 메서드의 구현을 테스트하는 성격을 띠게 되고 연관 객체의 인터페이스에 강하게 의존하면서 리팩토링 내성이 저하됨.
2. LocalDateTime.now()는 컨트롤할 수 없는 코드이기 때문에 Service 내부에서 호출될 경우 Service는 테스트 불가능한 코드가 됨.

[해결 방안]

1. 제어할 수 없는 코드인 경우에만 Test Double을 사용하고 DAO 등은 Repository Layer 단위 테스트에서 충분히 검증하고 Mocking하지 않도록 함.
2. LocalDateTime.now()를 Controller에서 호출하고 Service에는 변수로 넘겨주는 방식으로 변경. 테스트할 수 없는 코드가 존재하게 되면 해당 코드 위치부터 상위에 위치한 모듈들도 테스트하기 힘들게 됨.

[배운 점]

1. 리팩토링 내성, 회귀 방지, 빠른 테스트의 요소가 중요하다는 점을 인지하고 각 요소가 배타적인 성격을 가지고 있기 때문에 테스트 코드 작성 방식에 따른 트레이드 오프를 알게 되었음. 우선순위를 리팩토링 내성, 회귀 방지, 빠른 테스트 순으로 만족하게 하려고 노력함.
2. Test Double을 남발할 경우 인터페이스가 변경되면 거짓 음성이 발생하고 리팩토링 내성이 저하되기 때문에 외부 API 요청, 무작위 성격의 로직 등 테스트 시 컨트롤할 수 없는 모듈인 경우에만 적용하는 것이 좋다고 생각함. 
3. 제어할 수 없는 외부 API 요청, 무작위 성격의 로직 등의 로직은 최대한 상위 모듈 쪽에서 호출되도록해야함. 이와 같은 요소가 하위 모듈에 있을 경우 해당 모듈에 의존하는 상위 모듈들이 모두 테스트하기 힘들어지기 때문.

# 6. 책임 주도 설계 리팩토링을 통해 랜덤 매칭 조건에 따른 분기 문 제거, Domain Model에 비지니스 로직 응집,  오브젝트(조영호) 정리

[오브젝트조영호-정리](https://velog.io/@xogml951/%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8%EC%A1%B0%EC%98%81%ED%98%B8-%EC%9A%94%EC%95%BD-%EC%A0%95%EB%A6%AC)

[비지니스 로직을 Domain Model 응집하는 형태로 리팩토링](https://velog.io/@xogml951/%EB%B9%84%EC%A7%80%EB%8B%88%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%9D%84-Service%EC%97%90%EC%84%9C-Domain-Model%EB%A1%9C-%EC%98%AE%EA%B8%B0%EA%B8%B0-%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8%EC%A1%B0%EC%98%81%ED%98%B8-2)

[책임주도설계로 리팩토링](https://velog.io/@xogml951/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EB%A5%BC-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A4%91%EC%8B%AC%EC%84%A4%EA%B3%84%EC%97%90%EC%84%9C-%EC%B1%85%EC%9E%84%EC%A3%BC%EB%8F%84%EC%84%A4%EA%B3%84%EB%A1%9C-%EB%B3%80%EA%B2%BD%ED%95%B4%EB%B3%B4%EC%9E%90)

[문제점 및 이슈]

1. 랜덤 매칭의 경우 식사, 학습 영역에 따라 비지니스 로직에서 분기 문이 발생함. 만약 운동이라는 영역이 추가된다면 모든 로직에 운동 관련 분기 추가되어야 함. 현재 코드는 변경과 확장에 매우 취약한 코드임.
2. Controller에서 Dto 매핑시에도  식사, 학습 영역에 따라 다형성을 활용하지 못하고 분기가 발생함.
3. Service Layer에 Bussiness 로직을 직접 작성하는 것보다 가능하다면 Domain Model Entity, VO 등의 상태를 가지고 있는 객체에 책임을 응집하는 것이 좋다는 것을 알게 됨.

[원인 분석]

1. 식사와 학습이라는 데이터 타입을 저장하는 방식으로 객체를 설계하였기 때문에(데이터 주도 설계) 해당 데이터를 확인하는 분기 문이 공통으로 발생함.
2. 랜덤 매칭 신청 시 변환되는 Dto에서 식사, 학습이 필드 형태로 매핑되기 때문에 분기가 발생함.

[해결 방안]

1. 상속을 사용하지 않고 매칭 조건을 매칭 신청 객체가 Composition으로 가지게 하고 매칭 조건 인터페이스에 의존하도록 설계함. 다형성을 활용하여 식사, 학습에 해당하는 매칭 조건을 구현하고 동적으로 객체가 같은 메시지에 대해서 자율적으로 책임질 수 있도록 코드를 수정함. 분기 문을 삭제할 수 있었음.
2. @JsonTypeInfo, @JsonSubTypes을 활용하면 요청 Body가 Controller에서 변환될 때 알맞은 sub type의 객체가 생성되도록 할 수 있으며 이렇게 하면 다형성을 활용해 분기를 삭제할 수 있음.
3. JPA의 Cascade Option, Dirty Checking등의 기능의 도움을 받아 Service Layer에 있던 비지니스 로직을 최대한 Domain Model 내로 이동시킴.

[배운 점]

1. 문제 해결 과정에서 ‘오브젝트’라는 책을 읽게 되었는데 좋은 객체지향 설계는 객체가 스스로 자신의 상태에 대해서 책임지고 이러한 객체들이 서로 협력하는 방향으로 구성된 것이며 다형성을 활용하여 같은 메시지에 다르게 책임을 질 수 있도록 하는 것임을 알게 되었음
2. Solid 원칙과 객체지향의 4대 요소들이 왜 중요한지 깊이 있게 이해할 수 있었고 이러한 이해를 리팩토링 과정에 적용할 수 있었음.
3. 상속을 사용하면 안되는 상황에 사용 시 생기는 문제와 코드 재사용을 위해서는 Composition을 활용해야함을 배웠음.

# 7. Kafka acks:all, min.insync.replicas 설정 주의점, producer Idempotence를 위한 설정, RR방식 배치 성능 문제등 초기 설정 주의점 학습.

[Apache Kafka-개념과-기본-세팅](https://velog.io/@xogml951/Kafka-%EA%B0%9C%EB%85%90%EA%B3%BC-%EA%B8%B0%EB%B3%B8-%EC%84%B8%ED%8C%85)

[Apache Kafka 초기설정 주의점](https://velog.io/@xogml951/Apache-Kafka)

[문제점 및 이슈]

1. Kafka  produce, consume이 로직 중 무조건 단 한번만 실행 되어야 하는 것이 보장 되어야 하는 경우에 대해서 찾아보던 중 이를 위한 초기 설정 주의 사항들에 대해서 알게 되었음.

[해결 방안]

1. 해당 설정 주의사항 중 필요한 부분을 프로젝트에 적용하였음.

[배운 점]

1. Produce 시 acks:all옵션을 사용하여 여러 Partition에 event를 저장하고 싶다면 min.insync.replicas를 2이상으로 두어야함. 그렇지 않으면 디폴트가 1이기 때문에 leader parition에만 produce하고 ack를 받게됨.
2.  max.in.flight.requests.per.connection(성능을 위해 produce시 이벤트를 배치로 특정 개수만큼 모아서 보내는 개수)가 2이상일 경우 retry시 produce 순서가 보장 되지 않을 수 있는데 단 한번만 Event가 produce되길 원하며 produce 순서가 보장되길 원한다면
    - enable.idempotence를 true
    - retry 1이상
    - max.in.flight.requests.per.connection은 5(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION_FOR_IDEMPOTENCE)이하로 유지해아함.
3. 파티션 개수는 처음에 잘 정해야함. 파티션 개수는 줄일 수 없으며 늘릴 수는 있지만 늘릴 경우 key를 지정해도  Hashing이 파티션 개수 변화에 영향을 받아 같은 파티션에 produce가 보장 되지 않음.
4. Kafka produce시 Default로 사용되는 Round-Robin방식은 파티션에 최대한 고르게 이벤트를 produce하기 때문에 배치 처리의 관점에서 성능 저하 요인이 될 수 있음. 그 이유는 consumer는 한번에 하나의 파티션에서 특정 batch size로 consume하기 때문에 고르게 분포되면 한번에 consume할 수 있는 이벤트 개수가 줄어듬. 해당 문제를 배치 produce시 하나의 파티션에 produce하는 Sticky Partitioner를 통해 개선할 수 있음.

# 8. SSE, Redis Pub/Sub을 통해 Polling 방식의 알림 기능 개선, Kafka를 통해 핵심 로직의 알림 의존성 제거.

[SSE Redis pubsub Kafka로 알림 기능개선](https://velog.io/@xogml951/Server-Sent-EventsSSE-Redis-pubsub-Kafka%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EA%B8%B0%EB%8A%A5-%EA%B0%9C%EC%84%A0%ED%95%98%EA%B8%B0)

[Apache Kafka-개념과-기본-세팅](https://velog.io/@xogml951/Kafka-%EA%B0%9C%EB%85%90%EA%B3%BC-%EA%B8%B0%EB%B3%B8-%EC%84%B8%ED%8C%85)

[Apache Kafka 초기설정 주의점](https://velog.io/@xogml951/Apache-Kafka)

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/cd06fac7-d493-4d40-a73d-71ce304ba579/Untitled.png)

[문제점 및 이슈]

1. 알림 기능 추가 후 클라이언트의 Polling으로 인해 서버 부하가 크게 증가함.
2. 이를 해결하기 위해 Server Sent Event를 활용하였지만 알림 시에도 SSE 응답이 오지 않는 경우가 자주 발생함.
3. 알림 기능의 추가 후 매칭 확정, 댓글 작성 등의 응답시간이 증가하고 알림에 의존하는 구조가 됨.
4. 알림에 대한 의존성을 제거하기 위해 Kafka를 활용하는 경우 redis pub이 무수히 생성되는 문제가 발생할 수 있음.
5. 알림 연결이 바로 종료되는 경우가 생기고 실시간성이 지나치게 떨어지는 경우가 발생.

[원인 분석]

1. 알림의 특성상 생성 시 사용자에게 이를 노출할 필요가 있기 때문에 브라우저에서 Polling을 하는 구조로 되어있는데, 이 때문에 유저가 많아질수록 Server의 부하가 크게 증가함.
2. WAS를 Scale Out 하여 구성하는 경우 특정 WAS와 클라이언트가 SSE 연결을 하므로 알림을 생성한 이후의 요청이 같은 WAS에 오지 않는다면 SSE 응답을 보낼 수 없음.
3. 매칭 확정, 댓글 작성 등의 요청이 오면 알림도 생성되어야 하는데, 알림 쪽에 문제가 발생하면 핵심 기능도 실패할 수 있음. 또한, 알림이 정상 동작 하더라도 핵심 기능과 알림이 동기적으로 완료되어야 응답할 수 있기 때문에 Response Time이 증가함.
4. 알림 Event의 Consumer Group이 하나로 정의 되어있고 Alarm Event 발생 시 Redis channel에 pub, Database에 Alarm 저장 두 역할을 모두 수행함. Alarm 저장 트랜잭션이 롤백 되더라도 Redis pub은 이미 보내져 있고 Kafka Consumer의 Commit이 되지 않기 때문에 Redis pub만 여러 번 실행됨.
5. Nginx를 Reverse Proxy로 사용하는 경우 Upstream 요청 시 HTTP/1.0 버전을 사용함. 이 경우 SSE의 HTTP/1.1의 지속 연결이 적용되지 않기 때문에 연결이 바로 끊어질 수 있음. 또한, Nginx는 Buffering 기능이 있기 때문에 SSE의 실시간성이 떨어질 수 있음.

[해결 방안]

1. Server Sent Event를 활용하여 알림이 생성될 경우 WAS에서 Client로 이를 응답 하여 알려주기 때문에 Polling을 제거하였고 불필요한 API 요청을 줄일 수 있었음.
2. SSE 응답을 보내야 하는 경우 Redis Channel에 pub을 하고 모든 WAS들은 해당 채널에 sub을 하며 pub시 In-memory SSEmitterRepository에서 SSE 연결 객체를 찾아 연결된 WAS가 응답할 수 있도록 함.
3. 매칭 확정, 댓글 작성 트랜잭션이 성공하면 Kafka Alarm Event를 발행하는 방식으로 변경하면 매칭 확정, 댓글 작성은 Alarm과 상관없이 응답할 수 있으며 Alarm을 비동기적으로 저장하여 Response time을 줄일 수 있음. 또한, Consume하는 부분에서 Alarm 생성 로직을 공통화 할 수 있기 때문에 코드적인 중복도 최소화할 수 있음.
4. Redis pub/sub Consumer Group, Alarm 생성 Consumer Group으로 분리함. 각각이 Offset이 다르게 적용되기 때문에 코드 작성 오류로 인한 Consumer의 Commit이 의도한 대로 동작하지 않는 문제를 해결할 수 있었음.
5. Nginx를 1.1 버전으로 설정하고 X-Accel-Buffering: no를 통해 버퍼링 기능을 부분적으로 제한함.

[배운 점]

1. Polling 방식의 비효율성을 SSE로 해결할 수 있음을 배웠음.
2. Redis pub/sub과 Kafka의 차이를 이해하고 각각의 동작 방식을 이해하였음.
3. Kafka를 사용하는 이유와 목적에 대해서 알 수 있었음.
4. Kafka를 사용하기 위해 필수적으로 설정해 주어야 하는 옵션들과 Kafka 내부 동작 방식에 대해서 학습하고 이해할 수 있었음.
5. Nginx를 Reverse Proxy로 활용할 때 생길 수 있는 영향에 대해서 생각해 볼 수 있었음.

# 9. 트랜잭션 분리 및 Kafka로 매칭 알고리즘 실행 방식 개선

[랜덤 매칭 트랜잭션 분리 Kafka 기능 개선](https://velog.io/@xogml951/%EB%9E%9C%EB%8D%A4-%EB%A7%A4%EC%B9%AD-polling-%EB%B0%B0%EC%B9%98-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EB%B6%84%EB%A6%AC-%EB%B0%8F-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%814)

[문제점 및 이슈]

1. 한 명의 사용자만 매칭을 취소하더라도 전체 매칭 알고리즘이 롤백 됨.
2. 매칭 알고리즘의 정렬과 같은 CPU 연산시간이 긴 작업에서 DB Connection이 필요 없음에도 소모함.
3. 매칭 신청 시 매칭 알고리즘이 완료되어야 응답을 할 수 있음.
4. 매칭 알고리즘이 여러 스레드에서 동시에 실행될 수 있음. 
5. 매칭 알고리즘이 실패하더라도 재실행하지 못함.
6. 매칭 신청 취소 시 영속성 컨텍스트 Dirty Checking을 활용할 경우 update 쿼리가 대량으로 발생.

[원인 분석]

1. 트랜잭션이 하나로 되어있음.
2. 매칭 신청 API 호출 시 해당 요청이 매칭 알고리즘에 의존하여 동기적으로 처리됨.
3. 영속성 컨텍스트 Dirty Checking을 활용할 경우 Entity 개수만큼 update 쿼리가 발생함.

[해결 방안]

1. 트랜잭션을 분리해야 함. 매칭 신청 조회 트랜잭션을 readOnly=true로 실행하고 매칭 조건으로 정렬하고 매칭 그룹을 묶는 작업은 트랜잭션 밖에서 수행한다음, 매칭 그룹 개수만큼 트랜잭션을 실행하고 commit 시점에 조회의 Version이 다름이 확인되면 해당 트랜잭션만 롤백함.
2. 매칭 신청 API요청 시 매칭 신청이 완료되면 매칭 알고리즘 event를 produce하는 것으로 개선. 이렇게 할 경우 매칭 알고리즘 수행 완료와 상관없이 매칭 신청이 이루어질 수 있음. 
3. Kafka event produce 시 key값을 지정하여 하나의 파티션에만 생성되도록 보장하면 동시에 하나의 Consumer에서 매칭 알고리즘을 실행하도록 할 수 있음. 이것이 유지되기 위해서는 partition의 개수가 변경되어서는 안됨. 
4. bulkUpdate쿼리를 직접 작성하여야함. 이때, 영속성 컨텍스트와 무관하게 DB에 직접 반영되기 때문에 영속성 컨텍스트를 초기화 해야함.

[배운 점]

1. 트랜잭션을 적절히 분리하고 Version을 통해 낙관적락을 직접 구현해봄.
2. Kafka의 partition과 consumer의 상관 관계, key값 지정 시 특정 partition에만 저장되며 parition 개수 변경과 관련된 주의사항을 배움. 
3. update쿼리를 직접 작성하는 것과 Dirty Checking방식의 차이를 알게 됨.

# 10. 낙관적 락과 AOP를 활용해 방 매칭 DB 동시성 문제 해결

[Optimistic Lock과 AOP활용 방 매칭 동시성 문제 해결](https://velog.io/@xogml951/API%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%EA%B8%B0%EB%A1%9D-Optimistic-Lock%EA%B3%BC-AOP%ED%99%9C%EC%9A%A9)

[트랜잭션 Isolation개념 총 정리](https://velog.io/@xogml951/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-Isolation%EC%B4%9D-%EC%A0%95%EB%A6%AC)

[문제점 및 이슈]

1. 방 매칭에서 참여 인원 제한이 있음에도 동시에 서로 다른 사용자가 매칭을 신청할 경우 참여 인원보다 많은 인원이 참여해 버리는 문제가 발생

[원인 분석]

1. MYSQL Repetable Read 격리 수준에서 트랜잭션 이상 현상 lost update가 발생

[해결 방안]

1. 비관적 배타 록을 고려했으나 충돌 상황이 자주 발생하지 않고 수정 시점에 조회가 불가능해져 성능이 저하될 수 있기 때문에 낙관적록을 활용.
2. 또한, 충돌이 발생하여 요청이 취소되면 Retry 하는 관심사가 공통으로 적용되어야 하므로 AOP를 활용.

[배운 점]

1. 트랜잭션 Isolation과 관련된 Serializability, Recoverbility, 이상현상, 격리 수준 등의 개념과 MYSQL이 격리 수준에 따라 어떤 방식으로 동작 하는지 알게 되었음.
2. 낙관적 락과 비관적 락의 동작 방식과 장, 단점을 고려해 선택할 수 있게 되었음.
3. AOP가 Decorator Pattern, CGLIB, 빈 후처리기 등과 같은 기술들을 통해 어떻게 프록시 객체를 생성하고 적용되는지 이해하였음.
4. AOP를 통해 공통적으로 적용해야 하는 부가 로직을 효과적으로 처리할 수 있음을 경험했음.

# 11. Paging Fetch Join문제 해결 및 N+1 문제를 해결하기 위한 다양한 방식 이해 및 적용

[N+1문제 해결 방식과 장단점 총 정리](https://velog.io/@xogml951/JPA-N1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%EC%B4%9D%EC%A0%95%EB%A6%AC)

[문제점 및 이슈]

1.  페이징이 포함된 Select 쿼리에 Fetch Join을 사용했을 때 warn log가 발생.

[원인 분석]

1. 페이징이 포함된 Select 쿼리에서 Fetch Join을 사용할 경우 페이징을 위해 전체 테이블 데이터를 모두 메모리에 로딩해야 하며 OutOfMemory와 같은 심각한 장애 발생 가능.

[해결 방안]

1. 페이징이 포함된 Select 쿼리는 fetch join하지 말고 default_batch_fetch_size옵션 활용.
2. 일반Join을 하고 Dto로 바로 변환하여 조회하는 방식도 있음. 

[배운점]

1. 다양한 N+1 문제의 해결법과 각각의 장단점, 제약사항에 대해 배우고 선택할 수 있게 되었음.
2. fetch join
    1. 장점: 네트워크 쿼리 전송 횟수 감소
    2. 단점: 컬렉션 연관관계(1:N)를 가진 경우 paging제한, 컬렉션 하나까지만 fetch join 가능, 1:N관계에서 1쪽의 데이터가 join 시 중복으로 존재하기 때문에 불필요한 데이터 통신량 증가등의 문제가 있고 조회 메서드가 각각 정의해야함.
3. default_batch_fetch_size
    1. 장점: 컬렉션 관계에 대해서 제한이 없으며 join을 하지 않기 때문에 데이터 전송량이 감소하고 Entity그래프 탐색을 통해 손쉽게 코드를 작성할 수 있다는 장점이 있음. 
    2. 단점: fetch join대비 쿼리 전송 수가 많음.
4. 일반 Join을 하고 Dto로 바로 변환
    1. 장점: Entity Column이 많을 때 Projection하여 특정 컬럼만 조회할 수 있음, 커버링 인덱스 활용가능성 상승.
    2. 단점: 영속성 컨텍스트와 무관하게 동작하고 Repository가 Dto에 의존하게 되기 때문에 API변경에 DAO도 수정되어야 할 수 있음.
5. 결론
    1. 1:1 연관관계는 최대한 fetch join을 활용하고 컬렉션 연관관계는 default_batch_fetch_size활용.
    2. 많은 컬럼 중 특정 컬럼만 조회해야 할 경우나 커버링 인덱스를 활용하고 싶은 경우 데이터 전송량을 줄이고 싶으면 일반 Join을 하고 Projection하여 Dto로 바로 변환. 다만 이 경우 DAO객체를 분리하여 작성하는 것이 좋음.

# 12. Session 방식 JWT로 개선

[Refresh-Token을-어디에-저장해야-할까Feat.-XSS-CSRF-CORS](https://velog.io/@xogml951/Refresh-Token%EC%9D%84-%EC%96%B4%EB%94%94%EC%97%90-%EC%A0%80%EC%9E%A5%ED%95%B4%EC%95%BC-%ED%95%A0%EA%B9%8CFeat.-XSS-CSRF-CORS)

[OAuth 개념](https://velog.io/@xogml951/Spring-Security-OAuth-JWT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B3%BC%EC%A0%95-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%ED%98%84-%EC%B4%9D-%EC%A0%95%EB%A6%AC1-OAuth-%EA%B0%9C%EB%85%90%EA%B3%BC-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%B0%8F-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%EC%97%90-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0)

[JWT개념과-회원가입-및-로그인-로직-구현1](https://velog.io/@xogml951/Spring-Security-OAuth-JWT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B3%BC%EC%A0%95-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%ED%98%84-%EC%B4%9D-%EC%A0%95%EB%A6%AC2-JWT%EA%B0%9C%EB%85%90%EA%B3%BC-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%A1%9C%EC%A7%81-%EA%B5%AC%ED%98%84)

[OAuth-JWT-회원가입-및-로그인-로직-구현2](https://velog.io/@xogml951/Spring-Security-OAuth-JWT%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B3%BC%EC%A0%95-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EA%B5%AC%ED%98%84-%EC%B4%9D-%EC%A0%95%EB%A6%AC3-OAuth-JWT-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%A1%9C%EC%A7%81-%EA%B5%AC%ED%98%84)

[문제점 및 이슈]

1. Session 방식의 인증을 활용할 때 AWS ELB와 여러 WAS를 구성하면 제대로 로그인 처리가 되지 않는 문제 발생.

[원인 분석]

1. In-memory에 Session을 저장하기 때문에 어떤 WAS로 요청이 도달하느냐에 따라서 인증이 되지 않은 것으로 동작함.
2. Redis Clustered Session을 구성하는 경우 인증은 제대로 처리되지만 매 API 요청마다 Redis를 조회해야 하는 비용이 있고 향후 앱으로 개발할 경우 호환이 안 될 수 있는 문제가 있음.

[해결 방안]

1. JWT방식으로 인증 정보 자체를 Token에 포함시켜 Stateless 하게 유지.
2. Token 탈취 시 이를 무력화할 수 없다는 단점이 있지만 Access Token 만료 시간을 5분 정도로 짧게 유지하고 Refresh Token으로 재발급하도록 함.

[배운 점]

1. WAS를 Stateless 하게 유지하지 못할 경우 꼭 Session이 아니더라도 Scale Out시 문제가 발생할 수 있다는 것을 깨닫게 됨.
2. Refresh Token 저장 위치를 고민하고 근거를 찾는 과정에서 Local Storage의 경우 JavaScript 코드를 통해 조작할 수 있기 때문에 XSS공격에 취약하지만, Cookie의 경우 httpOnly를 통해 이를 방지할 수 있고 반대로 CSRF에는 취약함을 알게 되었음.
3. JWT의 구조와 Secret key 단순 설정과 같은 보안 관점의 잘못된 구현 방식에 대해서 배울 수 있었음.
4. Session, JWT의 차이와 장단점에 대해서 배울 수 있었음.
5. OAuth 2.0의 동작 방식에 대해서 배울 수 있었음.

# 13.  AWS High Available, Scalable Architecture 구성

[AWS-HAHigh-Availability-구축](https://velog.io/@xogml951/AWS-HAHigh-Availability-%EA%B5%AC%EC%B6%95-%EA%B8%B0%EB%A1%9D)

[Redis Sentinel vs Cluster](https://velog.io/@xogml951/Redis-replicationSentinel-Cluster)

[Kafka-개념과-내부구조](https://velog.io/@xogml951/Kafka-%EA%B0%9C%EB%85%90%EA%B3%BC-%EA%B8%B0%EB%B3%B8-%EC%84%B8%ED%8C%85)

[문제점 및 이슈]

1. AWS Availability Zone에 자체에 장애가 발생하면 그 위에 구축한 서비스도 장애가 발생할 수 있음.
2. 부하 증가나 예상치 못한 문제로 WAS가 종료되거나 MYSQL, Redis, Kafka 등에 장애가 발생하면 서비스가 정상 운영되지 못하고 자동복구 되지 못함. Redis의 경우 In-memory DB이기 때문에 최악의 경우 데이터가 유실 될 수 있음.
3. WAS, RDS등의 요소들은 Public Subnet에 존재할 경우 보안성이 떨어짐.

[원인 분석]

1. 시스템의 요소들이 하나의 Availability Zone에만 분포하고 이중화 되지 않음.
2. Default VPC를 사용하여 Private Subnet이 정의 되지 않음.

[해결 방안]

1. 시스템의 요소들이 모두 이중화 되어야 하며 적어도 둘 이상의 Availability Zone하도록 구성함.
2. WAS의 경우 ELB, ASG를 통해 부하 분산, Health check, 자동 복구가 될 수 있도록 하고 RDS는 Stand By 구성, Redis는 Cluster구성을 통해 Replica를 두고, Kafka는 Broker를 둘 이상 두고 In Sync Replica를 2이상으로 둠.

[배운점]

1. AWS HA 구성 이유와 VPC, ELB, ASG, RDS, MSK, ElastiCache등의 서비스의 설정 방법과 이유에 대해서 알 수 있었음.
2. RDB, Redis, Kafka가 가용성 확장성을 확보하는 원리와 관계형 데이터베이스와 NOSQL의 차이에 대해서 배울 수 있었음.

# 14. Blue/Green 무중단 배포 자동화

[Github action code deploy s3를 통한 Blue/Green무중단 배포](https://velog.io/@xogml951/CICD-%EA%B5%AC%EC%B6%95-Github-action-code-deploy-s3)

[문제점 및 이슈]

1. 배포 시 서비스가 일정 시간 중단 되어야 함.
2. Github Action으로 빌드 시 application.yml을 전달할 수 없음.

[원인 분석]

1. 배포를 EC2 터미널로 접속하여 수동으로 하기 때문에 서버 재 실행 시 서비스를 종료해야하고 개인의 실수나 소통 부재 시 배포가 의도한 대로 되지 않을 수 있음.
2. application.yml은 .gitignore에 등록되어 있기 때문에 github에 전달될 수 없음.

[해결 방안]

1. Github Action, AWS Code deploy Blue/Green 무중단 배포를 통해 특정 브랜치에 push할 경우 CI/CD가 자동화 됨. Load Balancer를 통해 점진적으로 뒤에 배포된 WAS로 트래픽을 이동 시키기 때문에 서비스를 종료하지 않을 수 있음.
2. Github Action Secret 를 통해서 전달함.

[배운점]

1. CI/CD 자동화의 중요성과 편리함을 느낄 수 있었음.
2. Github Action, Code deploy Blue/Green 설정방법과 hook에 대해서 이해할 수 있었음.

# 15. 운영 로그 관리, 접근성을 위해 Logback Rolling FileAppender 적용 및 Sentry 도입

[문제점 및 이슈]

1. 시스템 운영 로그 확인 시 Bastion host를 통해 Private Subnet의 EC2로 접속해 log파일을 보는 방식이 번거로워서 에러 사항을 확인하고 공유하기 힘듬.
2. 로그파일 성격상 지속적으로 크기가 증가하며 일자별로 확인하기 힘듦. 

[원인 분석]

1. Logging 방식에 대해서 설정이 되어 있지 않음.

[해결 방안]

1. Sentry를 활용하여 API요청에 대한 에러 로그를 쉽게 확인하고 통계를 통해 쉽게 확인할 수 있도록 함.
2. Logback 설정을 Rolling File Appender 로 구성하여 일자별로 생성되고 자동 삭제될 수 있도록 함. 

[배운 점]

1. Logging 설정의 중요성과 방법에 대해서 배웠음.
2. 프로젝트 협업 시 에러에 대한 전반적인 사항들을 공유하는 것이 중요하다는 것을 깨달음.

[추가 개선점]

1. Slack 알림을 보낼 때 비동기 스레드를 활용하는데 이 경우 스레드 id가 변경되기 때문에 하나의 요청에 대한 로그인지 확인하기가 힘듦. MDC를 활용하면 해결할 수 있을 것으로 생각됨.
---

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


    


