package org.example.endpoints.collections;

import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.base.BaseMethod;

@Endpoint(url = "${config.base_url}/collections/${id}/photos", methodType = HttpMethodType.GET)
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class ReadPhotosInCollectionMethod extends BaseMethod {

    public ReadPhotosInCollectionMethod() {
        super();
    }

    public ReadPhotosInCollectionMethod(String id) {
        super();
        replaceUrlPlaceholder("id", id);
    }
}
