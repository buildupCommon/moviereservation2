
# 영화표 예매 시스템 (리포트)

# 서비스 시나리오


기능적 요구사항
1. 고객이 예매를 한다.(예매 시 영화 및 좌석 선택)
2. 영화가 존재하는지 확인 후 존재 시 결제가 진행된다.
3. 결제가 완료 후 고객 좌석이 예매된다.
4. 고객이 예매를 취소한다.
5. 예매가 취소되면 결제도 취소된다.
6. 결제가 취소되면 좌석도 취소된다.
7. 영화관리 페이지를 통해 영화를 등록할 수 있다.
8. 고객은 나의페이지를 통해 상황을 확인할 수 있다.


비기능적 요구사항
1. 트랜잭션
    1. 좌석이 선택되지 않은 예약건은 아예 거래가 성립되지 않아야 한다. Sync 호출
    2. 티켓이 취소되야 결제가 취소된다. Sync 호출
1. 장애격리
    1. 일부 시스템이 장애가 나더라도 현재 상태에 대해 마이페이지에서 확인할 수 있도록 한다. Async 호출
1. 성능
    1. 고객이 자주 예매상태를 마이페이지(프론트엔드)에서 확인할 수 있어야 한다  CQRS



# 분석/설계

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  
![1](https://user-images.githubusercontent.com/54625960/121278735-36243600-c90e-11eb-9079-f41c146eee51.PNG)



# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd reservation
mvn spring-boot:run

cd payment
mvn spring-boot:run 

cd seatmng
mvn spring-boot:run  

cd moviemng
mvn spring-boot:run  

cd mypage
python policy-handler.py

```

## DDD 의 적용

```
# moviemng 서비스의 영화등록
http POST localhost:8082/movieMngs name="avengers" summary="avengers avengers"

# reserve 서비스의 결제확인
http POST localhost:8081/reserve movieName="avengers" seatNumber=10 paymentType="Credit" customerId=10
```


## 폴리글랏 프로그래밍, 퍼시스턴스

```
  datasource:
    url: jdbc:mysql://database-2.cxbzaw0b0fi0.ap-northeast-2.rds.amazonaws.com/innodb
    username: admin
    password: hi591005

```

마이페이지 DB를 mysql DB로 구성

![2](https://user-images.githubusercontent.com/54625960/121290783-2adb0580-c922-11eb-8f47-4bcff546ab7f.PNG)


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 좌석이 선택되지 않은 예약건은 아예 거래가 성립되지 않아야 한다. 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# MovieMngService.java

@FeignClient(name = "moviemng", url = "http://moviemng:8080")
public interface MovieMngService {

    @RequestMapping(method = RequestMethod.POST, path = "/isExist")
    public boolean isExist(@RequestBody MovieMng movieMng);

}
```

- 영화를 존재유무 확인 후 받은 직후(@PostPersist) 결제를 요청하도록 처리
```

    public void reserve(@RequestBody Reservation reservation) {
                logger.info("called reserve param " + reservation);
                moviereservationreport.external.MovieMng movieMng = new moviereservationreport.external.MovieMng();

                movieMng.setName(reservation.getMovieName());
                // mappings goes here
                Boolean isExistMovie = ReservationApplication.applicationContext
                                .getBean(moviereservationreport.external.MovieMngService.class).isExist(movieMng);

                logger.info("called isExist param " + isExistMovie);
                if (isExistMovie) {
                        logger.info("called isExist true");
                        repository.save(reservation);
                } else {
                        logger.info("called isExist false");
                }
        }
        
    @PostPersist
    public void onPostPersist() {
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        moviereservationreport.external.Payment payment = new moviereservationreport.external.Payment();
        // mappings goes here
        payment.setCustomerId(this.customerId);
        payment.setReservationId(this.id);
        payment.setSeatNumber(this.seatNumber);
        payment.setType(this.paymentType);
        ReservationApplication.applicationContext.getBean(moviereservationreport.external.PaymentService.class)
                .pay(payment);

    }
```



## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

마이페이지 관리를 위해 서비스가 일부 중단이 되더라도
마이페이지 이벤트를 카프카로 송출한다

 
```

    @PostPersist
    public void onPostPersist() {
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();
    }
```
- 마이페이지에서는 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
package fooddelivery;

...
@StreamListener(KafkaProcessor.INPUT)
    public void whenReserved_then_CREATE_1(@Payload Reserved reserved) {
        try {

            if (!reserved.validate())
                return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setReservationId(reserved.getId());
            myPage.setMovieName(reserved.getMovieName());
            myPage.setSeatNumber(reserved.getSeatNumber());
            myPage.setPaymentType(reserved.getPaymentType());
            myPage.setCustomerId(reserved.getCustomerId());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

```


# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 AWS를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 buildspec.yml 에 포함되었다.

특이사항으로는 모든 프로젝트 소스를 아래와 같이 하나의 git에서 관리를 하여 편의성을 도모했으며,

![3](https://user-images.githubusercontent.com/54625960/121290919-64ac0c00-c922-11eb-8cf1-4f57ac67f3f8.PNG)

각 프로젝트별 빌드를 하기 위해 파이프라인을 아래와 같이 개별적으로 구성했습니다.

![4](https://user-images.githubusercontent.com/54625960/121290927-670e6600-c922-11eb-8a10-881d9205db2e.PNG)


파이프라인은 aws codepipeline, codebuild를 활용했으며, codebuild의 경우 git의 루트 경로가 home임으로 
source build, dockering에서 필요한 경로를 아래와 같이 개별 프로젝트의 buildspec에서 정하여 CI/CD를 구현하였습니다.

![5](https://user-images.githubusercontent.com/54625960/119451490-49f96500-bd70-11eb-8dd3-1e0e84a931c4.PNG)
![6](https://user-images.githubusercontent.com/54625960/119451491-49f96500-bd70-11eb-99eb-855fcf4d846b.PNG)



# 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring Hystrix 옵션을 사용하여 구현함

시나리오는 예약(Reservation)-->결제(payment) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.
Hystrix 를 설정: 요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정

![11](https://user-images.githubusercontent.com/54625960/121296173-2cf59200-c92b-11eb-937f-34705fbbb0c8.PNG)

피호출 서비스(결제:payment) 의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
![12](https://user-images.githubusercontent.com/54625960/121296182-2ff08280-c92b-11eb-81d3-7933adf8f792.PNG)

부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
동시사용자 150명
60초 동안 실시
![16](https://user-images.githubusercontent.com/54625960/121296338-7219c400-c92b-11eb-9c5f-a62812d64f60.PNG)

![17](https://user-images.githubusercontent.com/54625960/121296343-734af100-c92b-11eb-825c-74c56e9676c5.PNG)


### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 30프로를 넘어서면 replica 를 10개까지 늘려준다

![1](https://user-images.githubusercontent.com/54625960/119450045-5ed4f900-bd6e-11eb-9f0d-19cf62471fe5.PNG)

- CB 에서 했던 방식대로 워크로드를 100초 동안 걸어준다.
- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 

![3](https://user-images.githubusercontent.com/54625960/119450050-60062600-bd6e-11eb-89ce-e07373449ed1.PNG)


- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다

![2](https://user-images.githubusercontent.com/54625960/119450048-60062600-bd6e-11eb-8a19-cbd1ca10d010.PNG)


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
![9](https://user-images.githubusercontent.com/54625960/121295149-85c42b00-c929-11eb-9407-1c1907101126.PNG)

```

- 새버전으로의 배포 시작

![8](https://user-images.githubusercontent.com/54625960/121295154-878dee80-c929-11eb-80e5-4269a14b7778.PNG)


- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
![10](https://user-images.githubusercontent.com/54625960/121295159-88bf1b80-c929-11eb-8111-6cbcb44535da.PNG)

배포기간중 Availability 가 평소 100%에서 70% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:
![13](https://user-images.githubusercontent.com/54625960/121295333-cd4ab700-c929-11eb-82c5-f2ac7f37235a.PNG)



배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

