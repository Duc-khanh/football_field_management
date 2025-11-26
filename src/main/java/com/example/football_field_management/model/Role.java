package com.example.football_field_management.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;
    private String description;
    @Column(name = "role_name")
    private String roleName;
    @ManyToMany(mappedBy = "roles")
    @JsonManagedReference
    private List<Account> accounts;
}


