package atguigu.eduservice.client;

import atguigu.servicebase.exceptionhandler.GuliException;

import java.util.Map;

public class UcenterFileDegradeFeignClient implements UcenterClient{
    @Override
    public Map getUserId(String memberId) {
        throw new GuliException(20001,"获取用户信息失败");
    }
}
