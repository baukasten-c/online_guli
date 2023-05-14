package atguigu.educms.service.impl;

import atguigu.educms.pojo.CrmBanner;
import atguigu.educms.mapper.CrmBannerMapper;
import atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-05-04
 */
@Service
public class CrmBannerServiceImpl extends ServiceImpl<CrmBannerMapper, CrmBanner> implements CrmBannerService {
    //查询所有banner
    @Override
    public List<CrmBanner> selectAllBanner() {
        //根据sort进行降序排列，显示排列之后前两条记录
        QueryWrapper<CrmBanner> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("sort").last("limit 3"); //last方法，拼接sql语句
        List<CrmBanner> list = baseMapper.selectList(wrapper);
        return list;
    }
}
