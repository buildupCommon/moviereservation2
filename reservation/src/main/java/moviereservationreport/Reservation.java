package moviereservationreport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long customerId;
    private String movieName;
    private Integer seatNumber;
    private String paymentType;

    @PostRemove
    public void onPostRemove() {
        Canceled canceled = new Canceled();
        BeanUtils.copyProperties(this, canceled);
        canceled.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        moviereservationreport.external.Payment payment = new moviereservationreport.external.Payment();
        // mappings goes here
        payment.setCustomerId(this.customerId);
        payment.setReservationId(this.id);
        payment.setSeatNumber(this.seatNumber);
        payment.setType(this.paymentType);
        ReservationApplication.applicationContext.getBean(moviereservationreport.external.PaymentService.class)
                .payCancled(payment);

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

        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("customerId : " + customerId + "\n");
        sb.append("movieName : " + movieName + "\n");
        sb.append("seatNumber : " + seatNumber + "\n");
        sb.append("paymentType : " + paymentType + "\n");
        return sb.toString();
    }

}
