package model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        schema = "ms_user",
        name = "profession"
)
public class Profession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean active;
}
