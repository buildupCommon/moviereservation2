
package moviereservationreport.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "payment", url = "http://payment:8080")
public interface PaymentService {

    @RequestMapping(method = RequestMethod.POST, path = "/pay")
    public void pay(@RequestBody Payment payment);

    @RequestMapping(method = RequestMethod.DELETE, path = "/payCancled")
    public void payCancled(@RequestBody Payment payment);
}