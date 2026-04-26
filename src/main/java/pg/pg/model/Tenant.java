package pg.pg.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pgNumber;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String mobileNumber;

    private String fatherName;
    private String fatherMobile;
    private String motherName;
    private String motherMobile;

    @Column(nullable = false)
    private String email;

    private LocalDate dob;

    @Column(length = 500)
    private String address;

    @ManyToOne
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @Column(name = "bed_id", insertable = false, updatable = false)
    private Long bedId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    private LocalDate joinDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    public Tenant() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPgNumber() { return pgNumber; }
    public void setPgNumber(String pgNumber) { this.pgNumber = pgNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getFatherMobile() { return fatherMobile; }
    public void setFatherMobile(String fatherMobile) { this.fatherMobile = fatherMobile; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getMotherMobile() { return motherMobile; }
    public void setMotherMobile(String motherMobile) { this.motherMobile = motherMobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Bed getBed() { return bed; }
    public void setBed(Bed bed) { this.bed = bed; }

    public Long getBedId() { return bedId; }
    public void setBedId(Long bedId) { this.bedId = bedId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
