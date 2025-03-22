package com.epam.resource.repository.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "resources")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String resourceId;

}
