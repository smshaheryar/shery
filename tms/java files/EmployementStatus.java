package entities;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Shaheryar Nadeem on 8/2/2018.
 */
@Entity
@Table(name = "employment_status")
public class EmployementStatus extends Model {

    @Column(name = "id")
    private Long id;



    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "description")
    private String description;

    public static final Finder<String, EmployementStatus> find = new Finder<>(EmployementStatus.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}




