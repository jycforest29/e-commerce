# e-commerce

#### 실행방법
./gradlew clean build

docker-compose up

#### 사용 기술
- Java 11
- Spring Boot 2.7
- Spring Data Jpa
- Redis
- EhCache
- Mysql
- Test Container
- Docker
- Jacoco

#### 해결한 기술적 이슈

- 동시성 문제 해결하기
  - Lettuce를 사용해 multi key에 대한 동시락 구현하고 롤백 테스트하기 
  - @Async와 CompletableFuture를 사용하여 장바구니 전체 주문 기능 개선하기 
  - 스레드를 사용한 동시성 로직 테스트 진행하기 
- 개발환경 구성하기
  - Docker, docker-compose, Test container 사용하기 
  - application.yml 분리 및 동적 포트 바인딩 사용하기 
  - @Slf4j로 로그찍기 
- 캐싱 사용하기
  - 프로젝트 구조에 맞는 캐싱 설계하기 
  - @Cacheable, @CachePut, @CacheEvict 테스트하기 
- 테스트 견고함 높이기
  - LocalDateTime.now()를 모킹하여 테스트하기 
  - Jacoco 사용해 테스트 커버리지 측정하기 
- JPA 성능 높이기
  - 벌크 연산시 JPQL 직접 작성해 성능 높이기 
- 스프링의 AOP 활용해 여러 커스텀 하기 
  - @ExceptionHandler로 커스텀 예외 사용하기 
  - 커스텀 어노테이션을 통해 로그인한 유저의 username을 리턴하고 테스트하기 
- 엔티티 설계하기
  - cascade 적용하지 않고 연관된 엔티티를 개별로 삭제하기 
  - 스프링 시큐리티에서 UserDetails 구현시 Role 설정하기
- DTO 및 파라미터 유효성 검사하기 
  - Spring validation 사용하기 
- 자바8에서 도입된 기능 적극 사용하기
  - 자바8의 Stream 사용하기
  - 자바8의 CompletableFuture 사용하기

#### 보완점
- N+1 문제 발생 확인 및 해결하기
- Batch로 구현한 주문 확정 로직 테스트하기
- 비동기 구현시 사용한 CompletableFuture 예외처리 하기
- Jmeter 사용해 부하 테스트 진행하기
- 스케일 아웃 시 카프카로 로컬 캐시 동기화하기
- Repository 계층의 JPQL 테스트하기
- JVM 튜닝해서 성능 높이기
- 스케일 아웃 진행 후 NGINX 리버스 프록시 사용해 성능 확인하기
- Batch로 랜덤 쿠폰 발행하기 
- 장바구니를 구현하는 가장 효과적인 방법 찾기(현재는 메인 DB 사용)
