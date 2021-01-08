package com.xuefei.zk.conf;

import lombok.Data;

/***
 *模拟配置数据
 */
@Data
public class PubParam {

    private PubParam() {}

    private static PubParam pubParam = new PubParam();

    public static PubParam getPubParam() {
        return pubParam;
    }

    //实时数据
    private String liveData;

}
