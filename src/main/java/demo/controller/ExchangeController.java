package demo.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.doman.ExchangeDTO;
import demo.servise.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExchangeController {

    @Autowired
    ExchangeService exchangeService;


    @GetMapping("/getExchange")
    public List<ExchangeDTO> getExchange() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return exchangeService.getExchange();
    }


    @GetMapping("/forex")
    public ResponseEntity<?> getForexData(@RequestParam("currency") Double currency,
                                          @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                          @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now().minusYears(1)) || endDate.isAfter(LocalDate.now().minusDays(1))) {
            return ResponseEntity.badRequest().body("日期區間僅限 1 年前~當下日期-1 天");
        }

        List<ExchangeDTO> forexDataList = exchangeService.getForexData(currency, startDate, endDate);
        return ResponseEntity.ok(forexDataList);
    }
}
