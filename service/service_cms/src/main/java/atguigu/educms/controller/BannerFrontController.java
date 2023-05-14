package atguigu.educms.controller;

import atguigu.commonutils.R;
import atguigu.educms.pojo.CrmBanner;
import atguigu.educms.service.CrmBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前台bannber显示
 * </p>
 *
 * @author testjava
 * @since 2023-05-04
 */
@RestController
@RequestMapping("/educms/bannerfront")
//@CrossOrigin
public class BannerFrontController {
    @Autowired
    private CrmBannerService bannerService;

    //查询所有banner
    @GetMapping("getAllBanner")
    @Cacheable(key = "'BannerList'", value = "banner")
    public R getAllBanner() {
        //因为用户访问很多，用mysql的话访问太频繁，写在service后面整合redis，避免直接查数据库
        List<CrmBanner> list = bannerService.selectAllBanner();
        return R.ok().data("list", list);
    }
}
