package site.ljc.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.jvm.hotspot.runtime.StaticBaseConstructor;

import javax.validation.OverridesAttribute;

/**
 * @Author Hamster
 * @Date 2022/4/28 21:22
 * @Version 1.0
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean {
    @Value("${tencent.sms.SmsSdkAppId}")
    private String sdkAppId;
    @Value("${tencent.sms.TemplateId}")
    private String templateId;

    public static String SDKAPPID;
    public static String TEMPLATEID;
    @Override
    public void afterPropertiesSet() throws Exception {
        SDKAPPID = sdkAppId;
        TEMPLATEID = templateId;
    }
}
