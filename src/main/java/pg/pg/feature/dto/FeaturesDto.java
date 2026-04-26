package pg.pg.feature.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeaturesDto {
    private String id;
    private String name;

    @JsonProperty("parent_id")
    @JsonAlias("parentId")
    private String parentId;

    private Integer orderBy;
    private String path;
    private String icon;
    private String defaultChildId;

    @Builder.Default
    private Boolean update = true;

    @Builder.Default
    private Boolean write = true;
}
