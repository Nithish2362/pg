package pg.pg.utils;

import java.util.Date;

import jakarta.persistence.*;

import lombok.*;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Types.Status status = Types.Status.ACTIVE;

    @PrePersist
    public void prePersist() {
        Date now = new Date();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;

        if (status == null) {
            status = Types.Status.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = new Date();
    }
}