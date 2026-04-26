package pg.pg.utils;

import pg.pg.utils.Types.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BaseDto {

    private String id;

    private String createdBy;

    private String updatedBy;

    private Date updatedDate;

    private Date createdDate;

    @Builder.Default
    private Types.Status status = Status.ACTIVE;
}
