package com.hqz.hzuoj.judge;

import com.hqz.hzuoj.HzuojJudgeServiceApplication;
import com.hqz.hzuoj.judge.mq.MessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = HzuojJudgeServiceApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HzuojJudgeServiceApplicationTests {


    @Test
    public void test() {

    }

}
