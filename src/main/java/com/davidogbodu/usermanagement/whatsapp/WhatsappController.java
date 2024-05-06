package com.davidogbodu.usermanagement.whatsapp;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {
    private final WhatsappService whatsappService;

    @PostMapping("message")
    public MessageResponse sendWhatsappMessage(@RequestBody @Validated MessageRequest request){
        return whatsappService.sendWhatsappMessage(request);
    }

}
