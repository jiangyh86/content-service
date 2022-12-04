package com.jyh.content.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jyh.content.common.ResponseResult;
import com.jyh.content.common.ResultCode;
import com.jyh.content.domin.enitiy.Notice;
import com.jyh.content.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiangyiheng
 * @date 2022-09-06 19:25
 */
@RestController
@Slf4j
@RequestMapping("/notices")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NoticeController {
    private final NoticeService noticeService;

    @Value("${disableNoticeRequest:false}")
    private boolean  disableNotice;

    @GetMapping("/latest")
    @SentinelResource(value = "getNotice",blockHandler = "getNoticeBlock")
    public ResponseResult getLatestNotice() {
        System.out.println(disableNotice);
        Notice notice = noticeService.getLatestNotice();
        return notice==null? ResponseResult.failure(ResultCode.RESULT_CODE_DATA_NONE) : ResponseResult.success(notice);
    }

    public ResponseResult getNoticeBlock(BlockException exception){
        log.info("接口限流");
        log.info(exception.getMessage());
        return ResponseResult.failure(ResultCode.INTERFACE_EXCEED_LOAD);
    }
}
