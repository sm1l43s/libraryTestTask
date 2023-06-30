package com.library;

import com.library.dto.AuthorDto;
import com.library.dto.BookDto;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
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
public class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    private Author createAuthor(String name) {
        return new Author(null, name, new ArrayList<>());
    }

    private Book createBook(String title, String isbn, Author author) {
        return new Book(null, title, isbn, author);
    }

    private BookDto createBookDto(String title, String isbn, AuthorDto authorDto) {
        return new BookDto(null, title, isbn, authorDto);
    }

    private Response postBook(BookDto bookDto) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bookDto)
                .when()
                .post("/api/v1/books");
    }

    private Response getBookById(Long id) {
        return given()
                .port(port)
                .when()
                .get("/api/v1/books/" + id);
    }

    private Response getAllBooks() {
        return given()
                .port(port)
                .when()
                .get("/api/v1/books");
    }

    private Response updateBook(BookDto bookDto) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(bookDto)
                .when()
                .put("/api/v1/books/" + bookDto.getId());
    }

    private Response deleteBook(Long id) {
        return given()
                .port(port)
                .when()
                .delete("/api/v1/books/" + id);
    }

    @Test
    @DisplayName("Create book should return created book")
    public void createBook_ShouldReturnCreatedBook() {
        // given
        Author author = createAuthor("John Doe");
        authorRepository.save(author);
        BookDto bookDto = createBookDto("Test Book", "1234567890", new AuthorDto(author.getId(), author.getName()));

        // when
        Response response = postBook(bookDto);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("id")).isNotNull();
        assertThat(response.body().jsonPath().getString("title")).isEqualTo(bookDto.getTitle());
        assertThat(response.body().jsonPath().getString("isbn")).isEqualTo(bookDto.getIsbn());
        assertThat(response.body().jsonPath().getObject("author", AuthorDto.class)).isEqualTo(bookDto.getAuthor());
    }

    @Test
    @DisplayName("Get book by id should return book with given id")
    public void getBookById_ShouldReturnBookWithGivenId() {
        // given
        Author author = createAuthor("John Doe");
        authorRepository.save(author);
        Book book = createBook("Test Book", "1234567890", author);
        bookRepository.save(book);

        // when
        Response response = getBookById(book.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(book.getId());
        assertThat(response.body().jsonPath().getString("title")).isEqualTo(book.getTitle());
        assertThat(response.body().jsonPath().getString("isbn")).isEqualTo(book.getIsbn());
        assertThat(response.body().jsonPath().getString("author.id")).isEqualTo(author.getId());
        assertThat(response.body().jsonPath().getString("author.name")).isEqualTo(author.getName());
    }

    @Test
    @DisplayName("Get all books should return all books")
    public void getAllBooks_ShouldReturnAllBooks() {
        // given
        Author author1 = createAuthor("John Doe");
        authorRepository.save(author1);
        Author author2 = createAuthor("Jane Doe");
        authorRepository.save(author2);
        Book book1 = createBook("Test Book 1", "1234567890", author1);
        bookRepository.save(book1);
        Book book2 = createBook("Test Book 2", "0987654321", author2);
        bookRepository.save(book2);

        // when
        Response response = getAllBooks();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList("")).hasSize(2);

    }

    @Test
    @DisplayName("Update book should return updated book")
    public void updateBook_ShouldReturnUpdatedBook() {
        // given
        Author author = createAuthor("John Doe");
        authorRepository.save(author);
        Book book = createBook("Test Book", "1234567890", author);
        bookRepository.save(book);
        BookDto updatedBookDto = createBookDto("Updated Book", "0987654321", new AuthorDto(author.getId(), author.getName()));
        updatedBookDto.setId(book.getId());

        // when
        updateBook(updatedBookDto);

        Response response = getBookById(updatedBookDto.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(updatedBookDto.getId());
        assertThat(response.body().jsonPath().getString("title")).isEqualTo(updatedBookDto.getTitle());
        assertThat(response.body().jsonPath().getString("isbn")).isEqualTo(updatedBookDto.getIsbn());
        assertThat(response.body().jsonPath().getObject("author", AuthorDto.class)).isEqualTo(updatedBookDto.getAuthor());
    }

    @Test
    @DisplayName("Delete book should delete book with given id")
    public void deleteBook_ShouldDeleteBookWithGivenId() {
        // given
        Author author = createAuthor("John Doe");
        authorRepository.save(author);
        Book book = createBook("Test Book", "1234567890", author);
        bookRepository.save(book);

        // when
        deleteBook(book.getId());

        Response response = getBookById(book.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEmpty();
    }
}