package com.myproject.systemdemo.server;

import com.myproject.systemdemo.domain.*;
import com.myproject.systemdemo.mapper.UserMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;



//@Component("IndicatorService")
////@Repository
//@Mapper
@Service
public class IndicatorServiceImpl implements IndicatorService {

    //@Qualifier("userMapper")
    @Autowired
    private UserMapper userMapper;

    public UserMapper getUserMapper() {
        return userMapper;
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public Search<Indicator> selectByPage(String type, int currentPage, int pageSize){
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        List<Indicator> rows = userMapper.selectByPage(type,begin,size);
        int totalCount = userMapper.selectTotalCount(type);
        Search<Indicator> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }
    @Override
    public Search<Indicator> selectByPageAndCondition(String type, int currentPage, int pageSize, Search search){
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        String searchType = search.getType();
        String searchContent = search.getContent();
        if("class".equals(searchType)){
            searchContent = "%"+searchContent+"%";
        }
        search.setContent(searchContent);
        List<Indicator> rows = userMapper.selectByPageAndCondition(type,begin,size,search);
        int totalCount = userMapper.selectTotalCountByCondition(type,search);
        Search<Indicator> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }
    @Override
    public Search<Log> selectLogByPage(String type, int currentPage, int pageSize){
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        List<Log> rows = userMapper.selectLogByPage(type,begin,size);
        int totalCount = userMapper.selectTotalCount(type);
        Search<Log> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }
    @Override
    public Search<Log> selectLogByPageAndCondition(String type, int currentPage, int pageSize, Search search){
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        String searchType = search.getType();
        String searchContent = search.getContent();
        if("class".equals(searchType)){
            searchContent = "%"+searchContent+"%";
        }
        search.setContent(searchContent);
        List<Log> rows = userMapper.selectLogByPageAndCondition(type,begin,size,search);
        int totalCount = userMapper.selectTotalCountByCondition(type,search);
        Search<Log> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }

    public Search<WeightMes> selectWeightMesByPage(String type, int currentPage, int pageSize){
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        List<WeightMes> rows = userMapper.selectWeightMesByPage(type,begin,size);
        int totalCount = userMapper.selectTotalCount(type);
        Search<WeightMes> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }

    public Search<WeightMes> selectWeightMesByPageAndCondition(String type, int currentPage, int pageSize, Search search){
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        String searchType = search.getType();
        String searchContent = search.getContent();
        if("type".equals(searchType)){
            searchContent = "%"+searchContent+"%";
        }
        search.setContent(searchContent);
        List<WeightMes> rows = userMapper.selectWeightMesByPageAndCondition(type,begin,size,search);
        int totalCount = userMapper.selectTotalCountByCondition(type,search);
        Search<WeightMes> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }
    public Search<Task> selectTaskByPage(String type, int currentPage, int pageSize){
        int begin = (currentPage -1) * pageSize;
        System.out.println(begin);
        System.out.println(pageSize);
        List<Task> rows = userMapper.selectTaskByPage(type,begin,pageSize);
        System.out.println(rows);
        int totalCount = userMapper.selectTotalCount(type);
        Search<Task> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }


    public Search<Apparatus> selectApparatusMesByPageAndCondition(String type, int currentPage, int pageSize, Search search){
        int begin = (currentPage -1) * pageSize;
        System.out.println(search);
        if(search != null){
            String searchType = search.getType();
            String searchContent = search.getContent();
            if("TaskName".equals(searchType)){
                searchContent = "%"+searchContent+"%";
            }
            search.setContent(searchContent);
        }else{
            search = new Search();

        }


        List<Apparatus> rows = userMapper.selectApparatusByPageAndCondition(type,begin, pageSize,search);
        int totalCount = userMapper.selectTotalCountByCondition(type,search);
        Search<Apparatus> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        return searches;
    }



}
