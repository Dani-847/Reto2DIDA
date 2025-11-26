package org.drk.reto2dida.user;

import jakarta.persistence.*;
import lombok.Data;
import org.drk.reto2dida.copia.Copia;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Copia> copias = new ArrayList<>();

    public void addCopia(Copia copia) {
        copia.setUser(this);
        this.copias.add(copia);
    }
}
