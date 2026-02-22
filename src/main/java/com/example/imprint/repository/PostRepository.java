package com.example.imprint.repository;

import com.example.imprint.domain.BoardEntity;
import com.example.imprint.domain.post.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//import java.time.LocalDateTime;
//import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findByBoard(BoardEntity board, Pageable pageable);

    /*
    // 오늘 작성된 게시글 수
    long countByCreatedAtAfter(LocalDateTime startOfDay);

    // 인기 게시글 추출 (조회수 + 댓글수 가중치)
    @Query("SELECT p FROM PostEntity p " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY (p.viewCount + COUNT(c) * 2) DESC")
    List<PostEntity> findTopHotPosts(Pageable pageable);
    */
    @Modifying
    @Query("update PostEntity p set p.views = p.views + 1 where p.id = :id")
    int updateViews(@Param("id") Long id);
}