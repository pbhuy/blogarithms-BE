package com.baohuy.blogarithms.service;

import com.baohuy.blogarithms.controller.BlogController;
import com.baohuy.blogarithms.exception.NotFoundException;
import com.baohuy.blogarithms.model.Blog;
import com.baohuy.blogarithms.repository.BlogRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BlogService {

    private static final Logger log = LogManager.getLogger(BlogService.class);
    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        blogRepository.save(new Blog("Making wearable medical devices more patient-friendly with Professor Esther Rodriguez-Villegas from Acurable",
                "https://techcrunch.com/wp-content/uploads/2022/05/found-2022-featured.jpg?w=430&h=230&crop=1",
                "Health",
                "Darrell Etherington",
                "October 4, 2023",
                "8 minutes",
                "Welcome back to Found, where we get the stories behind the startups. This week, our old friend Darrell Etherington joins Becca Szkutak to talk with Professor Esther Rodriguez-Villegas from Acurable...",
                "Biotech"));
        blogRepository.save(new Blog("Making wearable medical devices more patient-friendly with Professor Esther Rodriguez-Villegas from Acurable",
                "https://techcrunch.com/wp-content/uploads/2022/05/found-2022-featured.jpg?w=430&h=230&crop=1",
                "Health",
                "Darrell Etherington",
                "October 4, 2023",
                "8 minutes",
                "Welcome back to Found, where we get the stories behind the startups. This week, our old friend Darrell Etherington joins Becca Szkutak to talk with Professor Esther Rodriguez-Villegas from Acurable...",
                "Biotech"));
    }


    public Page<Blog> getBlogs(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return blogRepository.findAll(pageable);
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new NotFoundException("Blog not found with id: " + id));
    }

    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public Blog updateBlog(Long id, Blog blog) {
        Optional<Blog> foundBlog = blogRepository.findById(id);
        if (foundBlog.isPresent()) {
            Blog blogToUpdate = foundBlog.get();
            blogToUpdate.setTitle(blog.getTitle());
            blogToUpdate.setCategory(blog.getCategory());
            blogToUpdate.setContent(blog.getContent());
            blogToUpdate.setAuthor(blog.getAuthor());
            blogToUpdate.setImage(blog.getImage());
            blogToUpdate.setPublishedDate(blog.getPublishedDate());
            blogToUpdate.setTags(blog.getTags());
            blogToUpdate.setReadingTime(blog.getReadingTime());
            log.info("Updating blog with id: {}. Updated content: {}", id, blog);
            return blogRepository.save(blogToUpdate);
        } else {
            throw new NotFoundException("Blog not found with id: " + id);
        }
    }

    public Blog deleteBlog(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent()) {
            Blog blogToDelete = blog.get();
            blogRepository.delete(blogToDelete);
            return blogToDelete;
        } else {
            throw new NotFoundException("Blog not found with id: " + id);
        }
    }
}
