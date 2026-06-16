package com.mtganalytics.lab.config;

import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "opensearch")
public class OpensearchConfig {

    private String host;
    private int port;
    private String scheme;

    @Bean
    public OpenSearchClient openSearchClient() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        HttpHost httpHost = new HttpHost(scheme, host, port);

        OpenSearchTransport transport = ApacheHttpClient5TransportBuilder
                .builder(httpHost)
                .setMapper(new JacksonJsonpMapper(mapper))
                .build();

        return new OpenSearchClient(transport);
    }
}