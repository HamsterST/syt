package site.ljc.yygh.msm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Service;
import site.ljc.yygh.msm.service.MsmService;
import site.ljc.yygh.msm.utils.ConstantPropertiesUtils;

/**
 * @Author Hamster
 * @Date 2022/4/29 9:42
 * @Version 1.0
 */
@Service
public class MsmSerivceImpl implements MsmService {
    @Override
    public boolean send(String phone, String code) {
        if(StringUtils.isBlank(phone)){
            return false;
        }else{
            try{
                // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                Credential cred = new Credential("SecretId", "SecretKey");
                // 实例化一个http选项，可选的，没有特殊需求可以跳过
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("sms.tencentcloudapi.com");
                // 实例化一个client选项，可选的，没有特殊需求可以跳过
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);
                // 实例化要请求产品的client对象,clientProfile是可选的
                SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
                // 实例化一个请求对象,每个接口都会对应一个request对象
                SendSmsRequest req = new SendSmsRequest();
                String[] phoneNumberSet1 = {"15912841178"};
                req.setPhoneNumberSet(phoneNumberSet1);
                req.setSmsSdkAppid(ConstantPropertiesUtils.SDKAPPID);
                req.setSign("个人学习实践网站");
                req.setTemplateID(ConstantPropertiesUtils.TEMPLATEID);

                String[] templateParamSet1 = {"554433", "30"};
                req.setTemplateParamSet(templateParamSet1);

                // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
                SendSmsResponse resp = client.SendSms(req);
                // 输出json格式的字符串回包
                System.out.println(SendSmsResponse.toJsonString(resp));
                return true;
            } catch (TencentCloudSDKException e) {
                System.out.println(e.toString());
            }
        }

        return false;
    }
}
