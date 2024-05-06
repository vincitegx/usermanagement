package com.davidogbodu.usermanagement.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationProperties {

    @Value("${jwt.expiry.minutes}")
    private Long jwtValidity;

    @Value("${refresh.token.cookie}")
    private String refreshTokenCookie;

    @Value("${organization.properties.mail}")
    private String mailAddress;

    @Value("${organization.properties.mail}")
    private String whatsappAccessToken;

    @Value("${organization.properties.mail}")
    private String whatsappPhoneNumber;

    @Value("${organization.properties.mail}")
    private String whatsappPhoneNumberId;

    @Value("${organization.properties.mail}")
    private String whatsappBusinessAccountId;

    @Value("${organization.properties.mail}")
    private String whatsappBaseUrl;
}
