# e-commerce


#### 실행방법
./gradlew clean build

docker-compose up

#### 사용 기술
- Spring Boot
- Spring Data Jpa
- Redis
- EhCache
- Test Container
- Jacoco

#### 해결한 기술적 이슈

- **[Perf] @Async를 사용하여 장바구니 전체 주문 기능 개선하기**
- [Perf] @Query를 사용해 jpql을 직접 작성하기
- [Perf] Stream 사용하기

- [Test] @Cacheable, @CachePut, @CacheEvict 테스트하기
- [Test] Jacoco 사용해 테스트 커버리지 측정하기
- [Test] LocalDateTime을 모킹하여 테스트하기
- [Test] 스레드를 사용한 테스트 진행하기

- [Feat] UserDetails 구현시 Role 설정하기
- **[Feat]multi key에 대한 동시락 구현하기**

- **[CI] 도커 적절하게 사용하기**

- [Refactor] 커스텀 예외 사용하기
- [Refactor] 커스텀 어노테이션을 통해 로그인한 유저의 username을 리턴하기
- [Refactor] cascade 적용하지 않고 연관된 엔티티를 개별로 삭제하기
- [Refactor] Spring validation 사용하기

- **[Build] 환경에 따른 application.yml 분리 및 동적 포트 바인딩 사용**

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