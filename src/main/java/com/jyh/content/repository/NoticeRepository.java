package com.jyh.content.repository;

import com.jyh.content.domin.enitiy.Notice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author jiangyiheng
 * @date 2022-09-06 18:40
 */
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    /**
     * 根据类型提交查询，根据日期排序
     * @param showFlag 是否显示
     * @param sort  排序条件
     * @return   List<Notice>
     */
    List<Notice> findByShowFlag(boolean showFlag, Sort sort);
}
