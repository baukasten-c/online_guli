package atguigu.eduservice.listener;

import atguigu.eduservice.pojo.EduSubject;
import atguigu.eduservice.pojo.excel.SubjectData;
import atguigu.eduservice.service.EduSubjectService;
import atguigu.servicebase.exceptionhandler.GuliException;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

//spring的注入是在filter和listener之后的(顺序是这样的listener->filter->servlet ->spring)
//filter、listener都是在servlet容器中，Spring也可以创建，但是不在servlet容器中，不能正常使用，所以servlet交给web server来管理，不要交给spring管理
public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
    public EduSubjectService subjectService;

    public SubjectExcelListener() {
    }
    //因为SubjectExcelListener不能交给spring进行管理，需要自己new，不能注入其他对象
    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    //读取excel内容
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if (subjectData == null) {
            throw new GuliException(20001, "文件数据为空");
        }
        //一行一行读取，每次读取有两个值，第一个值一级分类，第二个值二级分类
        //判断一级分类是否重复
        EduSubject existOneSubject = this.existOneSubject(subjectData.getOneSubjectName());
        if (existOneSubject == null) { //没有相同一级分类，进行添加
            existOneSubject = new EduSubject();
            existOneSubject.setTitle(subjectData.getOneSubjectName()); //一级分类名称
            existOneSubject.setParentId("0");
            subjectService.save(existOneSubject);
        }

        //获取一级分类id值(existOneSubject此时一定不为null)
        String pid = existOneSubject.getId();

        //判断二级分类是否重复
        EduSubject existTwoSubject = this.existTwoSubject(subjectData.getTwoSubjectName(), pid);
        if (existTwoSubject == null) { //没有相同二级分类，进行添加
            existTwoSubject = new EduSubject();
            existTwoSubject.setTitle(subjectData.getTwoSubjectName()); //二级分类名称
            existTwoSubject.setParentId(pid);
            subjectService.save(existTwoSubject);
        }
    }

    //判断一级分类不能重复添加
    private EduSubject existOneSubject(String name) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", "0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    //判断二级分类不能重复添加
    private EduSubject existTwoSubject(String name, String pid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", pid);
        EduSubject twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
