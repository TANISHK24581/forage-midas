package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveClient {

    private final RestTemplate restTemplate;
    private final String incentiveUrl;

    public IncentiveClient(
            RestTemplateBuilder builder,
            @Value("${general.incentive-url}") String incentiveUrl
    ) {
        this.restTemplate = builder.build();
        this.incentiveUrl = incentiveUrl;
    }

    public Incentive fetchIncentive(Transaction transaction) {
        return restTemplate.postForObject(
                incentiveUrl,
                transaction,
                Incentive.class
        );
    }
}
