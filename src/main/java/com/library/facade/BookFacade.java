package com.library.facade;

import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.service.BookService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookFacade {

    private final BookService bookService;
    private final ModelMapper modelMapper;

    public BookDto createBook(BookDto bookDto) {
        Book book = modelMapper.map(bookDto, Book.class);
        Book savedBook = bookService.createBook(book);
        return modelMapper.map(savedBook, BookDto.class);
    }

    public BookDto getBookById(Long id) {
        Book book = bookService.getBookById(id);
        return modelMapper.map(book, BookDto.class);
    }

    public List<BookDto> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return books.stream().map(book -> modelMapper.map(book, BookDto.class)).collect(Collectors.toList());
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = modelMapper.map(bookDto, Book.class);
        Book updatedBook = bookService.updateBook(id, book);
        return modelMapper.map(updatedBook, BookDto.class);
    }

    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }
}
