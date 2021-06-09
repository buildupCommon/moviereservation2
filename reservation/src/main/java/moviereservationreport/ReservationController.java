package moviereservationreport;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private ReservationRepository repository;

        @RequestMapping(method = RequestMethod.POST, path = "/reserve")
        @HystrixCommand(fallbackMethod = "failback")
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

        @RequestMapping(method = RequestMethod.DELETE, path = "/cancel")
        public void cancel(@RequestBody Reservation reservation) {
                logger.debug("called cancel param " + reservation);

                repository.deleteById(reservation.getId());
        }

        public ResponseEntity<?> failback() {

                return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

        }
}
