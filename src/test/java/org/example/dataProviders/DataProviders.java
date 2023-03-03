package org.example.dataProviders;

import org.example.endpoints.search.SearchCollectionsMethod;
import org.example.model.SearchCollectionDto;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.CommonNames.baseSearchQuery;

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
}
