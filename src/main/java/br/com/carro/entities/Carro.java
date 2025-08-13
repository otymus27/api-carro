package br.com.carro.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tb_carro")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Carro {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String cor;

    @Column(nullable = false)
    private Integer ano;
}
