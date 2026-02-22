package com.example.maternal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.maternal.entity.DailyRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DailyRecordMapper extends BaseMapper<DailyRecord> {
}