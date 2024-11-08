package com.example.spacecatsmarket.web;

import com.example.spacecatsmarket.config.GreetingProperties;
import com.example.spacecatsmarket.config.GreetingProperties.CatGreeting;
import com.example.spacecatsmarket.featuretoggle.FeatureToggles;
import com.example.spacecatsmarket.featuretoggle.annotation.FeatureToggle;
import com.example.spacecatsmarket.web.exception.CatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingController {

    private final GreetingProperties greetingProperties;

    public GreetingController(GreetingProperties greetingProperties) {
        this.greetingProperties = greetingProperties;
    }

    @GetMapping("/{name}")
    @FeatureToggle(FeatureToggles.CUSTOMER_GREETING)
    public ResponseEntity<String> getCustomerById(@PathVariable String name) {
        String greeting = ofNullable(greetingProperties.getGreetings()
            .get(name)).map(CatGreeting::getMessage).orElseThrow(() -> new CatNotFoundException(name));
        return ResponseEntity.ok(greeting);
    }
}
