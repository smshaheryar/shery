package entities;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Shaheryar Nadeem on 7/30/2018.
 */
@Entity
@Table(name = "designation")
public class Designation extends Model{
@Id
    @Column(name = "id")
    private Long id;
@Column(name = "designation")
    private String designation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "description")
    private String description;



    public static  final Finder<String,Designation> find =new Finder<>(Designation.class);
}
