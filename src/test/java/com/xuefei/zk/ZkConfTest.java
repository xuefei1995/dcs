package com.xuefei.zk;

import com.xuefei.zk.conf.PubParam;
import com.xuefei.zk.conf.ZkConfig;
import org.junit.jupiter.api.Test;

class ZkConfTest {

    @Test
    void test() {

        //项目启动时调用该方法
        ZkConfig.asyncFlushConf();

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(PubParam.getPubParam().getLiveData());
        }
    }

}
