package org.example.endpoints.withAuth.base;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;

import static org.example.utils.AppProperties.getToken;

public class BaseMethod extends AbstractApiMethodV2 {
    public BaseMethod() {
        setHeader("Authorization", getToken());
        setHeader("Version", "v1");
    }
}
