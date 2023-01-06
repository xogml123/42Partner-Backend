# 42Partner-Backend
## 프로젝트 수행 배경 및 필요성

### 문제점

- 42Partner는 42Seoul생활을 하면서 학습과 식사를 함께할 파트너를 매칭해주는 프로그램입니다.
- 42Seoul이라는 교육 프로그램은 교수, 교재, 강의가 없고 학습자들 간의 동료학습을 통해 학습을 하는 방식임.
- 따라서, 다른 동료들과의 관계를 잘 맺는게 매우 중요하고 많은 동료들이 친해지고 이어질 수록 학습의 능률이 올라감.
- 하지만, 다른 사람들과 선뜻 친해지는게 어려운 카뎃들도 많기 때문에 이들을 연결 시켜줄 수 있는 프로그램의 필요성을 발견.
- 또한 클러스터에서 최근에는 식사를 할때 실내 취식이 가능해지면서 배달수요가 증가.
- 하지만, 혼자서 배달을 시키기엔 배달비가 부담되기 때문에 슬랙에서 같이 배달을 시킬 사람들을 모집하는 글또한 여럿 있음.

### 해결방안

- 카뎃들이 서로 어울리는 거나 처음 친해지는 경우는 주로 동료학습을 하거나 같이 식사를 할 때 입니다. 이 때, 서로 많은 이야기를 하고 친분을 쉽게 키울 수 있다는 점에 착안하여 **학습과 식사** 두 영역을 정하고 매칭을 원하는 카뎃을 맺어주는 것으로 해결.
- 배달을 시킬 경우 배달비도 아끼면서, 동료간에 소통을 할 수 있는 연결 창구가 될 수 있음.

## 프로젝트 수행 과정

- 협업 툴로는 Git을 활용, Git Flow 전략 수립. [Gitflow 전략](https://techblog.woowahan.com/2553/)
- Issue template, Pull request convention, commit convention을 정하여 일관된 형식으로 협업 및 정보 공유가 이루어질 수 있도록 함.
- [Git Convention](https://www.notion.so/Github-Convention-2386de41d1de41fabc7b10cf2ab235b0)
- Issue 관리를 위해 Jira와 Gihub Issue를 고민하던중  프로젝트 규모를 고려하여 Github Issue활용.
- Notion을 활용하여 전체적인 프로젝트의 일정관리 및 필요한 문서들을 정리.
- 1주일 단위로 Sprint 진행하여 MVP단위로 개발을 수행.
## 프로젝트 기능 소개
[프로젝트 기능 링크](https://indigo-catsup-e60.notion.site/f7dedb0b96e74269bd0180d9a04b9db8)

## Database Design

<img width="1521" alt="image" src="https://user-images.githubusercontent.com/47822403/210037066-4435282b-6d49-4d1c-9c13-bad5bab46ea6.png">

[DB 최신 설계도](https://www.erdcloud.com/d/zrkNWk5ncp6WJCMyS)
<br>
[DB 설계과정](https://indigo-catsup-e60.notion.site/Database-DB-Design-bf9ce489736f4001b471ba33c66f35e9)

## Architecture Design
<img width="717" alt="image" src="https://user-images.githubusercontent.com/47822403/210039280-49cfd160-796d-42e3-84df-67cb596ddf4b.png">

![EC2내부](https://user-images.githubusercontent.com/47822403/210048126-ffae2fd4-1cc1-400b-ad29-6eb256ff5c47.png)

![CICD](https://user-images.githubusercontent.com/47822403/210933284-bbe05bbf-9d13-4d3f-b221-70982c667bec.png)



[아키텍처 설계 과정](https://indigo-catsup-e60.notion.site/Architecture-Design-c103a3d951644e47b327293aadde2ed3)
## Design Point

- High Availavility
    - 적어도 둘 이상의 Availavility Zone에 instance가 분포하도록함.
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
- Infrastructure on code
    - Credit 때문에 Migration하게 되는 일이 두번이나 발생및 이후 프로젝트 할 인원을 위하여 인프라 코드화 및 문서화가 필요하다는 것을 느끼게됨.
    - Terraform 을 활용하여 코드화함.
    - [https://github.com/42Partner/42Partner-DevOps](https://github.com/42Partner/42Partner-DevOps)
    


