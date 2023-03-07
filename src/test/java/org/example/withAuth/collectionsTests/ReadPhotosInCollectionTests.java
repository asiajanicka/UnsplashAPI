package org.example.withAuth.collectionsTests;

import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.example.withAuth.dataProviders.DataProviders;
import org.example.endpoints.withAuth.collections.ReadPhotosInCollectionMethod;
import org.example.endpoints.withAuth.search.SearchCollectionsMethod;
import org.example.model.ErrorDto;
import org.example.model.PhotoDto;
import org.example.model.SearchCollectionDto;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.utils.CommonNames.baseSearchQuery;
import static org.example.utils.TestUtils.*;
import static org.example.utils.AppProperties.getDefaultResultsPerPageNum;
import static org.example.utils.AppProperties.getLimitForResultsPerPageNum;

public class ReadPhotosInCollectionTests implements IAbstractTest {
    private static String validCollectionId;

    @BeforeClass
    public void beforeClass() {
        SearchCollectionsMethod request = new SearchCollectionsMethod(baseSearchQuery);
        validCollectionId = request.callAPI()
                .as(SearchCollectionDto.class)
                .getCollections()
                .get(0)
                .getId();
    }

    @Test
    public void collectionWithValidIdTest() {
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response);
    }

    @Test(dataProvider = "empty-values", dataProviderClass = DataProviders.class)
    public void collectionWithEmptyValueAsIdTest(String emptyValue) {
        List<String> expectedErrorMessages = List.of("Not found", "Couldn't find Collection");

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(emptyValue);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).containsAnyElementsOf(expectedErrorMessages);
    }

    @Test(dataProvider = "invalid-collection-ids", dataProviderClass = DataProviders.class)
    public void collectionWithInvalidIdTest(String invalidCollectionId) {
        List<String> expectedErrorMessages = List.of("Not found", "Couldn't find Collection");

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(invalidCollectionId);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).containsAnyElementsOf(expectedErrorMessages);
    }

    private void assertDefaultResponse(Response response) {
        String linkHeader = response.getHeader("Link");
        int total = Integer.parseInt(response.getHeader("X-Total"));

        int expectedTotalNumberOfPages = (int) Math.ceil((double) total / getDefaultResultsPerPageNum());
        String expectedLastPageString = String.format("%s/photos?page=%d", validCollectionId, expectedTotalNumberOfPages);
        String expectedNextPageString = String.format("%s/photos?page=%d", validCollectionId, 2);

        List<PhotoDto> photoDtos = response.as(new TypeRef<>() {
        });

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(total).isGreaterThan(getDefaultResultsPerPageNum());
        soft.assertThat(photoDtos.size()).isEqualTo(getDefaultResultsPerPageNum());
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(isFirstPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isPrevPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test
    public void getGivenPageFromCollection() {
        int page = 4;

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("page", String.valueOf(page));

        Response response = request.callAPIExpectSuccess();

        List<PhotoDto> photoDtos = response.as(new TypeRef<>() {
        });
        String linkHeader = response.getHeader("Link");
        int total = Integer.parseInt(response.getHeader("X-Total"));

        int expectedTotalNumberOfPages = (int) Math.ceil((double) total / getDefaultResultsPerPageNum());
        String expectedFirstPageString = String.format("%s/photos?page=%d", validCollectionId, 1);
        String expectedPrevPageString = String.format("%s/photos?page=%d", validCollectionId, page - 1);
        String expectedLastPageString = String.format("%s/photos?page=%d", validCollectionId, expectedTotalNumberOfPages);
        String expectedNextPageString = String.format("%s/photos?page=%d", validCollectionId, page + 1);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(photoDtos.size()).isEqualTo(getDefaultResultsPerPageNum());
        soft.assertThat(getFirstPageLinkFromHeader(linkHeader)).contains(expectedFirstPageString);
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(getPrevPageLinkFromHeader(linkHeader)).contains(expectedPrevPageString);
        soft.assertAll();
    }

    @Test
    public void getLastPageFromCollectionTest() {
        ReadPhotosInCollectionMethod preRequest = new ReadPhotosInCollectionMethod(validCollectionId);
        int total = Integer.parseInt(preRequest.callAPIExpectSuccess().getHeader("X-Total"));
        int lastPage = (int) Math.ceil((double) total / getDefaultResultsPerPageNum());

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("page", String.valueOf(lastPage));

        Response response = request.callAPIExpectSuccess();

        List<PhotoDto> photoDtos = response.as(new TypeRef<>() {
        });
        String linkHeader = response.getHeader("Link");

        String expectedFirstPageString = String.format("%s/photos?page=%d", validCollectionId, 1);
        String expectedPrevPageString = String.format("%s/photos?page=%d", validCollectionId, lastPage - 1);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(photoDtos.size()).isGreaterThan(1);
        soft.assertThat(getFirstPageLinkFromHeader(linkHeader)).contains(expectedFirstPageString);
        soft.assertThat(getPrevPageLinkFromHeader(linkHeader)).contains(expectedPrevPageString);
        soft.assertThat(isNextPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isLastPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test
    public void validateIfDifferentPagesForTheSameCollectionReturnDifferentResultsTest() {
        ReadPhotosInCollectionMethod requestForSecondPage = new ReadPhotosInCollectionMethod(validCollectionId);
        requestForSecondPage.addUrlParameter("page", "2");
        List<PhotoDto> photoDtosSecondPage = requestForSecondPage.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        ReadPhotosInCollectionMethod requestForThirdPage = new ReadPhotosInCollectionMethod(validCollectionId);
        requestForThirdPage.addUrlParameter("page", "3");
        List<PhotoDto> photoDtosThirdPage = requestForThirdPage.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        assertThat(photoDtosSecondPage).doesNotContainAnyElementsOf(photoDtosThirdPage);
    }

    @Test
    public void getPageOverTotalNumberOfPagesTest() {
        ReadPhotosInCollectionMethod preRequest = new ReadPhotosInCollectionMethod(validCollectionId);
        int total = Integer.parseInt(preRequest.callAPIExpectSuccess().getHeader("X-Total"));
        int lastPage = (int) Math.ceil((double) total / getDefaultResultsPerPageNum());

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("page", String.valueOf(lastPage + 3));

        Response response = request.callAPIExpectSuccess();

        List<PhotoDto> photoDtos = response.as(new TypeRef<>() {
        });

        assertThat(photoDtos.size()).isEqualTo(0);
    }

    @Test(dataProvider = "invalid-integer-values", dataProviderClass = DataProviders.class)
    public void getDefaultResultIfPageSetToInvalidIntegerTest(int invalidInteger){
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("page", String.valueOf(invalidInteger));

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response);
    }

    @Test
    public void getDefaultResultIfPageIsEmptyTest(){
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("page", "");

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response);
    }

    @Test(dataProvider = "invalid-values-for-page-in-read-photos-in-collection", dataProviderClass = DataProviders.class)
    public void errorIfPageSetToInvalidValueTest(String invalidPage){
        String expectedErrorMessage =  "page is invalid";

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("page", invalidPage);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(expectedErrorMessage);
    }

    @Test(dataProvider = "over-default-number-of-items-per-page", dataProviderClass = DataProviders.class)
    public void getMoreThenDefaultNumberOfItemsPerPageButBelowLimitTest(int expectedNumberOfItemsPerPage) {
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("per_page", String.valueOf(expectedNumberOfItemsPerPage));

        Response response = request.callAPIExpectSuccess();
        String linkHeader = response.getHeader("Link");
        int total = Integer.parseInt(response.getHeader("X-Total"));

        List<PhotoDto> photoDtos = response.as(new TypeRef<>() {
        });

        int lastPage = (int) Math.ceil((double) total / expectedNumberOfItemsPerPage);
        String expectedLastPageString = String.format("/photos?page=%d&per_page=%d", lastPage, expectedNumberOfItemsPerPage);
        String expectedNextPageString = String.format("/photos?page=%d&per_page=%d", 2, expectedNumberOfItemsPerPage);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(photoDtos.size()).isEqualTo(expectedNumberOfItemsPerPage);
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(isFirstPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isPrevPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test
    public void getLimitResultsIfNumberOfResultsPerPageSetOverLimitTest(){
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("per_page", String.valueOf(getLimitForResultsPerPageNum() + 1));

        List<PhotoDto> photoDtos =request.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        assertThat(photoDtos.size()).isEqualTo(getLimitForResultsPerPageNum());
    }

    @Test(dataProvider = "below-default-number-of-items-per-page", dataProviderClass = DataProviders.class)
    public void getLessThenNumberOfItemsPerPageTest(int expectedNumberOfItemsPerPage) {
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("per_page", String.valueOf(expectedNumberOfItemsPerPage));

        Response response = request.callAPIExpectSuccess();
        String linkHeader = response.getHeader("Link");
        int total = Integer.parseInt(response.getHeader("X-Total"));

        List<PhotoDto> photoDtos = response.as(new TypeRef<>() {
        });

        int lastPage = (int) Math.ceil((double) total / expectedNumberOfItemsPerPage);
        String expectedLastPageString = String.format("/photos?page=%d&per_page=%d", lastPage, expectedNumberOfItemsPerPage);
        String expectedNextPageString = String.format("/photos?page=%d&per_page=%d", 2, expectedNumberOfItemsPerPage);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(photoDtos.size()).isEqualTo(expectedNumberOfItemsPerPage);
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(isFirstPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isPrevPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test
    public void getDefaultResultIfNumberOfItemsPerPageIsEmptyTest(){
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("per_page", "");

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response);
    }

    @Test(dataProvider = "invalid-integer-values", dataProviderClass = DataProviders.class)
    public void getLimitResultsIfNumberOfItemsPerPageSetToInvalidIntegerTest(int invalidInteger){
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("per_page", String.valueOf(invalidInteger));

        List<PhotoDto> photoDtos =request.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        assertThat(photoDtos.size()).isEqualTo(getLimitForResultsPerPageNum());
    }

    @Test(dataProvider = "invalid-values-for-number-of-items-per-page", dataProviderClass = DataProviders.class)
    public void errorIfNumberOfItemsPerPageSetToInvalidValueTest(String invalidNumber){
        String expectedErrorMsg = "per_page is invalid";

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("per_page", invalidNumber);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(expectedErrorMsg);
    }

    @Test
    public void landscapeOrientationPhotosTest() {
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("orientation", "landscape");

        List<PhotoDto> photoDtos = request.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        for (PhotoDto photoDto : photoDtos) {
            assertThat(photoDto.getWidth()).isGreaterThan(photoDto.getHeight());
        }
    }

    @Test
    public void portraitOrientationPhotosTest() {
        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.addUrlParameter("orientation", "portrait");

        List<PhotoDto> photoDtos = request.callAPIExpectSuccess().as(new TypeRef<>() {
        });

        for (PhotoDto photoDto : photoDtos) {
            assertThat(photoDto.getWidth()).isLessThan(photoDto.getHeight());
        }
    }

    @Test(dataProvider = "invalid-values-for-photo-orientation", dataProviderClass = DataProviders.class)
    public void errorIfInvalidOrientationTest(String invalidOrientation) {
        String expectedErrorMsg = "orientation does not have a valid value";

        ReadPhotosInCollectionMethod request = new ReadPhotosInCollectionMethod(validCollectionId);
        request.expectResponseStatus(HttpResponseStatusType.BAD_REQUEST_400);
        request.addUrlParameter("orientation", invalidOrientation);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(expectedErrorMsg);
    }
}
