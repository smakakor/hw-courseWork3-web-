package com.example.hwcoursework3web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class Operation {
    private final com.example.hwcoursework3web.model.Type type;
    private final LocalDateTime localDateTime;
    private final int quantity;
    private final Sock sock;



}
