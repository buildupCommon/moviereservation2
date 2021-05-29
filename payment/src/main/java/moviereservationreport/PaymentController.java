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
public class PaymentController {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private PaymentRepository repository;

        @RequestMapping(method = RequestMethod.POST, path = "/pay")
        public void pay(@RequestBody Payment payment) {
                logger.info("called pay param : " + payment);
                repository.save(payment);
        }

        @RequestMapping(method = RequestMethod.DELETE, path = "/payCancled")
        public void payCancled(@RequestBody Payment payment) {
                logger.info("called payCancled param : " + payment);
                Optional<Payment> oPayment = repository.findByReservationId(payment.getReservationId());
                oPayment.ifPresent(p -> {
                        repository.delete(p);
                });
        }
}
