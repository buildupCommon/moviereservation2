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
public class MovieMngController {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private MovieMngRepository repository;

        @RequestMapping(method = RequestMethod.POST, path = "/isExist")
        public boolean isExist(@RequestBody MovieMng movieMng) {
                logger.info("called isExist param " + movieMng);

                Optional<MovieMng> oMovieMng = repository.findByName(movieMng.getName());

                return oMovieMng.isPresent();
        }
}
