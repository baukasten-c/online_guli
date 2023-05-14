package atguigu.staservice.schedule;

import atguigu.staservice.service.StatisticsDailyService;
import atguigu.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class ScheduledTask { //添加定时任务
    @Autowired
    private StatisticsDailyService staService;

    //在每天凌晨1点，把前一天数据进行数据查询添加
    @Scheduled(cron = "0 0 1 * * ?")
    public void task() {
        //获取上一天的日期
        String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
        staService.registerCount(day);
    }
}
