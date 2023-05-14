package atguigu.staservice.service;

import atguigu.staservice.pojo.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {
    //统计某一天注册人数,生成统计数据
    void registerCount(String day);
    //图表显示
    Map<String, Object> getShowData(String type, String begin, String end);
}
