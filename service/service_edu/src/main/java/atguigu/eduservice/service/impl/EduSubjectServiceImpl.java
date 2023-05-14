package atguigu.eduservice.service.impl;

import atguigu.eduservice.listener.SubjectExcelListener;
import atguigu.eduservice.pojo.EduSubject;
import atguigu.eduservice.mapper.EduSubjectMapper;
import atguigu.eduservice.pojo.excel.SubjectData;
import atguigu.eduservice.pojo.subject.OneSubject;
import atguigu.eduservice.pojo.subject.TwoSubject;
import atguigu.eduservice.service.EduSubjectService;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-04-29
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {
    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            //文件输入流
            InputStream in = file.getInputStream();
            //调用方法进行读取
            //当一个类被@Component注解修饰，或者在applicationContext配置文件中使用了<bean>标签配置，将其交给Spring容器进行管理后，如果在该类的属性或方法中new了对象，这个类将不能再被其他类使用@Autowired自动注入，会报空指针异常
            //SubjectExcelListener不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
            EasyExcel.read(in, SubjectData.class, new SubjectExcelListener(subjectService)).sheet().doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //课程分类列表(树形)
    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //查询所有一级分类  parentid = 0
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id", "0");
        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);

        //查询所有二级分类  parentid != 0
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id", "0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);

        //创建list集合，用于存储最终封装数据
        List<OneSubject> finalSubjectList = new ArrayList<>();

        //封装一级分类
        //遍历查询出来所有一级分类list集合，得到每个一级分类对象
        for (EduSubject eduSubject : oneSubjectList) { //遍历oneSubjectList集合
            OneSubject oneSubject = new OneSubject();
            //获取每个一级分类对象值(把eduSubject里面值获取出来，放到OneSubject对象里面)
//            oneSubject.setId(eduSubject.getId());
//            oneSubject.setTitle(eduSubject.getTitle());
            BeanUtils.copyProperties(eduSubject, oneSubject); //eduSubject值复制到对应oneSubject对象里面
            //封装到要求的List<OneSubject> finalSubjectList集合里面
            finalSubjectList.add(oneSubject);

            //封装二级分类
            //创建list集合封装每个一级分类的二级分类
            List<TwoSubject> twoFinalSubjectList = new ArrayList<>();
            //遍历二级分类list集合
            for (EduSubject subSubject : twoSubjectList) {
                //判断二级分类parentid和一级分类id是否一样
                if (subSubject.getParentId().equals(eduSubject.getId())) {
                    TwoSubject twoSubject = new TwoSubject();
                    //把subSubject值复制到TwoSubject里面，放到twoFinalSubjectList里面
                    BeanUtils.copyProperties(subSubject, twoSubject);
                    twoFinalSubjectList.add(twoSubject);
                }
            }
            //把一级下面所有二级分类放到一级分类里面
            oneSubject.setChildren(twoFinalSubjectList);
        }
        return finalSubjectList;
    }
}
