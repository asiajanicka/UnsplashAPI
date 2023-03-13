package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SearchCollectionDto {

    @JsonProperty("total")
    private int total;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("results")
    private List<CollectionDto> collections;
}
