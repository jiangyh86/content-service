package com.jyh.content.repository;

import com.jyh.content.domin.enitiy.Share;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author jiangyiheng
 * @date 2022-09-06 17:15
 */
public interface ShareRepository extends JpaRepository<Share, Integer>, JpaSpecificationExecutor<Share> {
    /**
     * 对标题进行模糊查询,nativeQuery = true
     * @param title
     * @return
     */
    @Query(value = "SELECT s FROM Share s  where s.title like %:title% or  s.author like %:title% or s.summary like %:title%")
    List<Share> findByTitleLike(@Param("title")String title);


    /**
     * 模糊加分页查询
     * @param title
     * @param pageable
     * @return
     */
    Page<Share> findByTitleContaining(String title, Pageable pageable);
}
