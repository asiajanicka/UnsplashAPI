package org.example.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppProperties {

    private static String getProperties(String key) {
        return ResourceBundle.getBundle("unsplash").getString(key);
    }

    public static String getToken() {
        return getProperties("token");
    }

    public static int getDefaultResultsPerPageNum() {
        return Integer.parseInt(getProperties("default_num_for_results_per_page"));
    }

    public static int getLimitForResultsPerPageNum() {
        return Integer.parseInt(getProperties("limit_num_for_results_per_page"));
    }

    public static int getTotalPagesLimit() {
        return Integer.parseInt(getProperties("total_pages_limit"));
    }
}
