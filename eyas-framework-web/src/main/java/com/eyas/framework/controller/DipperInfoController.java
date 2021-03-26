package com.eyas.framework.controller;

import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.config.DipperInfo;
import com.eyas.framework.config.DipperInfoDTO;
import com.eyas.framework.data.EyasFrameworkDto;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.utils.TenantThreadLocal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class DipperInfoController {

    @Resource
    private DipperInfo dipperInfo;


    @GetMapping("/all")
    @WithOutToken
    public EyasFrameworkResult<List<DipperInfoDTO>> ok(){
        EyasFrameworkDto eyasFrameworkDto = new EyasFrameworkDto();
        eyasFrameworkDto.setTenantCode(100L);
        TenantThreadLocal.setSystemUser(eyasFrameworkDto);
        List<DipperInfoDTO> dipperInfoDTOList = this.dipperInfo.queryAll();
        return EyasFrameworkResult.ok(dipperInfoDTOList);
    }

    @PostMapping("/insert")
    @WithOutToken
    public EyasFrameworkResult<Integer> insert(@RequestBody DipperInfoDTO dipperInfoDTO){
        EyasFrameworkDto eyasFrameworkDto = new EyasFrameworkDto();
        eyasFrameworkDto.setTenantCode(100L);
        TenantThreadLocal.setSystemUser(eyasFrameworkDto);
        Integer cnt = this.dipperInfo.insert(dipperInfoDTO);
        return EyasFrameworkResult.ok(cnt);
    }

    @PutMapping("/update")
    @WithOutToken
    public EyasFrameworkResult<Integer> update(@RequestBody DipperInfoDTO dipperInfoDTO){
        EyasFrameworkDto eyasFrameworkDto = new EyasFrameworkDto();
        eyasFrameworkDto.setTenantCode(100L);
        TenantThreadLocal.setSystemUser(eyasFrameworkDto);
        Integer cnt = this.dipperInfo.update(dipperInfoDTO);
        return EyasFrameworkResult.ok(cnt);
    }

    @PostMapping("/delete")
    @WithOutToken
    public EyasFrameworkResult<Integer> delete(@RequestBody DipperInfoDTO dipperInfoDTO){
        EyasFrameworkDto eyasFrameworkDto = new EyasFrameworkDto();
        eyasFrameworkDto.setTenantCode(100L);
        TenantThreadLocal.setSystemUser(eyasFrameworkDto);
        Integer cnt = this.dipperInfo.delete(dipperInfoDTO);
        return EyasFrameworkResult.ok(cnt);
    }


}
