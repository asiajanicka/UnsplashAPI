package org.example.dataProviders;

import org.example.endpoints.search.SearchCollectionsMethod;
import org.example.model.SearchCollectionDto;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.Utils.CommonNames.baseSearchQuery;
import static org.example.utils.AppProperties.getDefaultResultsPerPageNum;

public class DataProviders {

    @DataProvider(name = "collectionsIds")
    public static Object[][] collectionsIds() {
        SearchCollectionsMethod request = new SearchCollectionsMethod(baseSearchQuery);
        List<String > collectionsIdsList = request.callAPI()
                .as(SearchCollectionDto.class)
                .getCollections()
                .stream()
                .map(p->p.getId())
                .collect(Collectors.toList());
        return new Object[][]{{collectionsIdsList}};
    }

    @DataProvider(name = "invalid-collection-ids")
    public static Object[][] invalidCollectionIds() {
        return new Object[][]{{"-1"}, {"invalid"}, {"1.5"}, {"0"}, {"a"}};
    }

    @DataProvider(name = "empty-values")
    public static Object[][] getEmptyValues() {
        return new Object[][]{{""},{" "} };
    }

    @DataProvider(name = "invalid-values-for-page-in-read-photos-in-collection")
    public static Object[][] invalidValuesForPageInReadPhotosInCollection() {
        return new Object[][]{{"invalid"}, {" "}, {"1.5"}};
    }

    @DataProvider(name = "invalid-integer-values")
    public static Object[][] invalidIntegerValues() {
        return new Object[][]{{-1}, {0}};
    }

    @DataProvider(name = "over-default-number-of-items-per-page")
    public static Object[][] overDefaultNumberOfItemsPerPage() {
        return new Object[][]{{getDefaultResultsPerPageNum() + 3}, {getDefaultResultsPerPageNum() + 20}};
    }

    @DataProvider(name = "below-default-number-of-items-per-page")
    public static Object[][] belowDefaultNumberOfItemsPerPage() {
        return new Object[][]{{1}, {5}};
    }

    @DataProvider(name = "invalid-values-for-number-of-items-per-page")
    public static Object[][] invalidValuesForNumberOfItemsPerPage() {
        return new Object[][]{{"-1"}, {"invalid"}, {" "}, {"1.5"}, {""}};
    }

    @DataProvider(name = "invalid-values-for-photo-orientation")
    public static Object[][] invalidValuesForPhotoOrientation() {
        return new Object[][]{{"-1"}, {"invalid"}, {" "}, {"1.5"}, {""}};
    }
}
