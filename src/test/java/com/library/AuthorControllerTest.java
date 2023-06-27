package com.library;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    @Before
    public void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    public void createAuthor_ShouldReturnCreatedAuthor() {
        // given
        AuthorDto authorDto = new AuthorDto(null, "John Doe");

        // when
        ResponseEntity<AuthorDto> response = restTemplate.postForEntity("/api/v1/authors", authorDto, AuthorDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(authorDto.getName());
    }

    @Test
    public void getAuthorById_ShouldReturnAuthorWithGivenId() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);

        // when
        ResponseEntity<AuthorDto> response = restTemplate.getForEntity("/api/v1/authors/" + author.getId(), AuthorDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(author.getId());
        assertThat(response.getBody().getName()).isEqualTo(author.getName());
    }

    @Test
    public void getAllAuthors_ShouldReturnAllAuthors() {
        // given
        Author author1 = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author1);
        Author author2 = new Author(null, "Jane Doe", new ArrayList<>());
        authorRepository.save(author2);

        // when
        ResponseEntity<AuthorDto[]> response = restTemplate.getForEntity("/api/v1/authors", AuthorDto[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

    }

    @Test
    public void updateAuthor_ShouldReturnUpdatedAuthor() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);
        AuthorDto updatedAuthorDto = new AuthorDto(author.getId(), "Updated Name");

        // when
        restTemplate.put("/api/v1/authors/" + author.getId(), updatedAuthorDto);
        ResponseEntity<AuthorDto> response = restTemplate.getForEntity("/api/v1/authors/" + author.getId(), AuthorDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(updatedAuthorDto.getId());
        assertThat(response.getBody().getName()).isEqualTo(updatedAuthorDto.getName());
    }

    @Test
    public void deleteAuthor_ShouldDeleteAuthorWithGivenId() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);

        // when
        restTemplate.delete("/api/v1/authors/" + author.getId());
        ResponseEntity<AuthorDto> response = restTemplate.getForEntity("/api/v1/authors/" + author.getId(), AuthorDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
