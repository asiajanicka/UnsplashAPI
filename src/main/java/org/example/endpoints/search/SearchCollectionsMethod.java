package org.example.endpoints.search;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.base.BaseMethod;

@Endpoint(url = "${config.base_url}/search/collections", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class SearchCollectionsMethod extends BaseMethod {

    public SearchCollectionsMethod() {
        super();
    }

    public SearchCollectionsMethod(String query) {
        addUrlParameter("query", query);
    }
}
