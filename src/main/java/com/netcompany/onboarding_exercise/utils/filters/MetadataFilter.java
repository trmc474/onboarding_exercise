package com.netcompany.onboarding_exercise.utils.filters;

import java.util.LinkedHashMap;
import java.util.Map;

public class MetadataFilter {
    public static Map<String, Object> filterMetadata(Map<String, Object> metadata) {
        Map<String, Object> filteredMetadata = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (entry.getValue() != null) {
                filteredMetadata.put(entry.getKey(), entry.getValue());
            }
        }

        return filteredMetadata;
    }
}
