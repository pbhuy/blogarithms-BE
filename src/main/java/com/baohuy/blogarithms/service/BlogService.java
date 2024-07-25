package com.baohuy.blogarithms.service;

import com.baohuy.blogarithms.exception.NotFoundException;
import com.baohuy.blogarithms.model.Blog;
import com.baohuy.blogarithms.repository.BlogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    private static final Logger log = LogManager.getLogger(BlogService.class);
    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public static List<Blog> readJsonToBlogs(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(inputStream);

        List<Blog> blogs = new ArrayList<>();

        if (rootNode.isArray()) {
            for (JsonNode blogNode : rootNode) {
                blogs.add(parseBlogFromJsonNode(blogNode));
            }
        } else {
            blogs.add(parseBlogFromJsonNode(rootNode));
        }

        return blogs;
    }

    private static Blog parseBlogFromJsonNode(JsonNode blogNode) {
        Blog blog = new Blog();
        blog.setId(blogNode.get("id").asLong());
        blog.setTitle(blogNode.get("title").asText());
        blog.setImage(blogNode.get("image").asText());
        blog.setCategory(blogNode.get("category").asText());
        blog.setAuthor(blogNode.get("author").asText());
        blog.setPublishedDate(blogNode.get("published_date").asText());
        blog.setReadingTime(blogNode.get("reading_time").asText());
        blog.setContent(blogNode.get("content").asText());

        // Handle tags
        List<String> tags = new ObjectMapper().convertValue(blogNode.get("tags"), new TypeReference<List<String>>() {});
        blog.setTags(String.join(",", tags));

        return blog;
    }

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/blogs.json")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("blogs.json not found!");
            }
            log.info("Loading blogs.json file");
            List<Blog> blogs = readJsonToBlogs(inputStream);
            blogRepository.saveAll(blogs);
            log.info("Loaded {} blogs from blogs.json", blogs.size());
        } catch (IOException e) {
            log.error("Error loading blogs.json", e);
            throw e;
        }
    }


    public Page<Blog> getBlogs(String term, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (term == null || term.isEmpty()) {
            return blogRepository.findAll(pageable);
        } else {
            return blogRepository.findAll((Specification<Blog>) (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + term.toLowerCase() + "%"));
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }, pageable);
        }
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
