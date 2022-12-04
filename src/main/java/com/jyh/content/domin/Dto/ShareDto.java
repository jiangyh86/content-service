package com.jyh.content.domin.Dto;

import com.jyh.content.domin.enitiy.Share;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiangyiheng
 * @date 2022-09-06 18:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareDto {
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像地址
     */
    private String avatar;
    private Share share;
}
