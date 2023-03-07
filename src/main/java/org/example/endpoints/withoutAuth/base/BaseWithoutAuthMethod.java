package org.example.endpoints.withoutAuth.base;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;

public class BaseWithoutAuthMethod extends AbstractApiMethodV2 {
    public BaseWithoutAuthMethod() {
        setHeader("Version", "v1");
    }
}
