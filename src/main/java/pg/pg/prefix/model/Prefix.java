package pg.pg.prefix.model;

import jakarta.persistence.*;
import pg.pg.common.util.PrefixType;

@Entity
@Table(name = "prefixes")
public class Prefix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prefix;

    @Column(nullable = false)
    private long currentSequence = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PrefixType prefixType;

    public Prefix() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public long getCurrentSequence() { return currentSequence; }
    public void setCurrentSequence(long currentSequence) { this.currentSequence = currentSequence; }

    public PrefixType getPrefixType() { return prefixType; }
    public void setPrefixType(PrefixType prefixType) { this.prefixType = prefixType; }

    public void incrementSequenceNo() {
        this.currentSequence++;
    }
}
