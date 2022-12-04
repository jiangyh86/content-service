package com.jyh.content.domin.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiangyiheng
 * @date 2022-10-06 10:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddBounds {
    private Integer userId;
    private Integer bounds;
}
