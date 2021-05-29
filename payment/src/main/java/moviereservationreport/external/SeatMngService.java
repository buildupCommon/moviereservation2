
package moviereservationreport.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "seatmng", url = "http://seatmng:8080")
public interface SeatMngService {

    @RequestMapping(method = RequestMethod.GET, path = "/seatRequest")
    public void seatRequest(@RequestBody SeatMng seatMng);

    @RequestMapping(method = RequestMethod.DELETE, path = "/seatCancel")
    public void seatCancel(@RequestBody SeatMng seatMng);

}