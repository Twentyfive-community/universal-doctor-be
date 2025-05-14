package model;

import enums.Sex;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        schema = "ms_user",
        name = "doctor"
)
public class Doctor extends MsUser {
    @ManyToOne(optional = false)
    private Profession profession;
    private Double hourlyRate;
    @Column(nullable = false)
    private Boolean accepted;
}
