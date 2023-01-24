package com.example.hwcoursework3web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sock {
    private final Color color;
    private final Size size;
    private final int cottonPercentage;


}
