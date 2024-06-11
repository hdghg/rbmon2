package com.github.hdghg.rbmon2.controller;

import com.github.hdghg.rbmon2.service.JdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenericController {

    @Autowired
    private JdaService jdaService;

    @GetMapping("/version-message")
    public void testMessage() {
        String disMsg = """
                Версия 0.3.2:
                * Улучшена обработка сетевых ошибок""";
        jdaService.sendMessage(disMsg);
    }
}
