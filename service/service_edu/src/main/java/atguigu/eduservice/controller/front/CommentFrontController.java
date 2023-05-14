package atguigu.eduservice.controller.front;

import atguigu.commonutils.JwtUtils;
import atguigu.commonutils.R;
import atguigu.eduservice.client.UcenterClient;
import atguigu.eduservice.pojo.EduComment;
import atguigu.eduservice.service.EduCommentService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-05-10
 */
@RestController
@RequestMapping("/eduservice/commentfront")
//@CrossOrigin
@Slf4j
public class CommentFrontController {
    @Autowired
    private EduCommentService commentService;
    @Autowired
    private UcenterClient ucenterClient;

    //根据课程id查询评论列表
    @GetMapping("getCommentInfo/{page}/{limit}")
    public R index(@PathVariable long page, @PathVariable long limit, String courseId) {
        //设置查询条件
        Page<EduComment> pageParam = new Page<>(page, limit);
        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        commentService.page(pageParam, wrapper);

        List<EduComment> commentList = pageParam.getRecords();

        //逐个查询会员昵称和头像(否则用户改变昵称和头像后，网页中数据不变)
        for (EduComment comment : commentList) {
            String memberId = comment.getMemberId();
            Map map = ucenterClient.getUserId(memberId);
            //json数据转成json字符串
            String jsonObject= JSON.toJSONString(map.get("userInfo"));
            //将json字符串转成需要的对象
            EduComment ucenterInfo = JSONObject.parseObject(jsonObject, EduComment.class);
            comment.setNickname(ucenterInfo.getNickname());
            comment.setAvatar(ucenterInfo.getAvatar());
        }

        //把分页数据获取出来，放到map集合
        Map<String, Object> map = new HashMap<>();
        map.put("items", commentList);
        map.put("current", pageParam.getCurrent()); //当前页
        map.put("pages", pageParam.getPages()); //总页数
        map.put("size", pageParam.getSize()); //一页记录数
        map.put("total", pageParam.getTotal()); //总记录数
        map.put("hasNext", pageParam.hasNext()); //上一页
        map.put("hasPrevious", pageParam.hasPrevious()); //下一页
        return R.ok().data(map);
    }

    //添加评论
    @PostMapping("addComment")
    public R addComment(@RequestBody EduComment comment, HttpServletRequest request) {
        //根据token字符串获取会员id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        if (StringUtils.isEmpty(memberId)){
            throw  new GuliException(20001, "请先登录");
        }
        //如果不为空，证明已经登录过
        comment.setMemberId(memberId);
        //远程调用ucenter根据用户id获取用户信息
        Map map = ucenterClient.getUserId(memberId);
        //远程调用ucenter模块，UcenterMemberController使用@restController修饰，getMemberInfoById返回的是JSON数据
        Object object = map.get("userInfo");
        //object中的json数据转成json字符串
        String jsonObject= JSON.toJSONString(object);
        //将json字符串转成需要的对象
        EduComment ucenterInfo = JSONObject.parseObject(jsonObject, EduComment.class);
        //设置名字和头像
        comment.setNickname(ucenterInfo.getNickname());
        comment.setAvatar(ucenterInfo.getAvatar());
        //保存评论
        commentService.save(comment);
        return R.ok();
    }
}
