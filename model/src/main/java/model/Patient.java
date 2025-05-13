package model;

import enums.Sex;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        schema = "ms_user",
        name = "patient"
)
public class Patient extends MsUser {
}
