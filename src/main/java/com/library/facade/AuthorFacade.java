package com.library.facade;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.service.AuthorService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AuthorFacade {

    private final AuthorService authorService;
    private final ModelMapper modelMapper;

    public AuthorDto createAuthor(AuthorDto authorDto) {
        Author author = modelMapper.map(authorDto, Author.class);
        Author savedAuthor = authorService.createAuthor(author);
        return modelMapper.map(savedAuthor, AuthorDto.class);
    }

    public AuthorDto getAuthorById(Long id) {
        Author author = authorService.getAuthorById(id);
        return modelMapper.map(author, AuthorDto.class);
    }

    public List<AuthorDto> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return authors.stream().map(author -> modelMapper.map(author, AuthorDto.class)).collect(Collectors.toList());
    }

    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {
        Author author = modelMapper.map(authorDto, Author.class);
        Author updatedAuthor = authorService.updateAuthor(id, author);
        return modelMapper.map(updatedAuthor, AuthorDto.class);
    }

    public void deleteAuthor(Long id) {
        authorService.deleteAuthor(id);
    }
}
