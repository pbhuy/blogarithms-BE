package com.baohuy.blogarithms.repository;

import com.baohuy.blogarithms.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
