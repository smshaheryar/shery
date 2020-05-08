package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import entities.*;
import model.EmployeeInformations;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Constants;
import utils.Crypto;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import static java.lang.Integer.parseInt;

/**
 * Created by Shaheryar Nadeem on 7/24/2018.
 */
/*@Restrict({@Group("Admin")} )*/

public class UserController extends Controller {

    String emailaddress="";
    BufferedImage img = null;
    String uploadedFileName="";
   // @Restrict({@Group("Admin")} )



           public Result profile() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
        DateFormat df3 = new SimpleDateFormat("dd-MMM-yyyy");
        String employee="";
        String inchargename="";
        String inchargeID="";
        String id;
        EmployeeMasters employeeMasters = new EmployeeMasters();

        try {
            JSONObject jsonObject = new JSONObject(request().body().asJson().toString());

            String encPass = Cryp
            to.encrypt(jsonObject.getString("password"),Constants.SECRET_KEY);

            //Logger.info("jsonObject: "+ jsonObject.toString());
   /*         Logger.info("jsonObject: " + jsonObject.getString("fullName"));
            Logger.info("jsonObject: " + jsonObject.getString("cnic"));
            Logger.info("jsonObject: " + jsonObject.getString("fatherName"));
            Logger.info("jsonObject: " + jsonObject.getString("motherName"));
            Logger.info("date: " + jsonObject.getString("date"));
            Logger.info("designation: " + jsonObject.getFString("designation"));
            Logger.info("department: " + jsonObject.getString("department"));
            Logger.info("jsonObject: " + jsonObject.getString("incharge"));
            Logger.info("mobileNumber: " + jsonObject.getLong("mobileNumber"));
            Logger.info("ptclNumber: " + jsonObject.getLong("ptclNumber"));
            Logger.info("emailIdOfficial: " + jsonObject.getString("emailIdOfficial"));
            Logger.info("emailIdPersonal: " + jsonObject.getString("emailIdPersonal"));
            Logger.info("address: " + jsonObject.getString("address"));
            Logger.info("bloodgroup: " + jsonObject.getString("bloodgroup"));
            Logger.info("ice: " + jsonObject.getString("ice"));
            Logger.info("relation: " + jsonObject.getString("relation"));*/
            //  Logger.info("role: " + jsonObject.getString("role"));
            Logger.info("ice: " + jsonObject.getString("employemntstatus"));

            Long role1=Long.parseLong(jsonObject.getString("role"));

            Long deptid=Long.parseLong(jsonObject.getString("department"));
            Long designid=Long.parseLong(jsonObject.getString("designation"));
            Long employid=Long.parseLong(jsonObject.getString("employemntstatus"));
            Long shiftid=Long.parseLong(jsonObject.getString("shift"));


            Logger.info("" + jsonObject.getString("iid"));
            inchargename= jsonObject.getString("iid");

            Logger.info("" + jsonObject.getString("incharge"));
            inchargeID= jsonObject.getString("incharge");
            Logger.info("" + inchargeID);

            EmployeeDetails employeeDetails = new EmployeeDetails();
            Roles roles = new Roles();
            LoginUser loginUser = new LoginUser();
            String checkpoint = jsonObject.getString("emailIdOfficial");
            EmployeeMasters employeeMasters1 = Ebean.find(EmployeeMasters.class).where().eq("Email_id_official",checkpoint).findUnique();

            if (employeeMasters1 == null) {
//setting values in employee masters

                employeeMasters.setCnic(jsonObject.getString("cnic"));
                employeeMasters.setFull_name(jsonObject.getString("fullName").trim());
                employeeMasters.setFather_name(jsonObject.getString("fatherName"));
                employeeMasters.setMother_name(jsonObject.getString("motherName"));


                String dateInString = jsonObject.getString("dateofbirth");

                //settin of date
                SimpleDateFormat  df = new SimpleDateFormat("MM/dd/yyyy");
                java.util.Date Cin = null;
                java.util.Date Cout = null;
                java.sql.Date sqlStartDate = null;
                Cout = df.parse(dateInString);
                sqlStartDate = new java.sql.Date(Cout.getTime());

                employeeMasters.setDob(sqlStartDate);



                String date = jsonObject.getString("dateofjoining");
                SimpleDateFormat  aa = new SimpleDateFormat("MM/dd/yyyy");
               . java.util.Date date1 = null;
                java.util.Date date2 = null;
                java.sql.Date sqlStartDate3 = null;

                date2 = aa.parse(date);
                sqlStartDate3=new  java.sql.Date(date2.getTime());





                // employeeMasters.setDob(new Date(System.currentTimeMillis()));
                employeeMasters.setDoj(sqlStartDate3);
                employeeMasters.setNationality_id(2);
                employeeMasters.setNationality_code(jsonObject.getString("nationality"));
                employeeMasters.setGender(jsonObject.getString("gender"));
                employeeMasters.setMarital_status(jsonObject.getString("maritalstatus"));
                //  employeeMasters.setMother_name(jsonObject.getString("department"));
                //  employeeMasters.set(jsonObject.getString("designation"));
                //employeeMasters.setMother_name(jsonObject.getString("department"));
                employeeMasters.setMobile(jsonObject.getString("mobileNumber"));
                employeeMasters.setPtcl(jsonObject.getString("ptclNumber"));
                employeeMasters.setEmail_id_official(jsonObject.getString("emailIdOfficial"));
              //  emailaddress=jsonObject.getString("emailIdOfficial");

                employeeMasters.setEmail_id_personal(jsonObject.getString("emailIdPersonal"));
                employeeMasters.setAddress(jsonObject.getString("address"));
                employeeMasters.setBlood_group(jsonObject.getString("bloodgroup"));
                employeeMasters.setIce(jsonObject.getString("ice"));
                employeeMasters.setRelation(jsonObject.getString("relation"));
                employeeMasters.setTimezone(jsonObject.getString("timezone"));
                employeeMasters.setImage_path(emailaddress);
                employeeMasters.setActive(true);
                Ebean.save(employeeMasters);


                Logger.info("employeeMasters.getId(): " + employeeMasters.getId());

                loginUser.setEmail(employeeMasters.getEmail_id_official());
                loginUser.setEmployeeId(employeeMasters.getId().intValue());
                loginUser.setPassword(encPass);

                String email = session().get("pa.u.id");
                LoginUser loginUser1 = LoginUser.find.where().eq("email",email).findUnique();
                String sqlTime = "SELECT now()";
                SqlQuery sqlQuery = Ebean.createSqlQuery(sqlTime);
                SqlRow row = sqlQuery.findUnique();
                loginUser.setLast_update(row.getTimestamp("now"));
                loginUser.setUpdatebyId(Long.parseLong(String.valueOf(loginUser1.getEmployeeId())));

                Logger.info("setEmail.getId(): " + loginUser.getEmail());
                Logger.info("setEmployee_id.getId(): " + employeeMasters.getId());
                Logger.info("setPassword.getId(): " + loginUser.getPassword());
                Ebean.save(loginUser);



                //setting values in roles
  /*              roles.setId(jsonObject.getLong(jsonObject.getString("role")));
                roles.setRole(jsonObject.getString("rolecode"));
                roles.setDescription("employee");
                Ebean.save(roles);
*/
//setting values in employee details

                Roles roles1 = Roles.find.where().eq("id", role1).findUnique();
                Department department1=Department.find.where().eq("id",deptid).findUnique();
                Designation designation1 =Designation.find.where().eq("id",designid).findUnique();
                Shift shift1=Shift.find.where().eq("id",shiftid).findUnique();
                List<EmployeeDetails> employeeDetailsList=Ebean.find(EmployeeDetails.class).where().or(com.avaje.ebean.Expr.eq("role_code","Administrator"),com.avaje.ebean.Expr.eq("role_code","incharge")).findList();
                EmployementStatus employementStatus=EmployementStatus.find.where().eq("id",employid).findUnique();

                employeeDetails.setEmployee_id(employeeMasters.getId());
                employee="ID ="+employeeDetails.getEmployee_id();
                Logger.info("employeedetailId: " + employeeDetails.getEmployee_id());


                emailaddress=employeeMasters.getEmail_id_official();
                employeeDetails.setIncharge_id(jsonObject.getLong("incharge"));
                employeeDetails.setInchargeName(jsonObject.getString("iid"));
                employeeDetails.setCalendar_id(1);
                employeeDetails.setCalendar_code("augest");
                employeeDetails.setDepartment_code(department1.getDescription());
                employeeDetails.setDepartment_id(department1.getId());//how to set long value
                employeeDetails.setShift_id(jsonObject.getLong("shift"));
                employeeDetails.setShift_code(shift1.getShift_code());
                employeeDetails.setRole_id(roles1.getId());
                employeeDetails.setRole_code(roles1.getRole());
                employeeDetails.setDesignation_id(designation1.getId());
                employeeDetails.setDesignationCode(designation1.getDesignation());
                employeeDetails.setEmploymentStatusId(employementStatus.getId());
                employeeDetails.setEmploymentStatusCode(employementStatus.getStatusCode());

                Ebean.save(employeeDetails);
                employee="ID "+employeeDetails.getEmployee_id();
                sendEmail(employeeDetails.getEmployee_id());

            } else {

                return ok("user with this official ID is already present");
                //    Logger.info("value is already presermt");


            }


        } catch (Exception e) {
            return ok("UserController.saveUserDetails"+e);
        }

        return ok(Json.toJson(employeeMasters));
    }

    public Result getEmployeeDetails(String employeeName) {

        JSONObject parentObject = new JSONObject();
 /*       EmployeeMasters info1 = null;
        EmployeeDetails info2 = null;*/
        String info3=null;
        Long id;
        EmployeeMasters employeeMasters = null;
        EmployeeDetails employeeDetails = null;
        EmployeeInformations employeeInformations = new EmployeeInformations();

        LoginUser loginUser = null;
        EmployementStatus employementStatus=null;
        try {
            employeeMasters = Ebean.find(EmployeeMasters.class).where().eq("full_name", employeeName).findUnique();
                List<EmployeeMasters> employeeMastersList= Ebean.find(EmployeeMasters.class).where().eq("full_name",employeeMasters.getFull_name()).findList();

            employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", employeeMasters.getId()).findUnique();
            //  Roles roles= Ebean.find(Roles.class).where().eq("role_coe",role).findUnique();
            loginUser=Ebean.find(LoginUser.class).where().eq("employee_id", employeeMasters.getId()).findUnique();
            employementStatus=Ebean.find(EmployementStatus.class).where().eq("id", employeeDetails.getId()).findUnique();
            if (employeeMasters == null) {
                parentObject.put("code", 0);
                parentObject.put("value", "No result found");
            } else {
                employeeDetails = Ebean.find(EmployeeDetails.class).where().eq("employee_id", employeeMasters.getId()).findUnique();
                //info1=Ebean.find(EmployeeMasters.class).where().eq("full_name",emp-loyeeName).findUnique();

                employeeInformations.setEmployeeMaster(employeeMasters);
                emailaddress=employeeMasters.getEmail_id_official();
                //  employeeInformations.setEmployeeDetails(employeeDetails);
                //employeeInformations.setEmployeeDetailId(employeeDetails.getId());
                employeeInformations.setEmployeeDetailId(employeeDetails.getId());
                employeeInformations.setEmployee_id(employeeDetails.getEmployee_id());
                employeeInformations.setInchargeName(employeeDetails.getInchargeName());
                employeeInformations.setIncharge_id(employeeDetails.getIncharge_id());
                employeeInformations.setCalendar_id(employeeDetails.getCalendar_id());
                employeeInformations.setCalendar_code(employeeDetails.getCalendar_code());
                employeeInformations.setDepartment_id(employeeDetails.getDepartment_id());
                employeeInformations.setDepartment_code(employeeDetails.getDepartment_code());
                employeeInformations.setShift_id(employeeDetails.getShift_id());
                employeeInformations.setShift_code(employeeDetails.getShift_code());
                employeeInformations.setRole_id(employeeDetails.getRole_id());
                employeeInformations.setRole_code(employeeDetails.getRole_code());
                employeeInformations.setDesignation_id(employeeDetails.getDesignation_id());
                employeeInformations.setDesignationCode(employeeDetails.getDesignationCode());
                employeeInformations.setPassword(Crypto.decrypt(loginUser.getPassword(), Constants.SECRET_KEY));
                employeeInformations.setEmployeeStatusId(employeeDetails.getEmploymentStatusId());
                employeeInformations.setEmployeeStatusCode(employeeDetails.getEmploymentStatusCode());
               // employeeInformations.setEmployeeMastersList(employeeMastersList);
                employeeInformations.setImagePath(emailaddress);

             /*   File folderInput = new File("C:\\Projects\\conure_time-management-system\\baseline"+employeeMasters.getEmail_id_official()+".png");
                BufferedImage folderImage = ImageIO.read(folderInput);*/
                //img = ImageIO.read(new File("C:\\Projects\\conure_time-management-system\\baseline\\"+employeeMasters.getEmail_id_official()+".png"));
            }
        } catch(Exception e) {
            Logger.error("", e);
        }
        return ok(Json.toJson(employeeInformations));
    }


    public Result submit() {
        String type="";
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart fileP = body.getFile("file");

        long employeeMasterId = Long.parseLong(request().body().asMultipartFormData().asFormUrlEncoded().get("employeeMasterId")[0]);
        JSONObject obj = new JSONObject();

        System.out.println("Done1"+ employeeMasterId);
        if (fileP != null) {

            File file = (File) fileP.getFile();
            File fileChecker = new File("baseline",fileP.getFilename());
            //File filechecker1 =new File("baseline",fileP.getContentType().s);


            String contentType = fileP.getContentType().split("/")[1].toString();

            //  System.out.println("file is   already present");

            EmployeeMasters employeeMasters = null;

            try {
                if (employeeMasterId > 0) {
                    employeeMasters = Ebean.find(EmployeeMasters.class).where().eq("id", employeeMasterId).findUnique();
                }



                File file2=null;
                File file1 =new File("baseline",fileP.getFilename());



                file2=new File("baseline",""+emailaddress+"."+contentType);
                if (file2.exists()) {
                    System.out.println("file is   already present");
                      file2.delete();
                    System.out.println("File is deleted now");

                    //return ok();
                }

                Logger.info(emailaddress+"."+contentType);
                employeeMasters.setImage_path(emailaddress+"."+contentType);

                Logger.info(employeeMasters.getImage_path());
                Ebean.update(employeeMasters);
                  /*  if(file2.exists()){
                        System.out.println("file is already present");

                    }
*/
                Ebean.update(employeeMasters);
              /*  File file2=new File("baseline",""+emailaddress+".png");
                if(file2.exists()){
                    System.out.println("file is   already present");
                }*/
                boolean success = file.renameTo(file2);
                java.io.FileWriter out= new java.io.FileWriter(file2, true /*append=yes*/);
                if (!success) {
                    System.out.println("file not renamed");
                    return badRequest("Error in File Upload");
                }

            } catch (Exception io) {

                fileChecker.delete();
                Logger.error(io.getMessage());
                io.printStackTrace();

                return badRequest("Error in File Upload");
            }

        }
        return ok();

    }


    public Result view() {
        return ok();

    }

    public Result updateUser() {
        try {
            JSONObject jsonObject = new JSONObject(request().body().asJson().toString());


            Logger.info("jsonObject: " + jsonObject.getLong("employeeMasterId"));

            Logger.info("jsonObject: " + jsonObject.getLong("employeeDetailId"));

            Roles roles1=new Roles();
            Designation designation1=new Designation();
            EmployementStatus employementStatus=new EmployementStatus();
            Department department1=new Department();

            EmployeeMasters employeeMasters = new EmployeeMasters();
            employeeMasters.setId(jsonObject.getLong("employeeMasterId"));
            employeeMasters.setFull_name(jsonObject.getString("fullName").trim());
            employeeMasters.setMarital_status(jsonObject.getString("maritalstatus"));
            employeeMasters.setFather_name(jsonObject.getString("fatherName"));
            employeeMasters.setMother_name(jsonObject.getString("motherName"));
            employeeMasters.setNationality_code(jsonObject.getString("nationality"));
            employeeMasters.setEmail_id_personal(jsonObject.getString("emailIdPersonal"));
            employeeMasters.setAddress(jsonObject.getString("address"));
            employeeMasters.setIce(jsonObject.getString("ice"));
            employeeMasters.setRelation(jsonObject.getString("relation"));
            employeeMasters.setMobile(jsonObject.getString("mobileNumber"));
            employeeMasters.setPtcl(jsonObject.getString("ptclNumber"));
            employeeMasters.setTimezone(jsonObject.getString("timezone"));




            LoginUser user = LoginUser.find.where().eq("employee_id",jsonObject.getLong("employeeMasterId")).findUnique();
            String email = session().get("pa.u.id");
            LoginUser loginUser1 = LoginUser.find.where().eq("email",email).findUnique();
            String sqlTime = "SELECT now()";

            SqlQuery sqlQuery = Ebean.createSqlQuery(sqlTime);

            SqlRow row = sqlQuery.findUnique();


            LoginUser loginUser=new LoginUser();
            loginUser.setId(user.getId());
            loginUser.setPassword(Crypto.encrypt(jsonObject.getString("password"), Constants.SECRET_KEY));
            loginUser.setLast_update(row.getTimestamp("now"));
            loginUser.setUpdatebyId(Long.parseLong(String.valueOf(loginUser1.getEmployeeId())));

            EmployeeDetails employeeDetails = new EmployeeDetails();
            employeeDetails.setId(jsonObject.getLong("employeeDetailId"));
            employeeDetails.setEmployee_id(employeeMasters.getId());
            employeeDetails.setRole_id(Long.parseLong(jsonObject.getString("role")));
            employeeDetails.setRole_code(jsonObject.getString("rolecode"));
            employeeDetails.setDesignation_id(Long.parseLong(jsonObject.getString("designation")));
            employeeDetails.setDesignationCode(jsonObject.getString("designationcode"));
            employeeDetails.setShift_id(Long.parseLong(jsonObject.getString("shift")));
            employeeDetails.setShift_code(jsonObject.getString("shiftcode"));
            employeeDetails.setEmploymentStatusId(Long.parseLong(jsonObject.getString("employemntstatus")));
            employeeDetails.setEmploymentStatusCode(jsonObject.getString("employementcode"));
            employeeDetails.setDepartment_code(jsonObject.getString("departmentcode"));
            employeeDetails.setDepartment_id(Long.parseLong(jsonObject.getString("department")));//how to set long value
            employeeDetails.setIncharge_id(Long.parseLong(jsonObject.getString("incharge")));
            employeeDetails.setInchargeName(jsonObject.getString("iid"));
            employeeDetails.setEmploymentStatusId(Long.parseLong(jsonObject.getString("employemntstatus")));
            employeeDetails.setEmploymentStatusCode(jsonObject.getString("employementcode"));
            if(jsonObject.getString("employementcode").equals("INACTIVE")||jsonObject.getString("employementcode").equals("RESIGNED")){
                employeeMasters.setActive(false);
            }
            else{
                employeeMasters.setActive(true);
            }



            Ebean.update(employeeMasters);
            Ebean.update(loginUser);
            Ebean.update(employeeDetails);

        // sendEmail(employeeDetails.getEmployee_id());
        } catch(Exception e) {
            Logger.error("", e);
        }
        return ok("");
    }

    public Result autoFill(String name) {
        List<EmployeeMasters> employeeMastersList = null;

        try {
            employeeMastersList = Ebean.find(EmployeeMasters.class).where().like("full_name", name).findList();
        } catch(Exception e) {
            Logger.error("", e);
        }

        return ok(Json.toJson(employeeMastersList));
    }

    public Result getFile(String file){
               Logger.info(file);
        return ok(new File ("./baseline//"+ file));
    }





    public Result getProfileInfo(String empId){
        Shift shiftDetails =null;
        EmployeeMasters employeeMasters=null;
        EmployeeDetails employeeDetails=null;
        JSONObject jsonObj= new JSONObject();

        try{

            employeeMasters=EmployeeMasters.find.byId(Long.valueOf(empId));
            jsonObj.put("empMaster",Json.toJson(employeeMasters));

            employeeDetails = EmployeeDetails.find.where().eq("employee_id",Long.valueOf(empId)).findUnique();

            EmployeeInformations employeeInfo= new EmployeeInformations();
            employeeInfo.setDepartment_code(employeeDetails.getDepartment_code());
            employeeInfo.setDesignationCode(employeeDetails.getDesignationCode());
            employeeInfo.setInchargeName(employeeDetails.getInchargeName());
            shiftDetails = Shift.find.where().eq("shift_code", employeeDetails.getShift_code()).findUnique();


            jsonObj.put("empDetail",Json.toJson(employeeInfo));
            jsonObj.put("shiftDetail",Json.toJson(shiftDetails));

        }catch(Exception ex){
            Logger.error(ex.getMessage());
            ex.printStackTrace();
        }

        return ok(jsonObj.toString());
    }
    public Result getUserName(String empId) {
        JSONObject obj = new JSONObject();
        try {
            EmployeeMasters employeeMasters = EmployeeMasters.find.where().eq("id", Integer.parseInt(empId)).findUnique();

            obj.put("Name",employeeMasters.getFull_name());
        } catch(Exception e) {
            Logger.error("", e);
        }

        return ok(obj.toString());
    }

    public Result sendEmail(Long empId){


        EmployeeDetails employeeDetails = EmployeeDetails.find.where().eq("employee_id",empId).findUnique();
        String email = session().get("pa.u.id");

        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        LoginUser loginUser1 =LoginUser.find.where().eq("employee_id",empId).findUnique();
        // LoginUser loginUser = LoginUser.find.where().eq("employee_id",employeeDetails.getIncharge_id()).findUnique();
        EmployeeMasters employeeMasters = EmployeeMasters.find.where().eq("id",loginUser.getEmployeeId()).findUnique();
        EmployeeMasters employeeMasters1= EmployeeMasters.find.where().eq("id",employeeDetails.getEmployee_id()).findUnique();
//        EmployeeMasters employee = EmployeeMasters.find.where().eq("id",parseInt(empId)).findUnique();

        final String username = "support@conurets.com";
        final String password = "koolkat4@";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "secure.emailsrvr.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("support@conurets.com")); // same email id
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("shujaat.khan@conurets.com,saad.ilyas@conurets.com,mariam.khan@conurets.com"));// whome u have to send mails that person id
            message.setSubject("New User Created");
            message.setText("Dear All,"+"\r\rA new User is created with the following details:"+"\r\r"+"Name : " +employeeMasters1.getFull_name()+"\r\rCNIC :"+employeeMasters1.getCnic()
                    +"\r\r"+"Nationality : " +employeeMasters1.getNationality_code()+"\r\r"+"Gender : " +employeeMasters1.getGender()+"\r\r"+"Martial Satus : " +employeeMasters1.getMarital_status()+"\r\r"+"Father's Name : " +employeeMasters1.getFather_name()
                    +"\r\r"+"Mother's Name : " +employeeMasters1.getMother_name()+"\r\r"+"Date Of Joining : " +employeeMasters1.getDoj()+"\r\r"+"Date of Birth : " +employeeMasters1.getDob()
                    +"\r\r"+"Mobile Number : " +employeeMasters1.getMobile()+"\r\r"+"PTCL Number : " +employeeMasters1.getPtcl()+"\r\r"+"Address : " +employeeMasters1.getAddress()
                    +"\r\r"+"Email Official ID : " +employeeMasters1.getEmail_id_official()+"\r\r"+"Password : " +loginUser1.getPassword()+"\r\r"+"Email Personal ID : " +employeeMasters1.getEmail_id_personal()+"\r\r"+"Blood Group : " +employeeMasters1.getBlood_group()+"\r\r"+"In Case Of Emergency : " +employeeMasters1.getIce()+"\r\r"+"Relation : " +employeeMasters1.getRelation()+
                    "\r\r"+"TimeZone : " +employeeMasters1.getTimezone()+"\r\r"+"Incharge Full name : " +employeeDetails.getInchargeName()+"\r\r"+"Department : " +employeeDetails.getDepartment_code()
                    +"\r\r"+"Designation : " +employeeDetails.getDesignationCode() +"\r\r"+"Shift : " +employeeDetails.getShift_code()
                    +"\r\r"+"Role : " +employeeDetails.getRole_code() +"\r\r"+"Employement Status : " +employeeDetails.getEmploymentStatusCode()+"\r\r"+"Employee ID : " +employeeDetails.getEmployee_id()+"\r\r For review visit: http://192.168.100.7:9500 ");

            Transport.send(message);

            System.out.println("Done");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("sendmail");
        return ok();

    }











}