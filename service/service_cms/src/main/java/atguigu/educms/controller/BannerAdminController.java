package atguigu.educms.controller;

import atguigu.commonutils.R;
import atguigu.educms.pojo.CrmBanner;
import atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 后台banner管理接口
 * </p>
 *
 * @author testjava
 * @since 2023-05-04
 */
@RestController
@RequestMapping("/educms/banneradmin")
//@CrossOrigin
//CMS是"Content Management System"的缩写,意为"内容管理系统"，一般是用来管理前台界面的轮播图
public class BannerAdminController {
    @Autowired
    private CrmBannerService bannerService;

    //添加banner
    @ApiOperation(value = "增加Banner")
    @CachePut(key = "'addBanner'", value = "banner")
    @PostMapping("add")
    public R addBanner(@RequestBody CrmBanner crmBanner) {
        boolean save = bannerService.save(crmBanner);
        return save ? R.ok() : R.error();
    }

    //逻辑删除Banner
    @ApiOperation(value = "删除Banner")
    @CacheEvict(key = "'deleteBanner'", value = "banner")
    @DeleteMapping("remove/{id}")
    public R remove(@PathVariable String id) {
        boolean remove = bannerService.removeById(id);
        return remove ? R.ok() : R.error();
    }

    //根据讲师banner的id进行查询
    @ApiOperation(value = "获取Banner")
    @GetMapping("get/{id}")
    public R get(@PathVariable String id) {
        CrmBanner banner = bannerService.getById(id);
        return R.ok().data("banner", banner);
    }

    @ApiOperation(value = "修改Banner")
    @CacheEvict(key = "'updateBanner'", value = "banner")
    @PutMapping("update")
    public R updateById(@RequestBody CrmBanner banner) {
        boolean update = bannerService.updateById(banner);
        return update ? R.ok() : R.error();
    }

    @ApiOperation(value = "分页查询banner")
    @GetMapping("pageBanner/{page}/{limit}")
    public R pageBanner(@PathVariable long page, @PathVariable long limit) {
        Page<CrmBanner> pageBanner = new Page<>(page, limit);
        bannerService.page(pageBanner, null);
        return R.ok().data("rows", pageBanner.getRecords()).data("total", pageBanner.getTotal());
    }
}
