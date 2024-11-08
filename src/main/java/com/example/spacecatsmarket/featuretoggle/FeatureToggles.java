package com.example.spacecatsmarket.featuretoggle;

import lombok.Getter;

@Getter
public enum FeatureToggles {

    CUSTOMER_GREETING("customer-greeting"),
    SOME_OTHER_FEATURE("feature");

    private final String featureName;

    FeatureToggles(String featureName) {
        this.featureName = featureName;
    }

}
