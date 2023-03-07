package org.example.endpoints.withAuth.search;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.withAuth.base.BaseMethod;

@Endpoint(url = "${config.base_url}/search/photos", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class SearchPhotosMethod extends BaseMethod {

    public SearchPhotosMethod() {
        super();
    }

    public SearchPhotosMethod(String query) {
        this();
        addUrlParameter("query", query);
    }
}
