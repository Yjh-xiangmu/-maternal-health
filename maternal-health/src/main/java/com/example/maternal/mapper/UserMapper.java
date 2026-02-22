package com.example.maternal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.maternal.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据库操作接口
 * 继承 BaseMapper 后，这就自动拥有了增删改查的能力，不需要写 SQL
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}