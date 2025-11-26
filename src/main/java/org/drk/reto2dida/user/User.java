package org.drk.reto2dida.user;

import jakarta.persistence.*;
import lombok.Data;
import org.drk.reto2dida.copia.Copia;
import org.drk.reto2dida.pelicula.Pelicula;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String password;

    @Column(name="is_admin")
    private Boolean isAdmin;

    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "user", fetch = FetchType.EAGER)
    private List<Pelicula> peliculas = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Copia> copies = new java.util.ArrayList<>();

    public void addGame(Pelicula g){
        g.setUser(this);
        this.peliculas.add(g);
    }
}
