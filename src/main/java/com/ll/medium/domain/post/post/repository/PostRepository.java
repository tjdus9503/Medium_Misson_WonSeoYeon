package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop30ByIsPublishedTrue(Sort sort);

    Page<Post> findByIsPublishedTrue(Pageable pageable);

    Page<Post> findByAuthor(Member author, Pageable pageable);

    Page<Post> findByAuthorAndIsPublishedTrue(Member author, Pageable pageable);
}
