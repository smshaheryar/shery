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
@Table(name = "department")
public class Department extends Model {


    @Column(name = "id")
    private Long id;

    @Column(name = "department_code")
    private String departmentCode;

    @Column(name = "description")
    private String description;






    public static final Finder<String, Department> find = new Finder<>(Department.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
