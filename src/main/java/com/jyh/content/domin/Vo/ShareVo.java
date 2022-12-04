package com.jyh.content.domin.Vo;

import com.jyh.content.domin.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author jiangyiheng
 * @date 2022-10-04 19:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareVo {
    /**
     * id
     */
    private Integer id;
    /**
     * 是否显示 0:否 1:是
     */
    private Integer showFlag;

    /**
     * 审核状态 NOT_YET: 待审核 PASSED:审核通过 REJECTED:审核不通过
     */
    private AuditStatusEnum auditStatus;

    /**
     * 审核不通过原因
     */
    private String reason;
}
