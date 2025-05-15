package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(
        schema = "ms_user",
        name = "patient"
)
public class Patient extends MsUser {
}
