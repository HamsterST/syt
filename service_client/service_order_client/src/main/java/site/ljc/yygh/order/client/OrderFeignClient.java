package site.ljc.yygh.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.ljc.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @Author Hamster
 * @Date 2022/5/5 11:12
 * @Version 1.0
 */
@FeignClient("service-order")
@Repository
public interface OrderFeignClient {
    @PostMapping("api/order/orderInfo/inner/getCountMap")
    Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
