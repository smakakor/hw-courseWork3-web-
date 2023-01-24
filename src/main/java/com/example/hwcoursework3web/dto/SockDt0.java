package com.example.hwcoursework3web.dto;

import com.example.hwcoursework3web.model.Color;
import com.example.hwcoursework3web.model.Size;
import lombok.Data;

@Data
public class SockDt0 {
    public Color color;
    private Size size;
    private int cottonPercentage;
    private int quantity;

}
