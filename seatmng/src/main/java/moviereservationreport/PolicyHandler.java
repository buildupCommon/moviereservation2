package moviereservationreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import moviereservationreport.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler {
    @Autowired
    SeatMngRepository seatMngRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

}
