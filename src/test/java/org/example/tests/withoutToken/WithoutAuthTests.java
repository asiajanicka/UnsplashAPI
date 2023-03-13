package org.example.tests.withoutToken;

import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import org.example.endpoints.collections.ReadCollectionMethod;
import org.example.endpoints.collections.ReadPhotosInCollectionMethod;
import org.example.endpoints.search.SearchCollectionsMethod;
import org.example.endpoints.search.SearchPhotosMethod;
import org.example.model.ErrorDto;
import org.example.tests.dataProviders.DataProviders;
import org.example.utils.CommonNames;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WithoutAuthTests implements IAbstractTest {
    private final String errorMsg = "OAuth error: The access token is invalid";

    @Test
    public void dontSearchPhotosWithoutToken() {
        SearchPhotosMethod request = new SearchPhotosMethod(false, CommonNames.baseSearchQuery);
        request.expectResponseStatus(HttpResponseStatusType.UNAUTHORIZED_401);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }

    @Test
    public void dontSearchCollectionWithoutToken() {
        SearchCollectionsMethod request = new SearchCollectionsMethod(false, CommonNames.baseSearchQuery);
        request.expectResponseStatus(HttpResponseStatusType.UNAUTHORIZED_401);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void dontReadCollectionWithoutToken(List<String> collectionsIds) {
        ReadCollectionMethod request = new ReadCollectionMethod(false, collectionsIds.get(0));
        request.expectResponseStatus(HttpResponseStatusType.UNAUTHORIZED_401);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void dontReadPhotosInCollectionWithoutToken(List<String> collectionsIds) {
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(false, collectionsIds.get(0));
        request.expectResponseStatus(HttpResponseStatusType.UNAUTHORIZED_401);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }
}
