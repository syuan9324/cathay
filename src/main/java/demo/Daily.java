package demo;

import demo.controller.ExchangeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@EnableScheduling
@Configuration
public class Daily {

    @Autowired
    private ExchangeController exchangeController;

    private static final Logger log = LoggerFactory.getLogger(Daily.class);

    @Scheduled(cron = "0 0 18 * * ?")
    public void saveDailyTranctionStockData() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        exchangeController.getExchange();
    }
}
