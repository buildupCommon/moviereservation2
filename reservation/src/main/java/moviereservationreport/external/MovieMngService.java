
package moviereservationreport.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "moviemng", url = "http://moviemng:8080")
public interface MovieMngService {

    @RequestMapping(method = RequestMethod.POST, path = "/isExist")
    public boolean isExist(@RequestBody MovieMng movieMng);

}