package com.library;


import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();
    }

    private AuthorDto createAuthorDto(String name) {
        return new AuthorDto(null, name);
    }

    private Response postAuthor(AuthorDto authorDto) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(authorDto)
                .when()
                .post("/api/v1/authors");
    }

    private Response getAuthorById(Long id) {
        return given()
                .port(port)
                .when()
                .get("/api/v1/authors/" + id);
    }

    private Response getAllAuthors() {
        return given()
                .port(port)
                .when()
                .get("/api/v1/authors");
    }

    private Response updateAuthor(AuthorDto authorDto) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(authorDto)
                .when()
                .put("/api/v1/authors/" + authorDto.getId());
    }

    private Response deleteAuthor(Long id) {
        return given()
                .port(port)
                .when()
                .delete("/api/v1/authors/" + id);
    }

    @Test
    @DisplayName("Create author should return created author")
    public void createAuthor_ShouldReturnCreatedAuthor() {
        // given
        AuthorDto authorDto = createAuthorDto("John Doe");

        // when
        Response response = postAuthor(authorDto);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("id")).isNotNull();
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(authorDto.getName());
    }

    @Test
    @DisplayName("Get author by id should return author with given id")
    public void getAuthorById_ShouldReturnAuthorWithGivenId() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);

        // when
        Response response = getAuthorById(author.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(author.getId());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(author.getName());
    }

    @Test
    @DisplayName("Get all authors should return all authors")
    public void getAllAuthors_ShouldReturnAllAuthors() {
        // given
        Author author1 = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author1);
        Author author2 = new Author(null, "Jane Doe", new ArrayList<>());
        authorRepository.save(author2);

        // when
        Response response = getAllAuthors();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList("")).hasSize(2);

    }

    @Test
    @DisplayName("Update author should return updated author")
    public void updateAuthor_ShouldReturnUpdatedAuthor() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);
        AuthorDto updatedAuthorDto = new AuthorDto(author.getId(), "Updated Name");

        // when
        updateAuthor(updatedAuthorDto);

        Response response = getAuthorById(updatedAuthorDto.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(updatedAuthorDto.getId());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(updatedAuthorDto.getName());
    }

    @Test
    @DisplayName("Delete author should delete author with given id")
    public void deleteAuthor_ShouldDeleteAuthorWithGivenId() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);

        // when
        deleteAuthor(author.getId());

        Response response = getAuthorById(author.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEmpty();
    }
}
