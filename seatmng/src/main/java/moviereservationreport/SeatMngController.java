package moviereservationreport;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeatMngController {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private SeatMngRepository repository;

        @RequestMapping(method = RequestMethod.POST, path = "/seatRequest")
        public void seatRequest(@RequestBody SeatMng seatMng) {
                logger.info("called seatRequest param : " + seatMng);
                repository.save(seatMng);
        }

        @RequestMapping(method = RequestMethod.DELETE, path = "/seatCancel")
        public void seatCancel(@RequestBody SeatMng seatMng) {
                logger.info("called seatCancel param : " + seatMng);
                Optional<SeatMng> oSeatMng = repository.findByReservationId(seatMng.getReservationId());
                oSeatMng.ifPresent(s -> {
                        repository.delete(s);
                });
        }
}
