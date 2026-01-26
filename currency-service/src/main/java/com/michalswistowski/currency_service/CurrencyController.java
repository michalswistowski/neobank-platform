package com.michalswistowski.currency_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    @GetMapping("/currency")
    public String test() {
        return "Hello world";
    }
}
