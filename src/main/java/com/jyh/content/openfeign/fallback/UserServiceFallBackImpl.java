package com.jyh.content.openfeign.fallback;

import com.jyh.content.common.ResponseResult;
import com.jyh.content.domin.enitiy.User;
import com.jyh.content.openfeign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author jiangyiheng
 * @date 2022-09-08 9:03
 */
@Slf4j
@Component
public class UserServiceFallBackImpl implements UserService {
    @Override
    public ResponseResult getUser(int id,@RequestHeader("x-token") String token) {
        log.info("fallback getUser");
        User user = new User();
        user.setNickname("降级方案返回用户");
        return ResponseResult.success(user);
    }

    @Override
    public ResponseResult updateUser(User user,@RequestHeader("x-token") String token) {
        log.info("fallback getUser");
        throw new IllegalArgumentException("兑换失败，修改积分失败");
    }
}
