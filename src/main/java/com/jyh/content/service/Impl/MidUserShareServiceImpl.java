package com.jyh.content.service.Impl;

import com.jyh.content.domin.enitiy.MidUserShare;
import com.jyh.content.repository.MidUserShareRepository;
import com.jyh.content.service.MidUserShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jiangyiheng
 * @date 2022-10-06 10:11
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MidUserShareServiceImpl implements MidUserShareService {
    private final MidUserShareRepository midUserShareService;


    @Override
    public int insert(MidUserShare dto) {
        MidUserShare save = midUserShareService.save(dto);
        return  save!=null? 1: -1;
    }
}
