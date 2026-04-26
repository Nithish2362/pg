package pg.pg.utils;

import java.util.Date;
import pg.pg.utils.Types.Status;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String createdBy;

    private String updatedBy;

    private Date updatedDate;

    @Column(nullable = false, updatable = false)
    private Date createdDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Types.Status status = Status.ACTIVE;
}
