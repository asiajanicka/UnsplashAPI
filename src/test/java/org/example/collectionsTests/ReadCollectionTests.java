package org.example.collectionsTests;

import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.dataProviders.DataProviders;
import org.example.endpoints.collections.ReadCollectionMethod;
import org.example.model.CollectionDto;
import org.example.model.ErrorDto;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadCollectionTests implements IAbstractTest {

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void validCollectionIdTest(List<String> collectionIds) {
        String expectedId = collectionIds.get(0);
        ReadCollectionMethod request = new ReadCollectionMethod(expectedId);

        CollectionDto collectionDto = request.callAPIExpectSuccess().as(CollectionDto.class);

        assertThat(collectionDto.getId()).isEqualTo(expectedId);
        assertThat(collectionDto.getTitle()).isNotNull();
        assertThat(collectionDto.getPublishedAt()).isNotNull();
    }

    @Test
    public void spaceAsCollectionIdTest() {
        ReadCollectionMethod request = new ReadCollectionMethod(" ");

        List<CollectionDto> collections = request.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        assertThat(collections).isNotEmpty();
    }

    @Test(dataProvider = "invalid-collection-ids", dataProviderClass = DataProviders.class)
    public void invalidCollectionIdTest(String invalidCollectionId) {
        List<String> expectedErrorMessages = List.of("Not found", "Couldn't find Collection");

        ReadCollectionMethod request = new ReadCollectionMethod(invalidCollectionId);
        request.expectResponseStatus(HttpResponseStatusType.NOT_FOUND_404);

        ErrorDto errors = request.callAPI().as(ErrorDto.class);

        assertThat(errors.getMessages()).containsAnyElementsOf(expectedErrorMessages);
    }

    @Test
    public void tooLongCollectionIdTest() {
        String expectedErrorMessage = "URI Too Long";

        ReadCollectionMethod request = new ReadCollectionMethod(RandomStringUtils.randomNumeric(40000));

        Response response = request.callAPI();
        assertThat(response.getStatusCode()).isEqualTo(414);

        ErrorDto errors = request.callAPI().as(ErrorDto.class);
        assertThat(errors.getMessages()).contains(expectedErrorMessage);
    }

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void validateIfCollectionWithDifferentIdsReturnDifferentIds(List<String> collectionsIds) {
        ReadCollectionMethod requestForFirstCollection = new ReadCollectionMethod(collectionsIds.get(0));

        CollectionDto collectionDtoForFirstCollection = requestForFirstCollection.callAPI().as(CollectionDto.class);

        ReadCollectionMethod requestForSecondCollection = new ReadCollectionMethod(collectionsIds.get(1));

        CollectionDto collectionDtoForSecondCollection = requestForSecondCollection.callAPI().as(CollectionDto.class);

        assertThat(collectionDtoForFirstCollection.getId()).isNotEqualTo(collectionDtoForSecondCollection.getId());
    }
}
