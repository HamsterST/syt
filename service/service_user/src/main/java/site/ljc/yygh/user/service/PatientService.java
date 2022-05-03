package site.ljc.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.ljc.yygh.model.user.Patient;

import java.util.List;

/**
 * @Author Hamster
 * @Date 2022/5/2 11:32
 * @Version 1.0
 */
public interface PatientService extends IService<Patient> {
    List<Patient> findAllUserId(Long userId);

    Patient getPatientId(Long id);
}
