package pg.pg.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeaturesDto {
    private String id;
    private String name;
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
