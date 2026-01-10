package com.sms.service;

import com.sms.model.DictData;
import com.sms.model.DictType;
import com.sms.repository.DictDataRepository;
import com.sms.repository.DictTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DictService {

    @Autowired
    private DictTypeRepository dictTypeRepository;

    @Autowired
    private DictDataRepository dictDataRepository;

    // ==================== 字典类型操作 ====================

    public List<DictType> findAllTypes() {
        return dictTypeRepository.findAll();
    }

    public Optional<DictType> findTypeById(Long id) {
        return dictTypeRepository.findById(id);
    }

    public Optional<DictType> findTypeByCode(String typeCode) {
        return dictTypeRepository.findByTypeCode(typeCode);
    }

    @CacheEvict(value = "dictData", allEntries = true)
    public DictType saveType(DictType dictType) {
        if (dictType.getId() == null) {
            dictType.setCreateTime(LocalDateTime.now());
        }
        return dictTypeRepository.save(dictType);
    }

    @Transactional
    @CacheEvict(value = "dictData", allEntries = true)
    public void deleteType(Long id) {
        DictType type = dictTypeRepository.findById(id).orElse(null);
        if (type != null) {
            dictDataRepository.deleteByTypeCode(type.getTypeCode());
            dictTypeRepository.deleteById(id);
        }
    }

    public boolean existsTypeCode(String typeCode) {
        return dictTypeRepository.existsByTypeCode(typeCode);
    }

    // ==================== 字典数据操作 ====================

    @Cacheable(value = "dictData", key = "#typeCode")
    public List<DictData> findDataByTypeCode(String typeCode) {
        return dictDataRepository.findByTypeCodeAndStatusOrderBySortOrderAsc(typeCode, 0);
    }

    public List<DictData> findAllDataByTypeCode(String typeCode) {
        return dictDataRepository.findByTypeCodeOrderBySortOrderAsc(typeCode);
    }

    public Optional<DictData> findDataById(Long id) {
        return dictDataRepository.findById(id);
    }

    @CacheEvict(value = "dictData", key = "#dictData.typeCode")
    public DictData saveData(DictData dictData) {
        if (dictData.getId() == null) {
            dictData.setCreateTime(LocalDateTime.now());
        }
        return dictDataRepository.save(dictData);
    }

    @CacheEvict(value = "dictData", allEntries = true)
    public void deleteData(Long id) {
        dictDataRepository.deleteById(id);
    }

    /**
     * 根据类型编码和字典值获取标签
     */
    public String getLabel(String typeCode, String dictValue) {
        return dictDataRepository.findByTypeCodeAndDictValue(typeCode, dictValue)
                .map(DictData::getDictLabel)
                .orElse(dictValue);
    }

    /**
     * 初始化默认字典数据
     */
    @Transactional
    public void initDefaultDicts() {
        // 性别
        if (!existsTypeCode("gender")) {
            DictType genderType = new DictType();
            genderType.setTypeCode("gender");
            genderType.setTypeName("性别");
            genderType.setDescription("性别字典");
            saveType(genderType);

            saveData(createDictData("gender", "男", "1", 1));
            saveData(createDictData("gender", "女", "2", 2));
        }

        // 民族
        if (!existsTypeCode("nation")) {
            DictType nationType = new DictType();
            nationType.setTypeCode("nation");
            nationType.setTypeName("民族");
            nationType.setDescription("民族字典");
            saveType(nationType);

            String[] nations = {"汉族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族", "壮族", "布依族", "朝鲜族", "满族", "侗族", "瑶族", "白族", "土家族", "哈尼族", "哈萨克族", "傣族", "黎族", "傈僳族", "佤族", "畲族", "高山族", "拉祜族", "水族", "东乡族", "纳西族", "景颇族", "柯尔克孜族", "土族", "达斡尔族", "仫佬族", "羌族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "塔吉克族", "怒族", "乌孜别克族", "俄罗斯族", "鄂温克族", "德昂族", "保安族", "裕固族", "京族", "塔塔尔族", "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族"};
            for (int i = 0; i < nations.length; i++) {
                saveData(createDictData("nation", nations[i], String.valueOf(i + 1), i + 1));
            }
        }

        // 学历
        if (!existsTypeCode("education")) {
            DictType eduType = new DictType();
            eduType.setTypeCode("education");
            eduType.setTypeName("学历");
            eduType.setDescription("学历字典");
            saveType(eduType);

            saveData(createDictData("education", "小学", "1", 1));
            saveData(createDictData("education", "初中", "2", 2));
            saveData(createDictData("education", "高中", "3", 3));
            saveData(createDictData("education", "中专", "4", 4));
            saveData(createDictData("education", "大专", "5", 5));
            saveData(createDictData("education", "本科", "6", 6));
            saveData(createDictData("education", "硕士", "7", 7));
            saveData(createDictData("education", "博士", "8", 8));
        }

        // 政治面貌
        if (!existsTypeCode("political")) {
            DictType politicalType = new DictType();
            politicalType.setTypeCode("political");
            politicalType.setTypeName("政治面貌");
            politicalType.setDescription("政治面貌字典");
            saveType(politicalType);

            saveData(createDictData("political", "群众", "1", 1));
            saveData(createDictData("political", "共青团员", "2", 2));
            saveData(createDictData("political", "中共党员", "3", 3));
            saveData(createDictData("political", "中共预备党员", "4", 4));
            saveData(createDictData("political", "民主党派", "5", 5));
        }

        // 学生状态
        if (!existsTypeCode("student_status")) {
            DictType statusType = new DictType();
            statusType.setTypeCode("student_status");
            statusType.setTypeName("学生状态");
            statusType.setDescription("学生在校状态");
            saveType(statusType);

            saveData(createDictData("student_status", "在读", "1", 1));
            saveData(createDictData("student_status", "休学", "2", 2));
            saveData(createDictData("student_status", "退学", "3", 3));
            saveData(createDictData("student_status", "毕业", "4", 4));
        }
    }

    private DictData createDictData(String typeCode, String label, String value, int sort) {
        DictData data = new DictData();
        data.setTypeCode(typeCode);
        data.setDictLabel(label);
        data.setDictValue(value);
        data.setSortOrder(sort);
        data.setStatus(0);
        return data;
    }
}
