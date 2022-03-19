package com.myproject.systemdemo.server;

import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Log;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.mapper.UserMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface IndicatorService {
    public Search<Indicator> selectByPage(String type, int currentPage, int pageSize);
    public Search<Indicator> selectByPageAndCondition(String type, int currentPage, int pageSize, Search search);
    public Search<Log> selectLogByPage(String type, int currentPage, int pageSize);
    public Search<Log> selectLogByPageAndCondition(String type, int currentPage, int pageSize, Search search);
}
