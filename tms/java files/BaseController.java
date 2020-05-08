
package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.Unrestricted;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.sun.org.apache.xpath.internal.operations.Bool;
import entities.*;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Predef;


import java.util.*;
import java.util.stream.Collectors;


public class BaseController extends Controller {


    public Result getUserName(String email)
    {
        LoginUser userExists = Ebean.find(LoginUser.class).where()
                .eq("email", email)
                .findUnique();
        //   String Uname= userExists.getName();
        return ok(email);

    }


/*
        public Result applyForLeave()
        {        String email = session().get("pa.u.id");
                LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
                return ok(views.html.applyForLeave.render(loginUser));
        }*/

    public Result viewCalender()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();

        return ok(views.html.viewCalender.render(loginUser,role.getRole()));

    }
    @Restrict({@Group("Administrator")} )
    public Result dashboard()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();


        return ok(views.html.dashboard.render(loginUser,role.getRole()));
    }


    public Result changePassword()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();

        return ok(views.html.changePassword.render(loginUser,role.getRole()));
    }

    public Result leavePolicy()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.leavePolicy.render(loginUser,role.getRole()));
    }
    @Restrict({@Group("Administrator")} )
    public Result EditleavePolicy()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.EditleavePolicy.render(loginUser,role.getRole()));
    }

    // @Restrict(@Group(""))
    @Restrict({@Group("Administrator")} )
    public Result Profile()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        //EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        //Roles role = Ebean.find(Roles.class).where().eq("role_coe", employeeDetails.getRole_id()).findUnique();
        List<Roles> role = Roles.find.orderBy("role_coe asc ").findList();
        List<Department> departments = Department.find.orderBy("description asc ").findList();
        List<Designation> designations = Designation.find.orderBy("designation asc ").findList();
        List<Shift> shifts=Shift.find.orderBy("shift_code asc ").findList();
        List<EmployementStatus> employementStatus =EmployementStatus.find.orderBy("status_code asc ").findList();

        //Set<EmployeeMasters> inchargeList = new HashSet<EmployeeMasters>();

        //  List<EmployeeDetails> employeeDetailsList=Ebean.find(EmployeeDetails.class).where().equals("role_code").and("Administrator","incharge").findList();
        List<EmployeeDetails> employeeDetailsList=Ebean.find(EmployeeDetails.class).where().or(com.avaje.ebean.Expr.eq("role_code","Administrator"),com.avaje.ebean.Expr.eq("role_code","Incharge")).setOrderBy("incharge_fullname ASC ").findList();

        Logger.info("employeeDetailsList: "+ employeeDetailsList.size());

        String strIds = "";
        List<Long> strIdList = new ArrayList<>();

        if (employeeDetailsList != null && employeeDetailsList.size() > 0) {
            for(int i=0;i<employeeDetailsList.size();i++){
                strIdList.add(employeeDetailsList.get(i).getEmployee_id());
            }
        }

        List<EmployeeMasters> inchargeList = null;

        if (strIdList != null && strIdList.size() > 0) {
            inchargeList = Ebean.find(EmployeeMasters.class).where().in("id", strIdList).setOrderBy("full_name asc").findList();
        } else {
            inchargeList = new ArrayList<>();
        }
        // Logger.info("sa")
        List<EmployeeDetails> employeeDetailsList12=Ebean.find(EmployeeDetails.class).where().findList();

        //   inchargeList.stream().sorted().forEach( e -> System.out.println("sorted name " + e.getFull_name()));
        // Set<EmployeeMasters> sortedSEt = (Set<EmployeeMasters>) inchargeList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toSet());
        EmployeeMasters employeeMasters=new EmployeeMasters();
        List<EmployeeMasters> employeeMastersList= Ebean.find(EmployeeMasters.class).orderBy("full_name asc").findList();
     //   List<EmployeeMasters> employeeMastersList= Ebean.find(EmployeeMasters.class).where().eq("is_active",Boolean.TRUE).orderBy("full_name asc").findList();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role2 = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();

        return ok(views.html.Profile.render(loginUser,role,role2.getRole(),departments,designations,employeeDetailsList,employementStatus,employeeMastersList,inchargeList,shifts));
    }
    @Restrict({@Group("Administrator")} )
    public Result addProject()
    {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.addProject.render(loginUser,role.getRole()));
    }
    @Restrict({@Group("Administrator"),@Group("Incharge")} )
    public Result leaveApproval() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.leaveApproval.render(loginUser,role.getRole()));
    }
    @Restrict({@Group("Administrator"),@Group("Incharge")} )
    public Result shiftScheduling() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.shiftScheduling.render(loginUser,role.getRole()));
    }
    @Restrict({@Group("Administrator"),@Group("Incharge")} )
    public Result timeAdjustment() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.timeAdjustment.render(loginUser,role.getRole()));
    }

    @Restrict({@Group("Administrator")} )
    public Result updateCalender() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.updateCalender.render(loginUser,role.getRole()));
    }
    @Restrict({@Group("Administrator"),@Group("Incharge")} )
    public Result inOutTimingsEmp() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.inOutTimingsEmp.render(loginUser,role.getRole()));
    }
}