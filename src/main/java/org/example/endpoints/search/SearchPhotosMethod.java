package org.example.endpoints.search;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.Paths;
import org.example.endpoints.base.BaseMethod;

import static org.example.utils.AppProperties.getToken;

@Endpoint(url = "${config.base_url}/${search}/${photos}", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class SearchPhotosMethod extends BaseMethod {

    public SearchPhotosMethod() {
        super();
        setHeader("Authorization", getToken());
        replaceUrlPlaceholder("search", Paths.SEARCH);
        replaceUrlPlaceholder("photos", Paths.PHOTOS);
    }

    public SearchPhotosMethod(boolean addToken, String query) {
        super();
        replaceUrlPlaceholder("search", Paths.SEARCH);
        replaceUrlPlaceholder("photos", Paths.PHOTOS);
        addUrlParameter("query", query);
        if (addToken) {
            setHeader("Authorization", getToken());
        }
    }

    public SearchPhotosMethod(String query) {
        this(true, query);
    }
}
