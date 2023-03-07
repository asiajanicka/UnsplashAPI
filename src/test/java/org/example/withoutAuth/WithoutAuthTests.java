package org.example.withoutAuth;

import org.example.endpoints.withoutAuth.collections.ReadCollectionWithoutAuthMethod;
import org.example.endpoints.withoutAuth.collections.ReadPhotosInCollectionWithoutAuthMethod;
import org.example.endpoints.withoutAuth.search.SearchCollectionsWithoutAuthMethod;
import org.example.endpoints.withoutAuth.search.SearchPhotosWithoutAuthMethod;
import org.example.model.ErrorDto;
import org.example.utils.CommonNames;
import org.example.withAuth.dataProviders.DataProviders;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WithoutAuthTests {

    private final String errorMsg = "OAuth error: The access token is invalid";

    @Test
    public void dontSearchPhotosWithoutToken() {
        SearchPhotosWithoutAuthMethod request = new SearchPhotosWithoutAuthMethod(CommonNames.baseSearchQuery);

        ErrorDto errorDto = request.callAPIExpectSuccess().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }

    @Test
    public void dontSearchCollectionWithoutToken() {
        SearchCollectionsWithoutAuthMethod request = new SearchCollectionsWithoutAuthMethod(CommonNames.baseSearchQuery);

        ErrorDto errorDto = request.callAPIExpectSuccess().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void dontReadCollectionWithoutToken(List<String> collectionsIds) {
        ReadCollectionWithoutAuthMethod request = new ReadCollectionWithoutAuthMethod(collectionsIds.get(0));

        ErrorDto errorDto = request.callAPIExpectSuccess().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void dontReadPhotosInCollectionWithoutToken(List<String> collectionsIds) {
        ReadPhotosInCollectionWithoutAuthMethod request = new ReadPhotosInCollectionWithoutAuthMethod(collectionsIds.get(0));

        ErrorDto errorDto = request.callAPIExpectSuccess().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(errorMsg);
    }
}
