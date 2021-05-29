package moviereservationreport;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import moviereservationreport.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler {

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_CollectMyInfos(@Payload Canceled canceled) {

        if (!canceled.validate())
            return;

        System.out.println("\n\n##### listener CollectMyInfos : " + canceled.toJson() + "\n\n");

        // Sample Logic //

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverApproved_CollectMyInfos(@Payload Approved approved) {

        if (!approved.validate())
            return;

        System.out.println("\n\n##### listener CollectMyInfos : " + approved.toJson() + "\n\n");

        // Sample Logic //

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCanceled_CollectMyInfos(@Payload PayCanceled payCanceled) {

        if (!payCanceled.validate())
            return;

        System.out.println("\n\n##### listener CollectMyInfos : " + payCanceled.toJson() + "\n\n");

        // Sample Logic //

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSeatRequested_CollectMyInfos(@Payload SeatRequested seatRequested) {

        if (!seatRequested.validate())
            return;

        System.out.println("\n\n##### listener CollectMyInfos : " + seatRequested.toJson() + "\n\n");

        // Sample Logic //

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSeatCanceled_CollectMyInfos(@Payload SeatCanceled seatCanceled) {

        if (!seatCanceled.validate())
            return;

        System.out.println("\n\n##### listener CollectMyInfos : " + seatCanceled.toJson() + "\n\n");

        // Sample Logic //

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_CollectMyInfos(@Payload Reserved reserved) {

        if (!reserved.validate())
            return;

        System.out.println("\n\n##### listener CollectMyInfos : " + reserved.toJson() + "\n\n");

        // Sample Logic //

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

}
