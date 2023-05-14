package atguigu.educms.service;

import atguigu.educms.pojo.CrmBanner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-05-04
 */
public interface CrmBannerService extends IService<CrmBanner> {
    //查询所有banner
    List<CrmBanner> selectAllBanner();
}
