package com.jyh.content.service.Impl;

import com.jyh.content.domin.enitiy.Share;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jiangyiheng
 * @date 2022-10-04 20:56
 */
@SpringBootTest
class ShareServiceImplTest {

    @Resource
    private ShareServiceImpl shareServiceUrl;

    @Test
    void findOneByName() {
//        System.out.println(shareServiceUrl.findOneByName( 4, 4));
    }
}