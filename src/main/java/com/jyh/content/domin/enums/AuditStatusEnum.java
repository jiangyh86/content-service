package com.jyh.content.domin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jiangyiheng
 * @date 2022-10-04 19:12
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {
    /**
     * 待审核
     */
    NOT_YET,
    /**
     * 审核通过
     */
    PASS,
    /**
     * 审核不通过
     */
    REJECT,
}
