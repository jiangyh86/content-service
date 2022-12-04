package com.jyh.content.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.jyh.content.auth.CheckAuthorization;
import com.jyh.content.auth.CheckLogin;
import com.jyh.content.common.ResponseResult;
import com.jyh.content.common.ResultCode;
import com.jyh.content.domin.Dto.ShareDto;
import com.jyh.content.domin.Vo.AddShareVo;
import com.jyh.content.domin.Vo.ShareVo;
import com.jyh.content.domin.enitiy.Share;
import com.jyh.content.domin.enitiy.User;
import com.jyh.content.openfeign.UserService;
import com.jyh.content.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jiangyiheng
 * @date 2022-09-06 17:20
 */
@RestController
@Slf4j
@RequestMapping("/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {
    private final ShareService shareService;

    private final UserService userService;


    @GetMapping("{id}")
    @CheckLogin
//    @SentinelResource(value = "getShareById")
    @SentinelResource(value = "getShareById", blockHandler = "getAllSharesBlock")
    public ResponseResult getShareById(@PathVariable Integer id, HttpServletRequest request) {
        String token = request.getHeader("x-token");
        Share share = shareService.findById(id);
        ResponseResult result = userService.getUser(share.getUserId(), token);
        //使用fastjson将Object类型转为String类型
        String string = JSONObject.toJSONString(result.getData());
        //使用fastjson将String类型转为JSONObject
        JSONObject jsonObject = JSONObject.parseObject(string);
        //JSONObject 转换为java实体类对象
        User user = JSONObject.toJavaObject(jsonObject, User.class);
        ShareDto shareDto = ShareDto.builder().share(share).avatar(user.getAvatar()).nickname(user.getNickname()).build();
//            ShareDto shareDto = ShareDto.builder().share(share).avatar("user.getAvatar()").nickname("user.getNickname()").build();
        return share == null ? ResponseResult.failure(ResultCode.RESULT_CODE_DATA_NONE) : ResponseResult.success(shareDto);
//        }
    }

    @GetMapping("/all")
//    @SentinelResource(value = "getAllShares")
    @SentinelResource(value = "getAllShares", blockHandler = "getAllSharesBlock")
    @CheckLogin
    public ResponseResult getAllShares(HttpServletRequest request,
                                       @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "6") Integer pageSize,
                                       @RequestParam(value = "title", required = false) String title) {
        String str = (String) request.getAttribute("id");
        Integer userId;
        if (str == null) {
            userId = null;
        } else {
            userId = Integer.parseInt(str);
        }
        log.info("userId:" + userId);
        if (pageNum == 0 || pageSize == 6) {
            //默认查询所有
            List<Share> all = shareService.findAll();
            return ResponseResult.success(all);
        }
        List<Share> shares = shareService.findOneByName(title, userId, pageNum, pageSize);
        return ResponseResult.success(shares);
    }

    @CheckLogin
    @CheckAuthorization("admin")
    @PostMapping("/update")
    public ResponseResult saveShares(@RequestBody ShareVo share) {
        return shareService.checkShare(share);
    }


    @ResponseBody
    public ResponseResult getAllSharesBlock(BlockException exception) {
        log.info("接口限流");
        log.info(exception.getMessage());
        return ResponseResult.failure(ResultCode.INTERFACE_EXCEED_LOAD);
    }

    @CheckLogin
    @GetMapping("/exchange")
    public ResponseResult exchange(@RequestParam("shareId") Integer shareId, HttpServletRequest request) {
        String str = (String) request.getAttribute("id");
        Integer userId;
        userId = str == null ? null : Integer.parseInt(str);
        String token = request.getHeader("x-token");
        return shareService.exchangeShare(userId, shareId, token);
    }

    @CheckLogin
    @PostMapping("/addShare")
    public ResponseResult addShare(HttpServletRequest request,@RequestBody AddShareVo addShare) {
        String str = (String) request.getAttribute("id");
        Integer userId;
        userId = str == null ? null : Integer.parseInt(str);
        return shareService.addShare(addShare,userId);
    }
    @CheckLogin
    @GetMapping("/findExchange")
    public ResponseResult findExchange(HttpServletRequest request){
        String str = (String) request.getAttribute("id");
        Integer userId;
        userId = str == null ? null : Integer.parseInt(str);
        return shareService.findExchange(userId);
    }

    @CheckLogin
    @GetMapping("/selectContribute")
    public ResponseResult selectContribute(HttpServletRequest request){
        String str = (String) request.getAttribute("id");
        Integer userId;
        userId = str == null ? null : Integer.parseInt(str);
        return shareService.selectContribute(userId);
    }
}
