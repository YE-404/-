package com.myproject.systemdemo.mapper;

import com.myproject.systemdemo.domain.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    @Select("select username,password,id from users where username = #{username} and password = #{password}")
    @ResultMap("userResultMap")
    User select(@Param("username") String username, @Param("password") String password);

    @Select("select ${searchMes} from users where ${type} = #{content};")
    String selectMesByCondition(@Param("searchMes") String searchMes,@Param("type") String type,@Param("content") String content);

    @Select("select * from users where ${type} = #{content};")
    @ResultMap("userResultMap")
    User selectAllByCondition(@Param("type") String type,@Param("content") String content);

    @Select("select count(1) from users;")
    Integer selectUserTotalSize();

    @Select("select * from users")
    @ResultMap("userResultMap")
    List<User> selectAll();

    @Select("select username,password,id from users")
    @ResultMap("userResultMap")
    List<User> selectAllUsernameAndPasswordAndId();

    @Select("select id,train_status from users where train_status != 0;")
    @ResultMap("userResultMap")
    List<User> selectTrainStatus();

    @Insert("insert into users values(null,#{username},#{password},now(),#{avatarUrl},#{loginTimes},0)")
    @ResultMap("userResultMap")
    void add(User user);

    @Update("update users set ${type} = #{message} where id = #{id}")
    @ResultMap("userResultMap")
    void updateMessageString(@Param("id") Integer id,@Param("type") String type,@Param("message") String message);



    //------------------------------------------------Indicator-------------------------------------------------------

    @Insert("insert into ${type} values(null,#{class},now())")
    @ResultMap("userResultMap")
    void addIndicator(@Param("type") String type,@Param("class") String class_);

    @Insert("insert into ${type} values(null,#{class},#{result},now())")
    @ResultMap("userResultMap")
    void addClockIndicator(@Param("type") String type,@Param("class") String class_,@Param("result") String result);

//    @Select("select * from ${type} ")
//    @ResultMap("indicatorResultMap")
//    List<Indicator> selectAllIndicator(@Param("type") String type);


    @Select("select * from ${type} where id = #{id}")
    @ResultMap("indicatorResultMap")
    List<Indicator> selectByIdIndicator(@Param("type") String type, @Param("id") Integer id);

    @Select("select * from ${type} where class like #{class}")
    @ResultMap("indicatorResultMap")
    List<Indicator> selectByClassIndicator(@Param("type") String type, @Param("class") String class_);

    @Delete("delete from ${type} where id = #{id}")
    @ResultMap("indicatorResultMap")
    void deleteById(@Param("type") String type, @Param("id") Integer id);

    void deleteIds(@Param("type") String type, @Param("ids") Integer[] ids);

    @Select("select * from ${type} limit #{begin},#{size}")
    @ResultMap("indicatorResultMap")
    List<Indicator> selectByPage(@Param("type") String type, @Param("begin") int begin, @Param("size") int size);

    @Select("select count(*) from ${type}")
    int selectTotalCount(@Param("type") String type);


    List<Indicator> selectByPageAndCondition(@Param("type") String type, @Param("begin") int begin, @Param("size") int size, @Param("search") Search search);


    int selectTotalCountByCondition(@Param("type") String type, @Param("search") Search search);


//----------------------------------------------log-----------------------------------------------------------
    @Insert("INSERT into userlogs values(null,#{userId},#{username},now(),now(),#{fileSize},#{savePath})")
    @ResultMap("LogMap")
    void adduserLog(@Param("userId") Integer userId,@Param("username") String username,
                    @Param("fileSize") String fileSize,@Param("savePath") String savePath);

    @Update("update userlogs set change_date = now(),file_size=#{fileSize} where user_id = #{userId}")
    @ResultMap("LogMap")
    void updateUserLog(@Param("fileSize") String fileSize,@Param("userId") Integer userId);

    @Select("select * from userlogs where user_id = #{userId};")
    @ResultMap("LogMap")
    Log selectUserLogByUserId(@Param("userId") Integer userId);

    @Delete("delete from ${type} where user_id = #{userId};")
    @ResultMap("LogMap")
    void deleteUserLogByUserId(@Param("type") String type,@Param("userId") Integer userId);

    @Select("select * from ${type} limit #{begin},#{size}")
    @ResultMap("LogMap")
    List<Log> selectLogByPage(@Param("type") String type, @Param("begin") int begin, @Param("size") int size);

    List<Log> selectLogByPageAndCondition(@Param("type") String type, @Param("begin") int begin, @Param("size") int size, @Param("search") Search search);

//----------------------------------------------system--------------------------------------------------------
    @Select("select * from system where id = 1")
    @ResultMap("SystemMap")
    System getSystemMessage();

    @Update("update system set visitor_volume=#{visitorVolume},user_volume=#{userVolume},system_log_volume=#{systemLogVolume}")
    @ResultMap("SystemMap")
    void updateSystemMessage(@Param("visitorVolume") Integer visitorVolume,@Param("userVolume") Integer userVolume,@Param("systemLogVolume") Integer systemLogVolume);
//---------------------------------------------------weight-----------------------------------------------------------

    @Select("select * from ${type} where id = #{id}")
    WeightMes selectWeightById(@Param("type") String type, @Param("id") int id);

    @Select("select * from ${type} limit #{begin},#{size}")
    List<WeightMes> selectWeightMesByPage(@Param("type") String type, @Param("begin") int begin, @Param("size") int size);

    List<WeightMes> selectWeightMesByPageAndCondition(@Param("type") String type, @Param("begin") int begin, @Param("size") int size, @Param("search") Search search);

    @Insert("insert into ${type} values(null,#{category},#{cfg},#{weight},#{message},#{url},now(),#{state})")
    void addWeight(@Param("type") String type,@Param("category") String category,@Param("cfg") String cfg,@Param("weight") String weight,
                   @Param("message") String message, @Param("url") String url,@Param("state") boolean state);

    @Delete("delete from ${type} where id = #{id}")
    void deleteWeightById(@Param("type") String type, @Param("id") Integer id);

//    @Select("select ${kind} from ${type} where id = #{id}")
//    String selectWeightMesById(@Param("kind") String kind,@Param("type") String type,@Param("id") Integer id);

    @Select("select ${kind} from ${type} where ${limitCondition} = #{limitValue}")
    String selectWeightMesById(@Param("kind") String kind,@Param("type") String type,@Param("limitCondition") String limitCondition,@Param("limitValue") String limitValue);

    @Select("select ${kind} from ${type} where category = #{category} and state = #{state}")
    String selectWeightMesByCondition(@Param("kind") String kind, @Param("type") String type,
                                      @Param("category") String category, @Param("state") boolean state);

    @Update("update ${tableType} set ${changeType}=#{changeValue} where ${limitType}=#{limitValue}")
    void setState(@Param("tableType") String tableType, @Param("changeType") String changeType, @Param("changeValue") boolean changeValue,
                  @Param("limitType") String limitType, @Param("limitValue") String limitValue);

    @Update("update ${tableType} set ${changeType}=#{changeValue} where id=#{id}")
    void updateWeightMessage(@Param("tableType") String tableType, @Param("changeType") String changeType, @Param("changeValue") String changeValue,
                             @Param("id") Integer id);
}
