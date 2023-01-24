package com.example.hwcoursework3web.Controller;

import com.example.hwcoursework3web.dto.SockDt0;
import com.example.hwcoursework3web.model.Color;
import com.example.hwcoursework3web.model.Size;
import com.example.hwcoursework3web.service.SockService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/sock")
public class SockController {
    private final SockService sockService;

    public SockController(SockService service) {
        this.sockService = service;
    }

    @PostMapping
    public void addSocks(@RequestBody SockDt0 sockDt0) {
        sockService.addSock(sockDt0);
    }

    @PutMapping
    private void issueSock(@RequestBody SockDt0 sockDt0) {
        sockService.issueSock(sockDt0);
    }

    @GetMapping
    public int getSocksCount(@RequestParam(required = false, name = "color") Color color,
                             @RequestParam(required = false, name = "size") Size size,
                             @RequestParam(required = false, name = "cottonMin") Integer cottonMin,
                             @RequestParam(required = false, name = "cottonMax") Integer cottonMax) {
        return sockService.getSockQuantity(color, size, cottonMin, cottonMax);
    }

    @DeleteMapping
    public void removeDefectiveSocks(@RequestParam SockDt0 sockDt0) {
        sockService.removeDefectiveSocks(sockDt0);
    }

    @GetMapping("/export")
    public FileSystemResource exportData() throws IOException {
        return sockService.exportData();
    }
}
