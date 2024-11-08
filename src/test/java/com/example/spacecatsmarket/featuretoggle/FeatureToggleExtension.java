package com.example.spacecatsmarket.featuretoggle;

import com.example.spacecatsmarket.featuretoggle.annotation.DisabledFeatureToggle;
import com.example.spacecatsmarket.featuretoggle.annotation.EnabledFeatureToggle;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class FeatureToggleExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {

        context.getTestMethod().ifPresent(method -> {

            FeatureToggleService featureToggleService = getFeatureToggleService(context);

            if (method.isAnnotationPresent(EnabledFeatureToggle.class)) {
                EnabledFeatureToggle enabledFeatureToggleAnnotation = method.getAnnotation(EnabledFeatureToggle.class);
                featureToggleService.enable(enabledFeatureToggleAnnotation.value().getFeatureName());
            } else if (method.isAnnotationPresent(DisabledFeatureToggle.class)) {
                DisabledFeatureToggle disabledFeatureToggleAnnotation = method.getAnnotation(DisabledFeatureToggle.class);
                featureToggleService.disable(disabledFeatureToggleAnnotation.value().getFeatureName());
            }
        });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        context.getTestMethod().ifPresent(method -> {
            String featureName = null;

            if (method.isAnnotationPresent(EnabledFeatureToggle.class)) {
                EnabledFeatureToggle enabledFeatureToggleAnnotation = method.getAnnotation(EnabledFeatureToggle.class);
                featureName = enabledFeatureToggleAnnotation.value().getFeatureName();
            } else if (method.isAnnotationPresent(DisabledFeatureToggle.class)) {
                DisabledFeatureToggle disabledFeatureToggleAnnotation = method.getAnnotation(DisabledFeatureToggle.class);
                featureName = disabledFeatureToggleAnnotation.value().getFeatureName();
            }
            if (featureName != null) {
                FeatureToggleService featureToggleService = getFeatureToggleService(context);
                if (getFeatureNamePropertyAsBoolean(context, featureName)) {
                    featureToggleService.enable(featureName);
                } else {
                    featureToggleService.disable(featureName);
                }
            }
        });
    }

    private boolean getFeatureNamePropertyAsBoolean(ExtensionContext context, String featureName) {
        Environment environment = SpringExtension.getApplicationContext(context).getEnvironment();
        return environment.getProperty("application.feature.toggles." + featureName, Boolean.class, Boolean.FALSE);
    }

    private FeatureToggleService getFeatureToggleService(ExtensionContext context) {
        return SpringExtension.getApplicationContext(context).getBean(FeatureToggleService.class);
    }

}
