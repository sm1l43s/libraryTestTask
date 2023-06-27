package com.library.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppException {
    private int statusCode;
    private String message;
}
