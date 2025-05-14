package model;

import enums.Sex;
import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public class MsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    @Column(nullable = false)
    private Sex sex;
    @Column(nullable = false,unique = true)
    private String taxCode;
    @Column(nullable = false)
    private Boolean active;
    @Column(nullable = false,unique = true)
    private String email;
    private String imgUrl;
    private String nationality;
}
