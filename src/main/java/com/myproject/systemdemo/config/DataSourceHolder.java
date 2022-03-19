package com.myproject.systemdemo.config;


public class DataSourceHolder {
    //线程本地环境
    private static final ThreadLocal<String> dataSources = new ThreadLocal<String>();
    //设置数据源,动态切换,就是调用这个setDataSource方法
    public static void setDataSource(String customerType) {
        //System.out.println("ThreadLocal_before");
        dataSources.set(customerType);
        //System.out.println("ThreadLocal_after");
    }
    //获取数据源
    public static String getDataSource() {
        return (String) dataSources.get();
    }
    //清除数据源
    public static void clearDataSource() {
        dataSources.remove();
    }
}
