package atguigu.staservice.service.impl;

import atguigu.commonutils.R;
import atguigu.servicebase.exceptionhandler.GuliException;
import atguigu.staservice.client.UcenterClient;
import atguigu.staservice.pojo.StatisticsDaily;
import atguigu.staservice.mapper.StatisticsDailyMapper;
import atguigu.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-05-11
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {
    @Autowired
    private UcenterClient ucenterClient;

    //统计某一天注册人数,生成统计数据
    @Override
    public void registerCount(String day) {
        //添加记录之前删除表相同日期的数据
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.eq("date_calculated", day);
        baseMapper.delete(wrapper);

        //获取统计信息
        //远程调用得到某一天注册人数
        R registerR = ucenterClient.countRegister(day);
        Integer countRegister = (Integer) registerR.getData().get("countRegister");
        Integer loginNum = RandomUtils.nextInt(100, 200);
        Integer videoViewNum = RandomUtils.nextInt(100, 200);
        Integer courseNum = RandomUtils.nextInt(100, 200);

        //把获取的数据添加到数据库的统计分析表里面
        StatisticsDaily sta = new StatisticsDaily();
        sta.setRegisterNum(countRegister); //注册人数
        sta.setDateCalculated(day); //统计日期
        sta.setVideoViewNum(videoViewNum);
        sta.setLoginNum(loginNum);
        sta.setCourseNum(courseNum);

        baseMapper.insert(sta);
    }

    //图表显示，返回两部分数据，日期json数组，数量json数组
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        //根据条件查询对应数据
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated", begin, end);
        //type字符串已经从小驼峰命名格式（lowerCamelCase）转换为小写下划线命名格式（lower_underscore_case）
        //select()就是获取date_calculated和type两个字段的信息
        wrapper.select("date_calculated", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, type));
        List<StatisticsDaily> staList = baseMapper.selectList(wrapper);

        //前端要求数组json结构，对应后端java代码是list集合
        //因为返回有两部分数据：日期 和 日期对应数量，所以创建两个list集合，一个日期list，一个数量list
        List<String> date_calculatedList = new ArrayList<>();
        List<Integer> numDataList = new ArrayList<>();

        //遍历查询所有数据list集合，进行封装
        for(StatisticsDaily daily : staList){
            //封装日期list集合
            date_calculatedList.add(daily.getDateCalculated());
            //封装对应数量
//            switch (type) {
//                case "login_num":
//                    numDataList.add(daily.getLoginNum());
//                    break;
//                case "register_num":
//                    numDataList.add(daily.getRegisterNum());
//                    break;
//                case "video_view_num":
//                    numDataList.add(daily.getVideoViewNum());
//                    break;
//                case "course_num":
//                    numDataList.add(daily.getCourseNum());
//                    break;
//            }
            try {
                Method getterMethod = StatisticsDaily.class.getMethod("get" + type.substring(0, 1).toUpperCase() + type.substring(1));
                Integer value = (Integer) getterMethod.invoke(daily);
                numDataList.add(value);
            } catch (Exception e) {
                throw new GuliException(20001, "封装对应数量失败");
            }
        }

        // 对日期集合进行排序
        Collections.sort(date_calculatedList, (date1, date2) -> {
            LocalDate localDate1 = LocalDate.parse(date1);
            LocalDate localDate2 = LocalDate.parse(date2);
            return localDate1.compareTo(localDate2);
        });

        //把封装之后两个list集合放到map集合，进行返回
        Map<String, Object> map = new HashMap<>();
        map.put("date_calculatedList", date_calculatedList);
        map.put("numDataList", numDataList);
        return map;
    }
}
