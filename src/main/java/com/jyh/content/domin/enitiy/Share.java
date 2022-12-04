package com.jyh.content.domin.enitiy;

import com.jyh.content.domin.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import java.util.Date;

/**
 * @author jiangyiheng
 * @date 2022-09-06 17:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="share")
@Builder
public class Share {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 发布人id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 是否原创 0:否 1:是
     */
    @Column(name = "is_original")
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
    @Column(name = "download_url")
    private String downloadUrl;

    /**
     * 下载数
     */
    @Column(name = "buy_count")
    private Integer buyCount;

    /**
     * 是否显示 0:否 1:是
     */
    @Column(name = "show_flag")
    private Integer showFlag;

    /**
     * 审核状态 NOT_YET: 待审核 PASSED:审核通过 REJECTED:审核不通过
     */
    @Column(name = "audit_status")
    @Enumerated(EnumType.STRING)
    private AuditStatusEnum auditStatus;

    /**
     * 审核不通过原因
     */
    private String reason;
}
