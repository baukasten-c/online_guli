package atguigu.eduservice.controller;

import atguigu.eduservice.pojo.EduTeacher;
import atguigu.eduservice.pojo.vo.TeacherQuery;
import atguigu.eduservice.service.EduTeacherService;
import atguigu.commonutils.R;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-04-24
 */
@RestController //@Controller+@ResponseBody(以Json的格式返回给前端浏览器)
@RequestMapping("/eduservice/teacher")
@Api(tags = "讲师管理")
//@CrossOrigin
public class EduTeacherController {
    @Autowired
    private EduTeacherService teacherService;

    //查询讲师表所有数据(rest风格)
    @ApiOperation(value = "所有讲师列表")
    @GetMapping("findAll") //访问地址： http://localhost:8001/eduservice/teacher/findAll
    public R findAllTeacher() {
        //调用service的方法实现查询所有的操作
        List<EduTeacher> list = teacherService.list(null);
        return R.ok().data("items",list);
    }

    //逻辑删除讲师的方法
    @ApiOperation(value = "逻辑删除讲师")
    @DeleteMapping("{id}")
    public R removeTeacher(@ApiParam(name = "id", value = "讲师ID", required = true)
                                 @PathVariable String id) {
        boolean flag = teacherService.removeById(id);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //分页查询讲师的方法
    @ApiOperation(value = "分页查询讲师")
    @GetMapping("pageTeacher/{current}/{limit}") //current 当前页，limit 每页记录数
    public R pageListTeacher(@PathVariable long current,
                             @PathVariable long limit) {
        try{
            int i = 10 / 0;
        }catch (Exception e){
            //执行自定义异常
            throw new GuliException(20001, "执行了自定义异常处理..");
        }

        //创建page对象
        Page<EduTeacher> pageTeacher = new Page<>(current,limit);
        //调用方法实现分页(调用方法时候，底层封装，把分页所有数据封装到pageTeacher对象里面)
        teacherService.page(pageTeacher,null);
        long total = pageTeacher.getTotal();//总记录数
        List<EduTeacher> records = pageTeacher.getRecords(); //数据list集合
        //data可以用对象，也可以用集合，用对象需要提前定义一个Vo对象，里面写好返回的属性，集合的键值对好处在于无限内容，不需要提前定义，灵活性比较好
//        Map map = new HashMap();
//        map.put("total",total);
//        map.put("rows",records);
//        return R.ok().data(map);
        return R.ok().data("total",total).data("rows",records);
        //也可以传page对象，前端从对象中再拿数据，这里直接传数据，前端省事点
//        return R.ok().data("result", pageTeacher);
    }

    //条件查询带分页的方法
    @ApiOperation(value = "分页条件查询讲师")
    @PostMapping("pageTeacherCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current,@PathVariable long limit,
                                  //前端传json字符串需要加requestbody，前端发送json数据的请求过来，这个json数据会被spring自动封装在这个对象中
                                  //@RequestBody使用post方式提交数据(get是传递的请求头中的参数，post传递的是请求体中的参数)
                                  @RequestBody(required = false) TeacherQuery teacherQuery) {
        //创建page对象
        Page<EduTeacher> pageTeacher = new Page<>(current,limit);
        //构建条件
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        // 多条件组合查询(动态sql)
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        //判断条件值是否为空，如果不为空拼接条件
        if(!StringUtils.isEmpty(name)) {
            //构建条件
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(level)) {
            //StringUtils.isEmpty这个判断对象是否为空的条件是！=null || .length != 0所以Integer类型用也可以
            wrapper.eq("level",level);
        }
//        if(!StringUtils.isEmpty(begin)) {
//            wrapper.ge("gmt_create",begin);
//        }
        wrapper.ge(!StringUtils.isEmpty(begin),"gmt_create",begin);
        if(!StringUtils.isEmpty(end)) {
            wrapper.le("gmt_create",end);
        }
        //排序
        wrapper.orderByDesc("gmt_create");
        //调用方法实现条件查询分页
        teacherService.page(pageTeacher,wrapper);

        long total = pageTeacher.getTotal();//总记录数
        List<EduTeacher> records = pageTeacher.getRecords(); //数据list集合
        return R.ok().data("total",total).data("rows",records);
    }

    //添加讲师接口的方法
    @ApiOperation(value = "添加讲师信息")
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher) {
        boolean save = teacherService.save(eduTeacher);
        return save ? R.ok() : R.error();
    }
    //前端先调用方法，判断id是否存在，再根据结果判断是否可以修改讲师
    //根据讲师id进行查询
    @ApiOperation(value = "查询讲师信息")
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id) {
        EduTeacher eduTeacher = teacherService.getById(id);
        return R.ok().data("teacher",eduTeacher);
    }

    //讲师修改功能
    @ApiOperation(value = "修改讲师信息")
    @PutMapping("updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher) {
        boolean flag = teacherService.updateById(eduTeacher);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}

