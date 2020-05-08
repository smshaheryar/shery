package entities;

import com.avaje.ebean.Model;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

/**
 * Created by Shaheryar Nadeem on 7/20/2018.
 */
@Entity
@Table(name = "employee_master")
public class EmployeeMasters extends Model implements Comparable<EmployeeMasters>{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "cnic")
    private String cnic;

    @Column(name = "father_name")
    private String father_name;

    @Column(name = "mother_name")
    private String mother_name;

    @Column(name = "dob")
    private Date dob;

    public Integer getNationality_id() {
        return nationality_id;
    }

    public void setNationality_id(Integer nationality_id) {
        this.nationality_id = nationality_id;
    }

    @Column(name = "nationality_id")
    private Integer nationality_id;



    @Column(name = "nationality_code")
    private String nationality_code;

    @Column(name = "gender")
    private String gender;
    @Column(name = "marital_status")
    private String marital_status;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "ptcl")
    private String ptcl;


    @Column(name = "address")
    private String address;

    @Column(name = "email_id_official")
    private String email_id_official;
    @Column(name = "email_id_personal")
    private String email_id_personal;
    @Column(name = "blood_group")
    private String blood_group;
    @Column(name = "ice")
    private String ice;
    @Column(name = "relation")
    private String relation;
    @Column(name = "doj")
    private Date doj;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Column(name = "is_active")
    private boolean isActive;
    @Column(name ="timezone")
    private String timezone;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    @Column(name ="imagepath")
    private String image_path;


    public Date getDoj() {
        return doj;
    }

    public void setDoj(Date doj) {
        this.doj = doj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }



    public String getNationality_code() {
        return nationality_code;
    }

    public void setNationality_code(String nationality_code) {
        this.nationality_code = nationality_code;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPtcl() {
        return ptcl;
    }

    public void setPtcl(String ptcl) {
        this.ptcl = ptcl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail_id_official() {
        return email_id_official;
    }

    public void setEmail_id_official(String email_id_official) {
        this.email_id_official = email_id_official;
    }

    public String getEmail_id_personal() {
        return email_id_personal;
    }

    public void setEmail_id_personal(String email_id_personal) {
        this.email_id_personal = email_id_personal;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getIce() {
        return ice;
    }

    public void setIce(String ice) {
        this.ice = ice;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public static final Finder<Long, EmployeeMasters> find = new Finder<Long, EmployeeMasters>(EmployeeMasters.class);

    @Override
    public int compareTo(@NotNull EmployeeMasters o) {
        return  ( o.getFull_name()).compareTo(this.getFull_name());
    }


}
