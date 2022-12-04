package com.jyh.content.service.Impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.jyh.content.common.ResponseResult;
import com.jyh.content.common.ResultCode;
import com.jyh.content.domin.Dto.ExchangeShareDto;
import com.jyh.content.domin.Dto.UserAddBounds;
import com.jyh.content.domin.Vo.AddShareVo;
import com.jyh.content.domin.Vo.ShareVo;
import com.jyh.content.domin.enitiy.MidUserShare;
import com.jyh.content.domin.enitiy.Share;
import com.jyh.content.domin.enitiy.User;
import com.jyh.content.domin.enums.AuditStatusEnum;
import com.jyh.content.openfeign.UserService;
import com.jyh.content.repository.MidUserShareRepository;
import com.jyh.content.repository.ShareRepository;
import com.jyh.content.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jiangyiheng
 * @date 2022-09-06 17:17
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareServiceImpl implements ShareService {

    private final MidUserShareServiceImpl midUserShareService;

    private final RocketMQTemplate rocketMQTemplate;

    private final ShareRepository shareRepository;

    private final MidUserShareRepository midUserShareRepository;

    private final UserService userService;

    @Override
    public String blockHandlerGetNumber(int number, BlockException e) {
        return "BLOCKED";
    }

    @Override
    public List<Share> findAll() {
        return shareRepository.findAll();
    }

    @Override
    public Share findById(Integer id) {
        //如果查询结果为空，返回null
        return shareRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseResult checkShare(ShareVo shareVo) {
        Share checkShare = shareRepository.findById(shareVo.getId()).orElse(null);
        //检查share内容是否存在
        if (checkShare == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在！");
//            return ResponseResult.failure(ResultCode.RESULT_CODE_DATA_NONE,"分享内容不存在");
        }
        if (!Objects.equals("NOT_YET", checkShare.getAuditStatus().toString())) {
            throw new IllegalArgumentException("该分享已经被审核");
        }
        //审核资源，更改审核状态更新原因和是否显示
        checkShare.setAuditStatus(shareVo.getAuditStatus());
        checkShare.setShowFlag(shareVo.getShowFlag());
        checkShare.setReason(shareVo.getReason());
        Share share = shareRepository.saveAndFlush(checkShare);

        //向中间表加入数据
        midUserShareService.insert(
                MidUserShare.builder().
                        shareId(share.getId())
                        .userId(share.getUserId())
                        .build()
        );
        //如果为pass 则发送消息让用户中心消费
        if (AuditStatusEnum.PASS.equals(shareVo.getAuditStatus())) {
            rocketMQTemplate.convertAndSend(
                    "add-bounds-jyh",
                    UserAddBounds.builder()
                            .userId(share.getUserId())
                            .bounds(50)
                            .build()
            );
        }
        return ResponseResult.success(share);
    }

    @Override
    public List<Share> findOneByName(String title, Integer userId, Integer pageNum, Integer pageSize) {
        PageRequest pageReques = PageRequest.of(pageNum, pageSize);
        Page<Share> sharePage;
        List<Share> list;
        if (title != null) {
            log.info(title);
            list = shareRepository.findByTitleLike(title);
            //对模糊查询的结果进行分页
            list = list.stream().skip((pageNum - 1) * pageSize).limit(pageSize).
                    collect(Collectors.toList());
        } else {
            sharePage = shareRepository.findAll(pageReques);
            list = sharePage.getContent();
        }
        //判断用户id是否存在
        if (userId == null) {
            //把所有downloadUrl置空
            list.forEach(item -> item.setDownloadUrl(""));
        } else {
            list.forEach(item -> {
                Example<MidUserShare> example = Example.of(MidUserShare.builder().userId(item.getUserId()).build());
                List<MidUserShare> userShare = midUserShareRepository.findAll(example);
                if (userShare == null) {
                    item.setDownloadUrl("");
                }
            });
        }

        return list;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseResult exchangeShare(Integer userId, Integer shareId,String token) {
        //判断share信息是否存在
        Share share = shareRepository.findById(shareId).orElse(null);
        if (share == null) {
            throw new SecurityException("shareId参数非法");
        }
        //查询中间表是否有过兑换记录
        MidUserShare midUserShare;
        Example<MidUserShare> example = Example.of(MidUserShare.builder().userId(userId).shareId(shareId).build());
        try {
            midUserShare = midUserShareRepository.findOne(example).orElse(null);
        } catch (Exception e) {
            throw new SecurityException("参数有误");
        }
        if (midUserShare != null) {
            throw new IllegalArgumentException("已经兑换过该分享");
        }
        //查询用户的积分是否足够兑换
        ResponseResult result = userService.getUser(userId,token);
        //使用fastjson将Object类型转为String类型
        String string = JSONObject.toJSONString(result.getData());
        //使用fastjson将String类型转为JSONObject
        JSONObject jsonObject = JSONObject.parseObject(string);
        //JSONObject 转换为java实体类对象
        User user = JSONObject.toJavaObject(jsonObject, User.class);
        //检查用户的积分是否足够兑换
        if (user.getBonus() < share.getPrice()) {
            throw new IllegalArgumentException("积分不足，无法兑换");
        }
        //进行积分兑换
        user.setBonus(user.getBonus()-share.getPrice());
        userService.updateUser(user,token);
        //修改被兑换次数
        share.setBuyCount(share.getBuyCount()+1);
        shareRepository.saveAndFlush(share);
        MidUserShare midUserShare1 = midUserShareRepository.save(MidUserShare.builder().userId(userId).shareId(shareId).build());
        return ResponseResult.success(midUserShare1!=null? share:null);
    }

    @Override
    public ResponseResult addShare(AddShareVo addShareVo,Integer userId) {
        Share share = new Share();
        BeanUtils.copyProperties(addShareVo,share);
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setBuyCount(0);
        share.setUserId(userId);
        share.setShowFlag(0);
        share.setAuditStatus(AuditStatusEnum.NOT_YET);
        share.setReason("");
        Share save = shareRepository.save(share);
        return save!=null? ResponseResult.success(save):ResponseResult.failure(ResultCode.DATABASE_ERROR);
    }

    @Override
    public ResponseResult findExchange(Integer userId) {
        List<ExchangeShareDto> exchangeShares = new ArrayList<>();
        //查询信息
        Example<MidUserShare> example = Example.of(MidUserShare.builder().userId(userId).build());
        List<MidUserShare> all = midUserShareRepository.findAll(example);
        all.forEach(item->{
            ExchangeShareDto shareDto = new ExchangeShareDto();
            log.info(item.getShareId().toString());
            Share share = shareRepository.findById(item.getShareId()).orElse(null);
            BeanUtils.copyProperties(share,shareDto);
            exchangeShares.add(shareDto);
        });
        return ResponseResult.success(exchangeShares);
    }

    @Override
    public ResponseResult selectContribute(Integer userId) {
        Example<Share> example = Example.of(Share.builder().userId(userId).build());
        List<Share> all = shareRepository.findAll(example);
        return ResponseResult.success(all);
    }

    @Override
    @SentinelResource(value = "getNumber", blockHandler = "blockHandlerGetNumber")
    public String getNumber(int number) {
        return "number = {" + number + "}";
    }


}
