package pg.pg.prefix.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.utils.BaseModel;
import pg.pg.utils.Types;

@Entity
@Table(name = "prefixes")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Prefix extends BaseModel {

    @Column(nullable = false, unique = true, length = 50)
    private String prefix;

    @Builder.Default
    @Column(nullable = false)
    private long currentSequence = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Types.PrefixType prefixType;

    public void incrementSequenceNo() {
        this.currentSequence++;
    }
}