package com.baohuy.blogarithms.controller;

import com.baohuy.blogarithms.utils.ApiResponse;
import com.baohuy.blogarithms.model.Blog;
import com.baohuy.blogarithms.service.BlogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {
    private static final Logger log = LogManager.getLogger(BlogController.class);
    private final BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Blog>>> getBlogs(
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Blog> blogs = blogService.getBlogs(term, page, size);
        return ResponseEntity.ok(ApiResponse.successWithPagination(blogs, "All blogs retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Blog>> getBlog(@PathVariable Long id) {
        Blog blog = blogService.getBlogById(id);
        ApiResponse<Blog> response = ApiResponse.success(blog, "Blog retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Blog>> createBlog(@RequestBody Blog blog) {
        Blog createdBlog = blogService.createBlog(blog);
        ApiResponse<Blog> response = ApiResponse.success(createdBlog, "Blog created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Blog>> updateBlog(@PathVariable Long id, @RequestBody Blog blog) {
        Blog updatedBlog = blogService.updateBlog(id, blog);
        ApiResponse<Blog> response = ApiResponse.success(updatedBlog, "Blog updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Blog>> deleteBlog(@PathVariable Long id) {
        Blog deletedBlog = blogService.deleteBlog(id);
        ApiResponse<Blog> response = ApiResponse.success(deletedBlog, "Blog deleted successfully");
        return ResponseEntity.ok(response);
    }
}
