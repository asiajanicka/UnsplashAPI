package org.example.endpoints.withoutAuth.collections;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.withoutAuth.base.BaseWithoutAuthMethod;

@Endpoint(url = "${config.base_url}/collections/${id}", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.UNAUTHORIZED_401)
public class ReadCollectionWithoutAuthMethod extends BaseWithoutAuthMethod {

    public ReadCollectionWithoutAuthMethod(String id) {
        super();
        replaceUrlPlaceholder("id", id);
    }
}
