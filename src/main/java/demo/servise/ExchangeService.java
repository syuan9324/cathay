package demo.servise;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.doman.ExchangeDTO;
import demo.repository.ExchangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExchangeService {

    @Autowired
    ExchangeRepository  exchangeRepository;

    public List<ExchangeDTO> getExchange() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String url = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";

//            信任所有主機
        final TrustStrategy strategy = (var1, var2) -> true;
        final SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, strategy)
                .build();

        final SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        final RestTemplate restTemplate = new RestTemplate(requestFactory);

        try {

            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            List<ExchangeDTO> data = parseEntity(forEntity.getBody());
            log.info("@@ ForEntity {}", forEntity.getBody());
            exchangeRepository.saveAll(data);
            return data;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("HTTP 錯誤：" + e.getStatusCode() + " - " + e.getStatusText());
        }
        return null;
    }

    public List<ExchangeDTO> parseEntity(String forEntity) {
        List<ExchangeDTO> DataList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> dataList = objectMapper.readValue(forEntity, new TypeReference<List<Map<String, String>>>() {
            });

            for (Map<String, String> data : dataList) {
                log.info("@@ {}", data);
                ExchangeDTO Data = new ExchangeDTO();
                LocalDate time = LocalDate.parse(data.get("Date"), DateTimeFormatter.ofPattern("yyyyMMdd"));
                Data.setDate(time);
                Data.setUsdNtd(Double.parseDouble(data.get("USD/NTD")));
                DataList.add(Data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DataList;
    }



    public List<ExchangeDTO> getForexData(Double currency, LocalDate startDate, LocalDate endDate) {
        List<ExchangeDTO>  exchangeDTOS =  exchangeRepository.findByUsdNtdAndDateBetween(currency, startDate, endDate);
        return exchangeDTOS;
    }




}
