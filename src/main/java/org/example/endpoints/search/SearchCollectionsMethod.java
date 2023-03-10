package org.example.endpoints.search;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.Paths;
import org.example.endpoints.base.BaseMethod;

import static org.example.utils.AppProperties.getToken;

@Endpoint(url = "${config.base_url}/${search}/${collections}", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class SearchCollectionsMethod extends BaseMethod {

    public SearchCollectionsMethod(boolean addToken, String query) {
        super();
        replaceUrlPlaceholder("search", Paths.SEARCH);
        replaceUrlPlaceholder("collections", Paths.COLLECTIONS);
        addUrlParameter("query", query);
        if (addToken) {
            setHeader("Authorization", getToken());
        }
    }

    public SearchCollectionsMethod(String query) {
        this(true, query);
    }
}
