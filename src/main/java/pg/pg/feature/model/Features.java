package pg.pg.feature.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import pg.pg.feature.dto.FeaturesDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "features")
public class Features {
    @Id
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
    private transient Boolean update = true;

    @Builder.Default
    private transient Boolean write = true;

    public FeaturesDto toFeaturesDto() {
        return FeaturesDto.builder()
                .id(this.id)
                .name(this.name)
                .parentId(this.parentId)
                .orderBy(this.orderBy)
                .path(this.path)
                .icon(this.icon)
                .defaultChildId(this.defaultChildId)
                .update(this.update)
                .write(this.write)
                .build();
    }
}
