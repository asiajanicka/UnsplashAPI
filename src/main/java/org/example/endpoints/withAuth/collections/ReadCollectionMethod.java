package org.example.endpoints.withAuth.collections;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.withAuth.base.BaseMethod;

@Endpoint(url = "${config.base_url}/collections/${id}", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class ReadCollectionMethod extends BaseMethod {

    public ReadCollectionMethod() {
        super();
    }

    public ReadCollectionMethod(String id) {
        this();
        replaceUrlPlaceholder("id", id);
    }
}
