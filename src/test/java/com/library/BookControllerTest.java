package com.library;

import com.library.dto.AuthorDto;
import com.library.dto.BookDto;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
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
public class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Before
    public void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void createBook_ShouldReturnCreatedBook() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);
        BookDto bookDto = new BookDto(null, "Test Book", "1234567890", new AuthorDto(author.getId(), author.getName()));

        // when
        ResponseEntity<BookDto> response = restTemplate.postForEntity("/api/v1/books", bookDto, BookDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(bookDto.getTitle());
        assertThat(response.getBody().getIsbn()).isEqualTo(bookDto.getIsbn());
        assertThat(response.getBody().getAuthor()).isEqualTo(bookDto.getAuthor());
    }

    @Test
    public void getBookById_ShouldReturnBookWithGivenId() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);
        Book book = new Book(null, "Test Book", "1234567890", author);
        bookRepository.save(book);

        // when
        ResponseEntity<BookDto> response = restTemplate.getForEntity("/api/v1/books/" + book.getId(), BookDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(book.getId());
        assertThat(response.getBody().getTitle()).isEqualTo(book.getTitle());
        assertThat(response.getBody().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(response.getBody().getAuthor().getId()).isEqualTo(author.getId());
        assertThat(response.getBody().getAuthor().getName()).isEqualTo(author.getName());
    }

    @Test
    public void getAllBooks_ShouldReturnAllBooks() {
        // given
        Author author1 = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author1);
        Author author2 = new Author(null, "Jane Doe", new ArrayList<>());
        authorRepository.save(author2);
        Book book1 = new Book(null, "Test Book 1", "1234567890", author1);
        bookRepository.save(book1);
        Book book2 = new Book(null, "Test Book 2", "0987654321", author2);
        bookRepository.save(book2);

        // when
        ResponseEntity<BookDto[]> response = restTemplate.getForEntity("/api/v1/books", BookDto[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

    }

    @Test
    public void updateBook_ShouldReturnUpdatedBook() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);
        Book book = new Book(null, "Test Book", "1234567890", author);
        bookRepository.save(book);
        BookDto updatedBookDto = new BookDto(book.getId(), "Updated Book", "0987654321", new AuthorDto(author.getId(), author.getName()));

        // when
        restTemplate.put("/api/v1/books/" + book.getId(), updatedBookDto);
        ResponseEntity<BookDto> response = restTemplate.getForEntity("/api/v1/books/" + book.getId(), BookDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(updatedBookDto.getId());
        assertThat(response.getBody().getTitle()).isEqualTo(updatedBookDto.getTitle());
        assertThat(response.getBody().getIsbn()).isEqualTo(updatedBookDto.getIsbn());
        assertThat(response.getBody().getAuthor()).isEqualTo(updatedBookDto.getAuthor());
    }

    @Test
    public void deleteBook_ShouldDeleteBookWithGivenId() {
        // given
        Author author = new Author(null, "John Doe", new ArrayList<>());
        authorRepository.save(author);
        Book book = new Book(null, "Test Book", "1234567890", author);
        bookRepository.save(book);

        // when
        restTemplate.delete("/api/v1/books/" + book.getId());
        ResponseEntity<BookDto> response = restTemplate.getForEntity("/api/v1/books/" + book.getId(), BookDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}