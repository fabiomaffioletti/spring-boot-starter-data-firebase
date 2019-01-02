package com.github.fabiomaffioletti.firebase.repository.test;

import com.github.fabiomaffioletti.firebase.FirebaseRealtimeDatabaseRepositoryTest;
import com.github.fabiomaffioletti.firebase.model.generatedkey.Comment;
import com.github.fabiomaffioletti.firebase.model.generatedkey.Post;
import com.github.fabiomaffioletti.firebase.repository.CommentRepository;
import com.github.fabiomaffioletti.firebase.repository.PostRepository;
import com.github.fabiomaffioletti.firebase.repository.RemoveAllRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@FirebaseRealtimeDatabaseRepositoryTest
@Import(CommentRepositoryTest.RepositoryTestConfiguration.class)
public class CommentRepositoryTest {

    @TestConfiguration
    public static class RepositoryTestConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public PostRepository postRepository() {
            return new PostRepository();
        }

        @Bean
        public CommentRepository commentRepository() {
            return new CommentRepository();
        }

        @Bean
        public RemoveAllRepository removeAllRepository() {
            return new RemoveAllRepository();
        }

    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RemoveAllRepository removeAllRepository;

    private Post post_1;

    private Post post_2;

    @Before
    public void setUp() {
        removeAllRepository.removeAll();

        post_1 = new Post();
        post_1.setTitle("My test post_1 # 1");
        post_1.setBody("My test post_1 # 1 body");
        post_1 = postRepository.push(post_1);

        post_2 = new Post();
        post_2.setTitle("My test post_2 # 2");
        post_2.setBody("My test post_2 # 2 body");
        post_2 = postRepository.push(post_2);
    }

    @Test
    public void itShouldSaveCommentsForTwoPostsWithSuccess() {
        String postId = post_1.getId();
        Comment comment = new Comment();
        comment.setContent("My test comment #1 for post_1 #1");
        comment = commentRepository.push(comment, postId);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        comment = new Comment();
        comment.setContent("My test comment #2 for post_1 #1");
        comment = commentRepository.push(comment, postId);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        List<Comment> comments = commentRepository.findAll(postId);
        assertThat(comments.size(), is(2));

        postId = post_2.getId();
        comment = new Comment();
        comment.setContent("My test comment #1 for post_2 #2");
        comment = commentRepository.push(comment, postId);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        comments = commentRepository.findAll(postId);
        assertThat(comments.size(), is(1));

        comment = commentRepository.get(comment.getId(), postId);
        assertNotNull(comment);

        try {
            commentRepository.get(comment.getId(), post_1.getId());
            fail();
        } catch(HttpClientErrorException e) {
            assertTrue(true);
        }
    }

    @Test
    public void itShouldSaveAndRemoveACommentWithSuccess() {
        String postId = post_1.getId();
        Comment comment = new Comment();
        comment.setContent("My test comment #1 for post_1 #1");
        comment = commentRepository.push(comment, postId);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        List<Comment> comments = commentRepository.findAll(postId);
        assertThat(comments.size(), is(1));

        commentRepository.remove(comment.getId(), postId);
        comments = commentRepository.findAll(postId);
        assertThat(comments.size(), is(0));
    }

    @Test
    public void itShouldSaveAndRemoveAllCommentsWithSuccess() {
        String postId = post_1.getId();
        Comment comment = new Comment();
        comment.setContent("My test comment #1 for post_1 #1");
        comment = commentRepository.push(comment, postId);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        comment.setContent("My test comment #2 for post_1 #1");
        comment = commentRepository.push(comment, postId);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        List<Comment> comments = commentRepository.findAll(postId);
        assertThat(comments.size(), is(2));

        commentRepository.removeAll(postId);
        comments = commentRepository.findAll(postId);
        assertThat(comments.size(), is(0));
    }

}
