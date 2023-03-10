package org.example.endpoints.base;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;

public class BaseMethod extends AbstractApiMethodV2 {

    public BaseMethod() {
        setHeader("Version", "v1");
    }
}
