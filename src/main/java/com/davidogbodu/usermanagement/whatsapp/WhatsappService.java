package com.davidogbodu.usermanagement.whatsapp;

import com.davidogbodu.usermanagement.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class WhatsappService {
    private final WebClient.Builder webClientBuilder;
    private final ApplicationProperties properties;
    private final static String BEARER = "Bearer ";
    public MessageResponse sendWhatsappMessage(MessageRequest messageRequest){
        return webClientBuilder.build()
                .post()
                .uri(properties.getWhatsappBaseUrl()+properties.getWhatsappPhoneNumberId()+"/messages")
                .header(HttpHeaders.AUTHORIZATION, BEARER+properties.getWhatsappAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(messageRequest)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
