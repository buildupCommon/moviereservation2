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
@Table(name = "SeatMng_table")
public class SeatMng {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long customerId;
    private Integer number;
    private Long reservationId;

    @PostPersist
    public void onPostPersist() {
        SeatRequested seatRequested = new SeatRequested();
        BeanUtils.copyProperties(this, seatRequested);
        seatRequested.publishAfterCommit();

    }

    @PostRemove
    public void onPostRemove() {

        SeatCanceled seatCanceled = new SeatCanceled();
        BeanUtils.copyProperties(this, seatCanceled);
        seatCanceled.publishAfterCommit();
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

}
