package com.jyh.content.domin.enitiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author jiangyiheng
 * @date 2022-09-06 18:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notice")
public class Notice {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否显示 0:否 1:是
     */
    @Column(name = "show_flag")
    private Boolean showFlag;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
}
