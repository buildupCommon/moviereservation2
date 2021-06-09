package moviereservationreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import moviereservationreport.config.kafka.KafkaProcessor;

@Service
public class MyPageViewHandler {

    @Autowired
    private MyPageRepository myPageRepository;

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

    @StreamListener(KafkaProcessor.INPUT)
    public void whenApproved_then_CREATE_2(@Payload Approved approved) {
        try {

            if (!approved.validate())
                return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setPaymentId(approved.getId());
            myPage.setCustomerId(approved.getCustomerId());
            myPage.setReservationId(approved.getReservationId());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenSeatRequested_then_CREATE_3(@Payload SeatRequested seatRequested) {
        try {

            if (!seatRequested.validate())
                return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setSeatMngId(seatRequested.getId());
            myPage.setSeatNumber(seatRequested.getNumber());
            myPage.setCustomerId(seatRequested.getCustomerId());
            myPage.setReservationId(seatRequested.getReservationId());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCanceled_then_DELETE_1(@Payload Canceled canceled) {
        try {
            if (!canceled.validate())
                return;
            // view 레파지 토리에 삭제 쿼리
            // myPageRepository.deleteByReservationId(canceled.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPayCanceled_then_DELETE_2(@Payload PayCanceled payCanceled) {
        try {
            if (!payCanceled.validate())
                return;
            // view 레파지 토리에 삭제 쿼리
            // myPageRepository.deleteByPaymentId(payCanceled.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenSeatCanceled_then_DELETE_3(@Payload SeatCanceled seatCanceled) {
        try {
            if (!seatCanceled.validate())
                return;
            // view 레파지 토리에 삭제 쿼리
            // myPageRepository.deleteBySeatMngId(seatCanceled.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}