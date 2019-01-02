package com.github.fabiomaffioletti.firebase.repository.test;

import com.github.fabiomaffioletti.firebase.FirebaseRealtimeDatabaseRepositoryTest;
import com.github.fabiomaffioletti.firebase.model.definedkey.Author;
import com.github.fabiomaffioletti.firebase.repository.AuthorRepository;
import com.github.fabiomaffioletti.firebase.repository.RemoveAllRepository;
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@FirebaseRealtimeDatabaseRepositoryTest
@Import(AuthorRepositoryTest.RepositoryTestConfiguration.class)
public class AuthorRepositoryTest {

    @TestConfiguration
    public static class RepositoryTestConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public AuthorRepository authorRepository() {
            return new AuthorRepository();
        }

        @Bean
        public RemoveAllRepository removeAllRepository() {
            return new RemoveAllRepository();
        }

    }

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private RemoveAllRepository removeAllRepository;

    @Before
    public void setUp() {
        removeAllRepository.removeAll();
    }

    @Test
    public void itShouldSaveAuthorsWithSuccessAsAList() {
        Author author = new Author();
        author.setId(1L);
        author.setName("author # 1");
        author = authorRepository.set(author);
        assertNotNull(author);

        author = new Author();
        author.setId(2L);
        author.setName("author # 2");
        author = authorRepository.set(author);
        assertNotNull(author);

        author = authorRepository.get(1L);
        assertNotNull(author);
        assertThat(author.getName(), is("author # 1"));

        author = new Author();
        author.setId(3000L);
        author.setName("author # 3");
        author = authorRepository.set(author);
        assertNotNull(author);

        List<Author> authors = authorRepository.findAll();
        assertThat(authors.size(), is(3));
    }

    @Test
    public void itShouldSaveAuthorsWithSuccessAsAnArray() {
        Author author = new Author();
        author.setId(1L);
        author.setName("author # 1");
        author = authorRepository.set(author);
        assertNotNull(author);

        author = new Author();
        author.setId(2L);
        author.setName("author # 2");
        author = authorRepository.set(author);
        assertNotNull(author);

        author = authorRepository.get(1L);
        assertNotNull(author);
        assertThat(author.getName(), is("author # 1"));

        author = new Author();
        author.setId(3L);
        author.setName("author # 3");
        author = authorRepository.set(author);
        assertNotNull(author);

        List<Author> authors = authorRepository.findAll();
        assertThat(authors.size(), is(4));
    }

}
