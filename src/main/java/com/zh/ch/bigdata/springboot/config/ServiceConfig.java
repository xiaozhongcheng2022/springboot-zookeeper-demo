package com.zh.ch.bigdata.springboot.config;

public class ServiceConfig {

    //服务注册的根路径
    public static final String REGISTER_ROOT_PATH = "/apps";

    private String desc;
    private int weight;

    @Override
    public String toString() {
        return "ServiceDetail{" +
                "desc='" + desc + '\'' +
                ", weight=" + weight +
                '}';
    }

    public ServiceConfig() {
    }

    public ServiceConfig(String desc, int weight) {
        this.desc = desc;
        this.weight = weight;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
