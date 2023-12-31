package atguigu.oss.service.impl;

import atguigu.oss.service.OssService;
import atguigu.oss.utils.ConstantPropertiesUtils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Override
    public String uploadFileAvatar(MultipartFile file) { //上传头像到oss
        //工具类获取值
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        //创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();

            //获取文件名称
            String fileName = file.getOriginalFilename();
            //在文件名称里面添加随机唯一的值，避免重名覆盖文件
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            fileName = uuid + fileName;
            //把文件按照日期进行分类
            String datePath = new DateTime().toString("yyyy/MM/dd"); //获取当前日期
            fileName = datePath + "/" + fileName; //拼接

            //创建PutObjectRequest对象(参数：Bucket名称、上传到oss文件路径和文件名称、上传文件输入流)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream);
            //调用oss方法实现上传
            ossClient.putObject(putObjectRequest);

            //把上传之后文件路径返回(需要把上传到阿里云oss路径手动拼接出来)
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
