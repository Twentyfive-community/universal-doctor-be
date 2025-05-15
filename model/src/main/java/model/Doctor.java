package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
