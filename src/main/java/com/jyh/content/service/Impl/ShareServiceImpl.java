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
        //?????????????????????????????????null
        return shareRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseResult checkShare(ShareVo shareVo) {
        Share checkShare = shareRepository.findById(shareVo.getId()).orElse(null);
        //??????share??????????????????
        if (checkShare == null) {
            throw new IllegalArgumentException("????????????????????????????????????");
//            return ResponseResult.failure(ResultCode.RESULT_CODE_DATA_NONE,"?????????????????????");
        }
        if (!Objects.equals("NOT_YET", checkShare.getAuditStatus().toString())) {
            throw new IllegalArgumentException("????????????????????????");
        }
        //????????????????????????????????????????????????????????????
        checkShare.setAuditStatus(shareVo.getAuditStatus());
        checkShare.setShowFlag(shareVo.getShowFlag());
        checkShare.setReason(shareVo.getReason());
        Share share = shareRepository.saveAndFlush(checkShare);

        //????????????????????????
        midUserShareService.insert(
                MidUserShare.builder().
                        shareId(share.getId())
                        .userId(share.getUserId())
                        .build()
        );
        //?????????pass ????????????????????????????????????
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
            //????????????????????????????????????
            list = list.stream().skip((pageNum - 1) * pageSize).limit(pageSize).
                    collect(Collectors.toList());
        } else {
            sharePage = shareRepository.findAll(pageReques);
            list = sharePage.getContent();
        }
        //????????????id????????????
        if (userId == null) {
            //?????????downloadUrl??????
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
        //??????share??????????????????
        Share share = shareRepository.findById(shareId).orElse(null);
        if (share == null) {
            throw new SecurityException("shareId????????????");
        }
        //???????????????????????????????????????
        MidUserShare midUserShare;
        Example<MidUserShare> example = Example.of(MidUserShare.builder().userId(userId).shareId(shareId).build());
        try {
            midUserShare = midUserShareRepository.findOne(example).orElse(null);
        } catch (Exception e) {
            throw new SecurityException("????????????");
        }
        if (midUserShare != null) {
            throw new IllegalArgumentException("????????????????????????");
        }
        //???????????????????????????????????????
        ResponseResult result = userService.getUser(userId,token);
        //??????fastjson???Object????????????String??????
        String string = JSONObject.toJSONString(result.getData());
        //??????fastjson???String????????????JSONObject
        JSONObject jsonObject = JSONObject.parseObject(string);
        //JSONObject ?????????java???????????????
        User user = JSONObject.toJavaObject(jsonObject, User.class);
        //???????????????????????????????????????
        if (user.getBonus() < share.getPrice()) {
            throw new IllegalArgumentException("???????????????????????????");
        }
        //??????????????????
        user.setBonus(user.getBonus()-share.getPrice());
        userService.updateUser(user,token);
        //?????????????????????
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
        //????????????
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
