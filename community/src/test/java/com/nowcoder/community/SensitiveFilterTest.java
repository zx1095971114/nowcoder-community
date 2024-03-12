package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/11  22:23
 * @description :
 **/

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitiveFilter(){
        String text = "赌？？？？？赌博";
        text = sensitiveFilter.filterSensitiveWords(text);
        System.out.println(text);

        text = "新葡京澳门赌场，可以@赌@博@，@嫖@娼@，@博@彩@";
        text = sensitiveFilter.filterSensitiveWords(text);
        System.out.println(text);
    }
}
