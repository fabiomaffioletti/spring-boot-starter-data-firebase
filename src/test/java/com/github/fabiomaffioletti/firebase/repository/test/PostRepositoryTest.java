package com.github.fabiomaffioletti.firebase.repository.test;

import com.github.fabiomaffioletti.firebase.FirebaseRealtimeDatabaseRepositoryTest;
import com.github.fabiomaffioletti.firebase.model.generatedkey.Post;
import com.github.fabiomaffioletti.firebase.model.generatedkey.Tag;
import com.github.fabiomaffioletti.firebase.repository.Filter;
import com.github.fabiomaffioletti.firebase.repository.PostRepository;
import com.github.fabiomaffioletti.firebase.repository.RemoveAllRepository;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@FirebaseRealtimeDatabaseRepositoryTest
@Import(PostRepositoryTest.RepositoryTestConfiguration.class)
public class PostRepositoryTest {

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
        public RemoveAllRepository removeAllRepository() {
            return new RemoveAllRepository();
        }

    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RemoveAllRepository removeAllRepository;

    @Before
    public void setUp() {
        removeAllRepository.removeAll();
    }

    @Test
    public void itShouldSaveAndUpdateAndRetrieveAPostWithSuccess() {
        Post post = new Post();
        post.setTitle("My test post # 1");
        post.setBody("My test post # 1 body");
        post = postRepository.push(post);
        assertNotNull(post);
        assertNotNull(post.getId());

        post = postRepository.get(post.getId());
        assertThat(post.getTitle(), is("My test post # 1"));
        assertNull(post.getTags());

        Tag tagOne = new Tag();
        tagOne.setName("tag_one");
        Tag tagTwo = new Tag();
        tagTwo.setName("tag_two");
        post.setTitle("My updated title # 1");
        post.setTags(Lists.newArrayList(tagOne, tagTwo));
        post = postRepository.update(post);
        assertThat(post.getTitle(), is("My updated title # 1"));
        assertThat(post.getTags().size(), is(2));

        List<Post> posts = postRepository.findAll();
        assertThat(posts.size(), is(1));

        posts = postRepository.find(Filter.FilterBuilder.builder().orderBy("title").limitToFirst(1).build());
        assertThat(posts.size(), is(1));
    }

    @Test
    public void itShouldSaveAndRetrieveAListOfPostWithSuccess() {
        IntStream.range(1, 20).boxed().forEach(i -> {
            Post post = new Post();
            post.setTitle(String.format("My test post # %s", i));
            post.setBody(String.format("My test post # %s body", i));
            postRepository.push(post);
        });

        List<Post> posts = postRepository.find(Filter.FilterBuilder.builder().orderBy("title").limitToFirst(5).build());
        assertThat(posts.size(), is(5));
    }

}