package entities;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name="roles")
public class Roles extends Model {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="role_coe")
    private String role;


    @Column(name = "description")
    private String description;



    /*@ManyToOne
    @JsonBackReference
    private LoginUser loginUser;
    public LoginUser getLoginUser() {
        return loginUser;
    }



    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }*/


    public static final Finder<String, Roles> find = new Finder<>(Roles.class);


    /*private EmployeeDetails employeeDetails;

    public EmployeeDetails getEmployeeDetails() {
        return employeeDetails;
    }

    public void setEmployeeDetails(EmployeeDetails employeeDetails) {
        this.employeeDetails = employeeDetails;
    }


    private EmployeeMasters employeeMasters;
    public EmployeeMasters getEmployeeMasters() {
        return employeeMasters;
    }

    public void setEmployeeMasters(EmployeeMasters employeeMasters) {
        this.employeeMasters = employeeMasters;
    }*/












    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
