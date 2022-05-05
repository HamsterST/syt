package site.ljc.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import site.ljc.yygh.model.order.OrderInfo;
import site.ljc.yygh.vo.order.OrderCountQueryVo;
import site.ljc.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * @Author Hamster
 * @Date 2022/5/3 19:35
 * @Version 1.0
 */
public interface OrderMapper extends BaseMapper<OrderInfo> {
    //查询预约统计数据的方法
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
