package com.jyh.content.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jyh.content.common.ResponseResult;
import com.jyh.content.domin.Vo.AddShareVo;
import com.jyh.content.domin.Vo.ShareVo;
import com.jyh.content.domin.enitiy.Share;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author jiangyiheng
 * @date 2022-09-06 17:14
 */
public interface ShareService {
    /**
     * 根据id查看分享
     * @param id
     * @return
     */
    Share findById(Integer id);

    List<Share> findAll();


     String getNumber(int number);

     String blockHandlerGetNumber(int number, BlockException e);


     ResponseResult checkShare(ShareVo shareVo);

    List<Share> findOneByName(String title,Integer userId, Integer pageNum,Integer pageSize);

    ResponseResult exchangeShare(Integer userId,Integer shareId,String token);

    /**
     * 进行share的投稿增加
     * @param addShareVo
     * @param userId
     * @return
     */
    ResponseResult addShare(AddShareVo addShareVo,Integer userId);

    /**
     * 查询我得兑换信息
     * @param userId
     * @return
     */
    ResponseResult findExchange(Integer userId);

    /**
     * 查询我得投稿信息
     * @param userId
     * @return
     */
    ResponseResult selectContribute(Integer userId);
}
