package org.example.endpoints.withoutAuth.search;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.withoutAuth.base.BaseWithoutAuthMethod;

@Endpoint(url = "${config.base_url}/search/photos", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.UNAUTHORIZED_401)
public class SearchPhotosWithoutAuthMethod extends BaseWithoutAuthMethod {

    public SearchPhotosWithoutAuthMethod(String query) {
        super();
        addUrlParameter("query", query);
    }
}
