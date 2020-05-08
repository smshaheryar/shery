package entities;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.function.LongFunction;


/**
 * Created by  on 09/06/2018.
 */

@Entity
@Table(name= "employee_detail")
public class   EmployeeDetails extends Model {

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name= "employee_id")
    private Long employee_id;

    @Column(name= "incharge_fullname")
    public String inchargeName;

    @Column(name= "incharge_id")
    private Long incharge_id;

    public Integer getCalendar_id() {
        return calendar_id;
    }

    public void setCalendar_id(Integer calendar_id) {
        this.calendar_id = calendar_id;
    }

    @Column(name= "calendar_id")
    private Integer calendar_id;

    @Column(name= "calendar_code")
    private String calendar_code;




    @Column(name= "department_id")
    private Long department_id;

    @Column(name= "department_code")
    private String department_code;


    @Column(name= "shift_id",nullable = true)
    private Long shift_id;

    @Column(name= "shift_code")
    private String shift_code;

    @Column(name= "role_id",nullable = true)
    private Long role_id;

    @Column(name= "role_code")
    private String role_code;

    @Column(name = "employment_status_id")
    private Long employmentStatusId;
    @Column(name = "employment_status_code")
    private String employmentStatusCode;

    public Long getDesignation_id() {
        return designation_id;
    }

    public void setDesignation_id(Long designation_id) {
        this.designation_id = designation_id;
    }

    @Column(name="designation_id")
    private Long designation_id;


    public String getDesignationCode() {
        return designationCode;
    }

    public void setDesignationCode(String designationCode) {
        this.designationCode = designationCode;
    }

    @Column(name="designation_code")
    private String designationCode;



    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }


    public Long getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(Long employee_id) {
        this.employee_id = employee_id;
    }

    public Long getIncharge_id() {
        return incharge_id;
    }

    public void setIncharge_id(Long incharge_id) {
        this.incharge_id = incharge_id;
    }



    public String getCalendar_code() {
        return calendar_code;
    }

    public void setCalendar_code(String calendar_code) {
        this.calendar_code = calendar_code;
    }



    public String getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    public Long getShift_id() {
        return shift_id;
    }

    public void setShift_id(Long shift_id) {
        this.shift_id = shift_id;
    }

    public String getShift_code() {
        return shift_code;
    }

    public void setShift_code(String shift_code) {
        this.shift_code = shift_code;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    public List<Roles> roles;

/*
    @OneToOne
    @JsonBackReference
    private LoginUser loginUser;


    @OneToMany( cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<LeaveBalance> leaveBalanceList;

    public LoginUser getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }
*/


    public static final Finder<Long, EmployeeDetails> find = new Finder<Long, EmployeeDetails>(EmployeeDetails.class);



    public CheckinCheckoutTime getCheckinCheckoutTime() {
        return checkinCheckoutTime;
    }

    public void setCheckinCheckoutTime(CheckinCheckoutTime checkinCheckoutTime) {
        this.checkinCheckoutTime = checkinCheckoutTime;
    }

    public String getInchargeName() {
        return inchargeName;
    }

    public void setInchargeName(String inchargeName) {
        this.inchargeName = inchargeName;
    }

    @OneToMany( cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private CheckinCheckoutTime checkinCheckoutTime;

    public AdjustmentRequest getAdjustmentRequest() {
        return adjustmentRequest;
    }

    public void setAdjustmentRequest(AdjustmentRequest adjustmentRequest) {
        this.adjustmentRequest = adjustmentRequest;
    }

    @OneToMany( cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private AdjustmentRequest adjustmentRequest;

 /*   public List<LeaveBalance> getLeaveBalanceList() {
        return leaveBalanceList;
    }

    public void setLeaveBalanceList(List<LeaveBalance> leaveBalanceList) {
        this.leaveBalanceList = leaveBalanceList;
    }

*/

    public Long getEmploymentStatusId() {
        return employmentStatusId;
    }

    public void setEmploymentStatusId(Long employmentStatusId) {
        this.employmentStatusId = employmentStatusId;
    }

    public String getEmploymentStatusCode() {
        return employmentStatusCode;
    }

    public void setEmploymentStatusCode(String employmentStatusCode) {
        this.employmentStatusCode = employmentStatusCode;
    }
}
