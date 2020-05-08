package controllers;

import auth.UserProvider;
import com.avaje.ebean.Ebean;
import com.feth.play.module.pa.PlayAuthenticate;
import dal.UserRoleAccountDal;
import entities.EmployeeDetails;
import entities.LoginUser;
import entities.Project;
import entities.Roles;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import utils.Constants;
import utils.Crypto;
import views.html.login.login;

import javax.inject.Inject;
import java.util.List;

public class LoginController extends Controller{

    private final PlayAuthenticate auth;

    private final MyUsernamePasswordAuthProvider provider;
    private final UserProvider userProvider;

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";


    @Inject
    public LoginController(final PlayAuthenticate auth, final MyUsernamePasswordAuthProvider provider,
                            final UserProvider userProvider) {
        this.auth = auth;
        this.provider = provider;
        this.userProvider = userProvider;
    }

    public Result login() {
        return ok(login.render(this.auth, this.userProvider,  this.provider.getLoginForm()));
    }




    public Result employeeView() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        return ok(views.html.employee.render(loginUser));
    }
 /*   @Restrict({@Group("Admin")} )*/
    public Result inchargeView() {
        String email = session().get("pa.u.id");
   LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();
        //EmployeeDetails employeeDetails=loginUser.getEmployeeId();




        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        return ok(views.html.incharge.render(loginUser,role.getRole()));
    }

    public Result signInView() {
        String email = session().get("pa.u.id");
        LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
       // String role = Roles.find.where().eq("email", email).findUnique().getRole();
        // GET Employee Master Object by EMployee Id ... loginUser.getEmpolyeeId
        // GET ROLE_ID from Employee Master object...

       EmployeeDetails employeeDetails=Ebean.find(EmployeeDetails.class).where().eq("employee_id", loginUser.getEmployeeId()).findUnique();
        //EmployeeDetails employeeDetails=loginUser.getEmployeeId();

        Roles role = Ebean.find(Roles.class).where().eq("id", employeeDetails.getRole_id()).findUnique();
        List<Project> projectList =Ebean.find(Project.class).findList();

        return ok(views.html.signIn.render(loginUser,role.getRole()));
    }


public Result example(){
    String email = session().get("pa.u.id");
    LoginUser loginUser = LoginUser.find.where().eq("email", email).findUnique();
        return ok(views.html.example.render(loginUser));
}

    public Result doLogin() {

        com.feth.play.module.pa.controllers.Authenticate.noCache(response());

        final Form<MyUsernamePasswordAuthProvider.MyLogin> filledForm = this.provider.getLoginForm()
                .bindFromRequest();

        if (filledForm.hasErrors()) {

            // LoginUser did not fill everything properly
            return badRequest(login.render(this.auth, this.userProvider, filledForm));

        } else {
            String email = filledForm.get().getEmail();
            String password = filledForm.get().getPassword();
        /*    String id=*/


            // Everything was filled
            Result result =  this.provider.handleLogin(ctx());

            LoginUser userExists = Ebean.find(LoginUser.class).where()
                    .eq("email", email)
                    .eq("password", Crypto.encrypt(password, Constants.SECRET_KEY))
                    .findUnique();


            if (UserRoleAccountDal.getInstance().userExists == null) {

                return redirect(this.auth.getResolver().login());
                //return badRequest(login.render(this.auth, this.userProvider, filledForm));
            }

            EmployeeDetails employeeDetails =Ebean.find(EmployeeDetails.class).where().eq("employee_id",userExists.getEmployeeId().intValue()).findUnique();


            if((userExists != null) &&  ((employeeDetails.getRole_code().equalsIgnoreCase("administrator"))||(employeeDetails.getRole_code().equalsIgnoreCase("Admin")))) {
                if((!employeeDetails.getEmploymentStatusCode().equalsIgnoreCase("INACTIVE"))&&(!employeeDetails.getEmploymentStatusCode().equalsIgnoreCase("RESIGNED"))) {
                    //return redirect(routes.LoginController.inchargeView());

                    return redirect(routes.InOutTimingsController.inOutDetails());
            }
            }

            else if (((userExists != null) && ((employeeDetails.getRole_code().equalsIgnoreCase("Employee")))))
            {
                if((!employeeDetails.getEmploymentStatusCode().equalsIgnoreCase("INACTIVE"))&&(!employeeDetails.getEmploymentStatusCode().equalsIgnoreCase("RESIGNED"))) {
                    return redirect(routes.InOutTimingsController.inOutDetails());
                }
            }
            else if (((userExists != null) && ( (employeeDetails.getRole_code().equalsIgnoreCase("incharge")))))
            {
                if((!employeeDetails.getEmploymentStatusCode().equalsIgnoreCase("INACTIVE"))&&(!employeeDetails.getEmploymentStatusCode().equalsIgnoreCase("RESIGNED"))) {
                    return redirect(routes.InOutTimingsController.inOutDetails());
                }
            }
            else if (((userExists != null) && ( ( employeeDetails.getRole_code().equals("Developer")))))
            {
                return redirect(routes.InOutTimingsController.inOutDetails());
            }
          /*      String roleCode= employeeDetails.getRole_code();
            if(userExists != null){
                return redirect(String.valueOf(views.html.signIn.render(roleCode)));
            }
*/

            if(unauthorized().status() == 401){
              //  return ok("alert");
             /*   return ok(login.);*/
            }

            //Http.Context.current().flash().put("error","User is in-active.");
            ctx().flash().put("error","User is in-active.");
            return redirect(this.auth.getResolver().login());

        }

    }



}
