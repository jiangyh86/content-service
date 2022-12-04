package com.jyh.content.openfeign;

import com.jyh.content.common.ResponseResult;
import com.jyh.content.domin.enitiy.User;
import com.jyh.content.openfeign.fallback.UserServiceFallBackImpl;
//import com.jyh.content.openfeign.fallback.UserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiangyiheng
 * @date 2022-09-06 18:19
 */
@FeignClient(value = "user-service",path = "/users",fallback = UserServiceFallBackImpl.class)
//@FeignClient(value = "user-service",path = "/users",fallbackFactory = UserServiceFallbackFactory.class)
public interface UserService {
    /**
     * 调用user模块服务
     * @param id
     * @return
     */
    @GetMapping("{id}")
    ResponseResult getUser(@PathVariable("id") int id,@RequestHeader("x-token") String token);


    @PostMapping("/update")
     ResponseResult updateUser(@RequestBody User user,@RequestHeader("x-token") String token);
}
