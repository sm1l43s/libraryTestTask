package com.library.dto;

import lombok.Data;

@Data
public class BookDto {

    private Long id;
    private String title;
    private String isbn;
    private AuthorDto author;
}
