package moviereservationreport;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "seatMngs", path = "seatMngs")
public interface SeatMngRepository extends PagingAndSortingRepository<SeatMng, Long> {

    Optional<SeatMng> findByReservationId(Long reservationId);
}
