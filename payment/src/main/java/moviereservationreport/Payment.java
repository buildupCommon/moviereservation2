package moviereservationreport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long customerId;
    private String type;
    private Integer seatNumber;
    private Long reservationId;

    @PostRemove
    public void onPostRemove() {
        PayCanceled payCanceled = new PayCanceled();
        BeanUtils.copyProperties(this, payCanceled);
        payCanceled.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        moviereservationreport.external.SeatMng seatMng = new moviereservationreport.external.SeatMng();
        // mappings goes here
        seatMng.setCustomerId(this.customerId);
        seatMng.setReservationId(this.reservationId);
        seatMng.setNumber(this.seatNumber);
        PaymentApplication.applicationContext.getBean(moviereservationreport.external.SeatMngService.class)
                .seatCancel(seatMng);

    }

    @PrePersist
    public void onPrePersist() {
        Approved approved = new Approved();
        BeanUtils.copyProperties(this, approved);
        approved.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        moviereservationreport.external.SeatMng seatMng = new moviereservationreport.external.SeatMng();
        // mappings goes here
        seatMng.setCustomerId(this.customerId);
        seatMng.setReservationId(this.reservationId);
        seatMng.setNumber(this.seatNumber);
        PaymentApplication.applicationContext.getBean(moviereservationreport.external.SeatMngService.class)
                .seatRequest(seatMng);

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id : " + id + "\n");
        sb.append("customerId : " + customerId + "\n");
        sb.append("reservationId : " + reservationId + "\n");
        sb.append("seatNumber : " + seatNumber + "\n");
        sb.append("paymentType : " + type + "\n");
        return sb.toString();
    }
}
