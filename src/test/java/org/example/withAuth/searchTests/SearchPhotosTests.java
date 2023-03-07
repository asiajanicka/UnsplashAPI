package org.example.withAuth.searchTests;

import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.example.withAuth.dataProviders.DataProviders;
import org.example.endpoints.withAuth.search.SearchPhotosMethod;
import org.example.model.ErrorDto;
import org.example.model.PhotoDto;
import org.example.model.SearchPhotoDto;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.utils.CommonNames.baseSearchQuery;
import static org.example.utils.TestUtils.*;
import static org.example.utils.AppProperties.*;

public class SearchPhotosTests implements IAbstractTest {

    @Test(dataProvider = "valid-query-for-photo-search", dataProviderClass = DataProviders.class)
    public void validQueryWithTotalPagesAbove1Test(String query) {
        SearchPhotosMethod request = new SearchPhotosMethod(query);

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response, query);
    }

    private void assertDefaultResponse(Response response, String query) {
        SearchPhotoDto searchPhotoDto = response.as(SearchPhotoDto.class);
        String linkHeader = response.getHeader("Link");

        String expectedLastPageString = String.format("page=%d&query=%s", searchPhotoDto.getTotalPages(), query.replaceAll(" ","+"));
        String expectedNextPageString = String.format("page=2&query=%s", query.replaceAll(" ","+"));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getTotal()).isGreaterThan(getDefaultResultsPerPageNum());
        soft.assertThat(searchPhotoDto.getTotalPages()).isGreaterThan(1);
        soft.assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(getDefaultResultsPerPageNum());
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(isFirstPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isPrevPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    public void assertEmptyResponse(Response response) {
        SearchPhotoDto searchPhotoDto = response.as(SearchPhotoDto.class);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getTotal()).isEqualTo(0);
        soft.assertThat(searchPhotoDto.getTotalPages()).isEqualTo(0);
        soft.assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(0);
        soft.assertThat(response.getHeader("Link")).isNull();
        soft.assertAll();
    }

    @Test
    public void validQueryWithTotalPagesEqualTo1Test() {
        String query = "pierogi";

        SearchPhotosMethod request = new SearchPhotosMethod(query);

        Response response = request.callAPIExpectSuccess();

        SearchPhotoDto searchPhotoDto = response.as(SearchPhotoDto.class);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getTotal()).isGreaterThan(0).isLessThanOrEqualTo(getDefaultResultsPerPageNum());
        soft.assertThat(searchPhotoDto.getTotalPages()).isEqualTo(1);
        soft.assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(searchPhotoDto.getTotal());
        soft.assertThat(response.getHeader("Link")).isNull();
        soft.assertAll();
    }

    @Test
    public void validQueryWithTotalPagesEqualToLimitTest() {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getTotal()).isEqualTo(getTotalPagesLimit() * getDefaultResultsPerPageNum());
        soft.assertThat(searchPhotoDto.getTotalPages()).isEqualTo(getTotalPagesLimit());
        soft.assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(getDefaultResultsPerPageNum());
        soft.assertAll();
    }

    @Test
    public void validQueryWithNoResultsTest() {
        String query = "pneumonoultramicroscopicsilicovolcanoconiosis";

        SearchPhotosMethod request = new SearchPhotosMethod(query);

        Response response = request.callAPIExpectSuccess();

        assertEmptyResponse(response);
    }

    @Test(dataProvider = "search-photos-query-about-dog", dataProviderClass = DataProviders.class)
    public void validateIfDifferentQueriesReturnDifferentResultsTest(String dogResponseBodyAsSting) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);

        String responseBodyAsString = request.callAPI().getBody().asString();

        assertThat(responseBodyAsString).isNotEqualTo(dogResponseBodyAsSting);
    }

    @Test(dataProvider = "empty-values", dataProviderClass = DataProviders.class)
    public void emptyQueryTest(String emptyQuery) {
        SearchPhotosMethod request = new SearchPhotosMethod(emptyQuery);

        Response response = request.callAPIExpectSuccess();

        assertEmptyResponse(response);
    }

    @Test
    public void missingQueryFieldTest() {
        String expectedErrorMsg = "query is missing";

        SearchPhotosMethod request = new SearchPhotosMethod();
        request.expectResponseStatus(HttpResponseStatusType.BAD_REQUEST_400);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(expectedErrorMsg);
    }

    @Test
    public void longQueryWithSpacesTest() {
        String expectedQuery = "pneumonoultramicroscopicsilicovolcanoconiosis cat hamster";

        SearchPhotosMethod request = new SearchPhotosMethod(expectedQuery);

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getTotal()).isGreaterThan(0);
        soft.assertThat(searchPhotoDto.getTotalPages()).isGreaterThan(0);
        soft.assertThat(searchPhotoDto.getPhotos().size()).isGreaterThan(0);
        soft.assertAll();
    }

    @Test
    public void getGivenPageFromSearchResultTest() {
        int page = 4;

        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("page", String.valueOf(page));

        Response response = request.callAPIExpectSuccess();

        SearchPhotoDto searchPhotoDto = response.as(SearchPhotoDto.class);
        String linkHeader = response.getHeader("Link");

        String expectedFirstPageString = String.format("page=%d&query=%s", 1, baseSearchQuery);
        String expectedLastPageString = String.format("page=%d&query=%s", searchPhotoDto.getTotalPages(), baseSearchQuery);
        String expectedNextPageString = String.format("page=%d&query=%s", page + 1, baseSearchQuery);
        String expectedPrevPageString = String.format("page=%d&query=%s", page - 1, baseSearchQuery);

        assertThat(searchPhotoDto.getTotal()).isGreaterThan(page * getDefaultResultsPerPageNum());
        assertThat(searchPhotoDto.getTotalPages()).isGreaterThan(4);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(getFirstPageLinkFromHeader(linkHeader)).contains(expectedFirstPageString);
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(getPrevPageLinkFromHeader(linkHeader)).contains(expectedPrevPageString);
        soft.assertAll();
    }

    @Test(dataProvider = "query-with-total-number-of-pages", dataProviderClass = DataProviders.class)
    public void getLastPageForSearchPhotosTest(String query, int totalPages) {
        int minNumberOfResults = (totalPages - 1) * getDefaultResultsPerPageNum();
        SearchPhotosMethod request = new SearchPhotosMethod(query);
        request.addUrlParameter("page", String.valueOf(totalPages));

        Response response = request.callAPIExpectSuccess();

        SearchPhotoDto searchPhotoDto = response.as(SearchPhotoDto.class);
        String linkHeader = response.getHeader("Link");

        String expectedFirstPageString = String.format("page=%d&query=%s", 1, query);
        String expectedPrevPageString = String.format("page=%d&query=%s", totalPages - 1, query);

        assertThat(searchPhotoDto.getTotal()).isGreaterThan(minNumberOfResults + 1);
        assertThat(searchPhotoDto.getTotalPages()).isEqualTo(totalPages);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(getFirstPageLinkFromHeader(linkHeader)).contains(expectedFirstPageString);
        soft.assertThat(getPrevPageLinkFromHeader(linkHeader)).contains(expectedPrevPageString);
        soft.assertThat(isNextPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isLastPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test
    public void validateIfDifferentPagesForTheSameQueryReturnDifferentResultsTest() {
        SearchPhotosMethod requestForSecond = new SearchPhotosMethod(baseSearchQuery);
        requestForSecond.addUrlParameter("page", "2");

        String responseBodyAsStringForSecondPage = requestForSecond.callAPIExpectSuccess().getBody().asString();

        SearchPhotosMethod requestForThirdPage = new SearchPhotosMethod(baseSearchQuery);
        requestForThirdPage.addUrlParameter("page", "3");

        String responseBodyAsStringForThirdPage = requestForThirdPage.callAPIExpectSuccess().getBody().asString();

        assertThat(responseBodyAsStringForSecondPage).isNotEqualTo(responseBodyAsStringForThirdPage);
    }

    @Test(dataProvider = "query-with-total-number-of-pages", dataProviderClass = DataProviders.class)
    public void getPageOverTotalNumberOfPagesTest(String query, int totalPages) {
        SearchPhotosMethod request = new SearchPhotosMethod(query);
        request.addUrlParameter("page", String.valueOf(totalPages + 2));

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(0);
    }

    @Test(dataProvider = "invalid-integer-values", dataProviderClass = DataProviders.class)
    public void getDefaultResultIfPageSetToInvalidIntegerTest(int invalidInteger) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("page", String.valueOf(invalidInteger));

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response, baseSearchQuery);
    }

    @Test(dataProvider = "invalid-values-for-page-in-read-photos-in-collection", dataProviderClass = DataProviders.class)
    public void getDefaultResultIfPageSetToInvalidValueTest(String invalidPage) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("page", invalidPage);

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response, baseSearchQuery);
    }

    @Test(dataProvider = "over-default-number-of-items-per-page", dataProviderClass = DataProviders.class)
    public void getMoreThenDefaultNumberOfItemsPerPageButBelowLimitSearchTest(int expectedNumberOfItemsPerPage) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("per_page", String.valueOf(expectedNumberOfItemsPerPage));

        Response response = request.callAPIExpectSuccess();
        String linkHeader = response.getHeader("Link");

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        int expectedNumberOfTotalPages = (int) Math.ceil((double) searchPhotoDto.getTotal() / expectedNumberOfItemsPerPage);
        assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(expectedNumberOfItemsPerPage);
        assertThat(searchPhotoDto.getTotalPages()).isEqualTo(expectedNumberOfTotalPages);

        String expectedLastPageString = String.format("page=%d&per_page=%d&query=%s", expectedNumberOfTotalPages,
                expectedNumberOfItemsPerPage, baseSearchQuery);
        String expectedNextPageString = String.format("page=%d&per_page=%d&query=%s", 2,
                expectedNumberOfItemsPerPage, baseSearchQuery);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(expectedNumberOfItemsPerPage);
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(isFirstPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isPrevPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test
    public void getLimitResultsIfNumberOfResultsPerPageSetOverLimit() {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("per_page", String.valueOf(getLimitForResultsPerPageNum() + 1));

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(getLimitForResultsPerPageNum());
    }

    @Test(dataProvider = "below-default-number-of-items-per-page", dataProviderClass = DataProviders.class)
    public void getLessThenDefaultNumberOfItemsPerPageSearchTest(int expectedNumberOfItemsPerPage) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("per_page", String.valueOf(expectedNumberOfItemsPerPage));

        Response response = request.callAPIExpectSuccess();
        String linkHeader = response.getHeader("Link");

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        int expectedNumberOfTotalPages = (int) Math.ceil((double) searchPhotoDto.getTotal() / expectedNumberOfItemsPerPage);
        assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(expectedNumberOfItemsPerPage);
        assertThat(searchPhotoDto.getTotalPages()).isEqualTo(expectedNumberOfTotalPages);

        String expectedLastPageString = String.format("page=%d&per_page=%d&query=%s", expectedNumberOfTotalPages, expectedNumberOfItemsPerPage, baseSearchQuery);
        String expectedNextPageString = String.format("page=%d&per_page=%d&query=%s", 2, expectedNumberOfItemsPerPage, baseSearchQuery);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(expectedNumberOfItemsPerPage);
        soft.assertThat(getLastPageLinkFromHeader(linkHeader)).contains(expectedLastPageString);
        soft.assertThat(getNextPageLinkFromHeader(linkHeader)).contains(expectedNextPageString);
        soft.assertThat(isFirstPageLinkAdded(linkHeader)).isFalse();
        soft.assertThat(isPrevPageLinkAdded(linkHeader)).isFalse();
        soft.assertAll();
    }

    @Test(dataProvider = "invalid-integer-values", dataProviderClass = DataProviders.class)
    public void getOneResultIfNumberOfItemsPerPageSetToInvalidIntegerTest(int invalidInteger) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("per_page", String.valueOf(invalidInteger));

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(1);
    }

    @Test(dataProvider = "invalid-values-for-number-of-items-per-page", dataProviderClass = DataProviders.class)
    public void getOneResultIfNumberOfItemsPerPageSetToInvalidValueTest(String invalidNumber) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("per_page", invalidNumber);

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        assertThat(searchPhotoDto.getPhotos().size()).isEqualTo(1);
    }

    @Test
    public void orderByLatestSearchTest() {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("order_by", "latest");

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        List<Instant> updatedAtList = searchPhotoDto.getPhotos()
                .stream().map(PhotoDto::getUpdatedAt).collect(Collectors.toList());

        assertThat(updatedAtList).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test(dataProvider = "invalid-values-for-optional-params", dataProviderClass = DataProviders.class)
    public void getDefaultResultIfOrderBySetToInvalidValueTest(String invalidValue) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("order_by", invalidValue);

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response, baseSearchQuery);
    }

    @Test(dataProvider = "collectionsIds", dataProviderClass = DataProviders.class)
    public void collectionBasedSearchTest(List<String> collectionsIdsList) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("collections", collectionsIdsList.get(0));

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response, baseSearchQuery);
    }

    @Test(dataProvider = "invalid-collection-ids", dataProviderClass = DataProviders.class)
    public void getDefaultResultIfCollectionIdSetToInvalidValueTest(String invalidCollectionId) {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("collections", invalidCollectionId);

        Response response = request.callAPIExpectSuccess();

        assertDefaultResponse(response, baseSearchQuery);
    }

    @Test
    public void landscapeOrientationSearchPhotosTest() {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("orientation", "landscape");
        request.setLogRequest(true);
        request.setLogResponse(true);

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        for (PhotoDto photoDto : searchPhotoDto.getPhotos()) {
            assertThat(photoDto.getWidth()).isGreaterThan(photoDto.getHeight());
        }
    }

    @Test
    public void portraitOrientationSearchPhotosTest() {
        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("orientation", "portrait");
        request.setLogRequest(true);
        request.setLogResponse(true);

        SearchPhotoDto searchPhotoDto = request.callAPIExpectSuccess().as(SearchPhotoDto.class);

        for (PhotoDto photoDto : searchPhotoDto.getPhotos()) {
            assertThat(photoDto.getWidth()).isLessThan(photoDto.getHeight());
        }
    }

    @Test(dataProvider = "invalid-values-for-optional-params", dataProviderClass = DataProviders.class)
    public void errorIfInvalidOrientationTest(String invalidOrientation) {
        String expectedErrorMsg = "orientation does not have a valid value";

        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.expectResponseStatus(HttpResponseStatusType.BAD_REQUEST_400);
        request.addUrlParameter("orientation", invalidOrientation);
        request.setLogRequest(true);
        request.setLogResponse(true);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(expectedErrorMsg);
    }

    @Test(dataProvider = "invalid-values-for-optional-params", dataProviderClass = DataProviders.class)
    public void errorIfInvalidColorSearchPhotosTest(String invalidColor) {
        String expectedErrorMsg = "color does not have a valid value";

        SearchPhotosMethod request = new SearchPhotosMethod(baseSearchQuery);
        request.addUrlParameter("color", invalidColor);
        request.expectResponseStatus(HttpResponseStatusType.BAD_REQUEST_400);
        request.setLogRequest(true);
        request.setLogResponse(true);

        ErrorDto errorDto = request.callAPI().as(ErrorDto.class);

        assertThat(errorDto.getMessages()).contains(expectedErrorMsg);
    }
}
