package com.jyh.content.domin.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiangyiheng
 * @date 2022-10-20 10:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeShareDto {
    /**
     * 标题
     */
    private String title;
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
}
