package moviereservationreport;

import org.springframework.data.repository.CrudRepository;

public interface MyPageRepository extends CrudRepository<MyPage, Long> {

        void deleteByReservationId(Long reservationId);

        void deleteByPaymentId(Long paymentId);

        void deleteBySeatMngId(Long seatMngId);
}