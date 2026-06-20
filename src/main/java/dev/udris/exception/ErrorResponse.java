package dev.udris.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String title, String detail, LocalDateTime timestamp){

}
