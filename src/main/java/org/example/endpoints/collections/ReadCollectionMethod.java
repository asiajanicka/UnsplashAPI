package org.example.endpoints.collections;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.Paths;
import org.example.endpoints.base.BaseMethod;

import static org.example.utils.AppProperties.getToken;

@Endpoint(url = "${config.base_url}/${collections}/${id}", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class ReadCollectionMethod extends BaseMethod {

    public ReadCollectionMethod(boolean addToken, String id) {
        super();
        replaceUrlPlaceholder("collections", Paths.COLLECTIONS);
        replaceUrlPlaceholder("id", id);
        if (addToken) {
            setHeader("Authorization", getToken());
        }
    }

    public ReadCollectionMethod(String id) {
        this(true, id);
    }
}
