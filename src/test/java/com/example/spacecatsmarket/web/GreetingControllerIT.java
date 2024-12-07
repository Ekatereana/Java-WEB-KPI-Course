package com.example.spacecatsmarket.web;

import com.example.spacecatsmarket.AbstractIt;
import com.example.spacecatsmarket.featuretoggle.FeatureToggleExtension;
import com.example.spacecatsmarket.featuretoggle.FeatureToggles;
import com.example.spacecatsmarket.featuretoggle.annotation.DisabledFeatureToggle;
import com.example.spacecatsmarket.featuretoggle.annotation.EnabledFeatureToggle;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("Greeting Controller IT")
@ExtendWith(FeatureToggleExtension.class)
class GreetingControllerIT extends AbstractIt {

    private static final String DOBBY = "dobby";
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    @WithMockUser
    @DisabledFeatureToggle(FeatureToggles.CUSTOMER_GREETING)
    void shouldGet404FeatureDisabled() {
        mockMvc.perform(get("/api/v1/greetings/{name}", DOBBY)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @EnabledFeatureToggle(FeatureToggles.CUSTOMER_GREETING)
    void shouldGet200() {
        mockMvc.perform(get("/api/v1/greetings/{name}", DOBBY)).andExpect(status().isOk());
    }
}
