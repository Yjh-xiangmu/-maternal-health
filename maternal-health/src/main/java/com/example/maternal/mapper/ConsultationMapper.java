package com.example.maternal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.maternal.entity.ConsultationOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsultationMapper extends BaseMapper<ConsultationOrder> {
}