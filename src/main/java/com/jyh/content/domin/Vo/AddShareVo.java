package com.jyh.content.domin.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author jiangyiheng
 * @date 2022-10-20 10:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddShareVo {
    /**
     * 标题
     */
    private String title;
    /**
     * 是否原创 0:否 1:是
     */
    private Integer isOriginal;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面
     */
    private String cover;

    /**
     * 概要信息
     */
    private String summary;

    /**
     * 价格（需要的积分）
     */
    private Integer price;

    /**
     * 下载地址
     */
    private String downloadUrl;
}
