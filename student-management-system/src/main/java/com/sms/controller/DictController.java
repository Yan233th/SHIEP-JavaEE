package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.model.DictData;
import com.sms.model.DictType;
import com.sms.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    // ==================== 字典类型接口 ====================

    @GetMapping("/types")
    public ApiResponse<List<DictType>> getAllTypes() {
        return ApiResponse.success(dictService.findAllTypes());
    }

    @GetMapping("/types/{id}")
    public ApiResponse<DictType> getTypeById(@PathVariable Long id) {
        return dictService.findTypeById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("字典类型不存在"));
    }

    @PostMapping("/types")
    public ApiResponse<DictType> createType(@RequestBody DictType dictType) {
        if (dictService.existsTypeCode(dictType.getTypeCode())) {
            return ApiResponse.error("字典类型编码已存在");
        }
        return ApiResponse.success(dictService.saveType(dictType));
    }

    @PutMapping("/types/{id}")
    public ApiResponse<DictType> updateType(@PathVariable Long id, @RequestBody DictType dictType) {
        if (!dictService.findTypeById(id).isPresent()) {
            return ApiResponse.error("字典类型不存在");
        }
        dictType.setId(id);
        return ApiResponse.success(dictService.saveType(dictType));
    }

    @DeleteMapping("/types/{id}")
    public ApiResponse<String> deleteType(@PathVariable Long id) {
        dictService.deleteType(id);
        return ApiResponse.success("删除成功");
    }

    // ==================== 字典数据接口 ====================

    @GetMapping("/data/{typeCode}")
    public ApiResponse<List<DictData>> getDataByTypeCode(@PathVariable String typeCode) {
        return ApiResponse.success(dictService.findDataByTypeCode(typeCode));
    }

    @GetMapping("/data/all/{typeCode}")
    public ApiResponse<List<DictData>> getAllDataByTypeCode(@PathVariable String typeCode) {
        return ApiResponse.success(dictService.findAllDataByTypeCode(typeCode));
    }

    @GetMapping("/data/item/{id}")
    public ApiResponse<DictData> getDataById(@PathVariable Long id) {
        return dictService.findDataById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("字典数据不存在"));
    }

    @PostMapping("/data")
    public ApiResponse<DictData> createData(@RequestBody DictData dictData) {
        return ApiResponse.success(dictService.saveData(dictData));
    }

    @PutMapping("/data/{id}")
    public ApiResponse<DictData> updateData(@PathVariable Long id, @RequestBody DictData dictData) {
        if (!dictService.findDataById(id).isPresent()) {
            return ApiResponse.error("字典数据不存在");
        }
        dictData.setId(id);
        return ApiResponse.success(dictService.saveData(dictData));
    }

    @DeleteMapping("/data/{id}")
    public ApiResponse<String> deleteData(@PathVariable Long id) {
        dictService.deleteData(id);
        return ApiResponse.success("删除成功");
    }

    @GetMapping("/label/{typeCode}/{dictValue}")
    public ApiResponse<String> getLabel(@PathVariable String typeCode, @PathVariable String dictValue) {
        return ApiResponse.success(dictService.getLabel(typeCode, dictValue));
    }

    @PostMapping("/init")
    public ApiResponse<String> initDefaultDicts() {
        dictService.initDefaultDicts();
        return ApiResponse.success("初始化成功");
    }
}
