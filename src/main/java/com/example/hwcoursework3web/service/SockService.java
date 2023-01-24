package com.example.hwcoursework3web.service;

import com.example.hwcoursework3web.dto.SockDt0;
import com.example.hwcoursework3web.exception.InSufficientSockQuantityException;
import com.example.hwcoursework3web.exception.InvalidSockException;
import com.example.hwcoursework3web.model.Color;
import com.example.hwcoursework3web.model.Size;
import com.example.hwcoursework3web.model.Sock;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SockService {
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final Map<Sock, Integer> socks = new HashMap<>();

    public SockService(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    public void importData(InputStream inputStream) throws IOException {
        List<SockDt0> importList = objectMapper.readValue(inputStream, new TypeReference<List<SockDt0>>() {
        });
        this.socks.clear();
        for (SockDt0 dt0 : importList) {
            addSock(dt0);
        }
    }

    public FileSystemResource exportData() throws IOException {
        Path filePath = Files.createTempFile("export-", "json");
        List<SockDt0> sockDt0List = new ArrayList<>();
        for (Map.Entry<Sock, Integer> entry : this.socks.entrySet()) {
            sockDt0List.add(mapToDto(entry.getKey(), entry.getValue()));
        }
        Files.write(
                filePath,
                objectMapper.writeValueAsBytes(sockDt0List));
        return new FileSystemResource(filePath);
    }

    private SockDt0 mapToDto(Sock sock, int quantity) {
        SockDt0 sockDt0 = new SockDt0();
        sockDt0.setColor(sock.getColor());
        sockDt0.setSize(sock.getSize());
        sockDt0.setCottonPercentage(sock.getCottonPercentage());
        sockDt0.setQuantity(quantity);
        return sockDt0;

    }

    public void addSock(SockDt0 sockDt0) {
        validateRequest(sockDt0);
        Sock sock = mapTosSock(sockDt0);
        if (socks.containsKey(sock)) {
            socks.put(sock, socks.get(sock) + sockDt0.getQuantity());
        } else {
            socks.put(sock, sockDt0.getQuantity());
        }
        auditService.recordAddOperation(sock,sockDt0.getQuantity());
    }

    public int getSockQuantity(Color color, Size size, Integer cottonMin, Integer cottonMax) {
        int total = 0;
        for (Map.Entry<Sock, Integer> entry : socks.entrySet()) {
            if (color != null && !entry.getKey().getColor().equals(color)) {
                continue;
            }
            if (size != null && !entry.getKey().getSize().equals(size)) {
                continue;
            }
            if (cottonMin != null && entry.getKey().getCottonPercentage() < cottonMin) {
                continue;
            }
            if (cottonMax != null && entry.getKey().getCottonPercentage() > cottonMax) {
                continue;
            }
            total += entry.getValue();
        }
        return total;
    }

    private void validateRequest(SockDt0 sockDt0) {
        if (sockDt0.getColor() == null || sockDt0.getSize() == null) {
            throw new InvalidSockException("Все поля должны заполнены");
        }
        if (sockDt0.getCottonPercentage() < 0 && sockDt0.getCottonPercentage() > 100) {
            throw new InvalidSockException("Процент хлопка может быть между 0 и 100");
        }
        if (sockDt0.getQuantity() <= 0) {
            throw new InvalidSockException("Введите число больше 0");
        }
    }

    private Sock mapTosSock(SockDt0 sockDt0) {
        return new Sock(sockDt0.getColor(), sockDt0.getSize(), sockDt0.getCottonPercentage());
    }

    public void issueSock(SockDt0 sockDt0) {
        decreaseSockQuantity(sockDt0,true);
    }

    public void removeDefectiveSocks(SockDt0 sockDt0) {
        decreaseSockQuantity(sockDt0,false);
    }

    private void decreaseSockQuantity(SockDt0 sockDt0,boolean isIssue) {
        validateRequest(sockDt0);
        Sock sock = mapTosSock(sockDt0);
        int sockQuantity = socks.getOrDefault(sock, 0);
        if (sockQuantity >= sockDt0.getQuantity()) {
            socks.put(sock, sockQuantity - sockDt0.getQuantity());
        } else {
            throw new InSufficientSockQuantityException("Носков больше нет");
        }
        if (isIssue) {
            auditService.recordIssueOperation(sock, sockDt0.getQuantity());
        } else {
            auditService.recordRemoveDefectedOperation(sock,sockDt0.getQuantity());
        }
    }

}
