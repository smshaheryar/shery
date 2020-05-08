/**
 * Created by S.Aiman on 10/07/2018.
 */

function changePassword()
{
    var currPass = $('#current_password').val();
    var newPass = $('#new_password').val();
    var confPass = $('#confirm_password').val();

    if(currPass=="" || newPass=="" || confPass=="")
    {
        alert("Enter all required fields");
    }
else {
        if (newPass.length < 4) {
            alert("Password length should not be less than 4 characters");
        }
        else {
            if (newPass != confPass) {
                alert("Password doesn't match.Re-enter password to Confirm");
                $('#confirm_password').val("");
                $('#current_password').val(currPass);
                $('#new_password').val(newPass);
            }
            else {

                $.ajax({
                    type: "GET",
                    url: "/data/changePassword/" + encodeURIComponent(currPass) + "/" + encodeURIComponent(newPass)+"/" +localStorage.getItem("empId"),
                    success: function (rsp) {

                        try {
                            var data = rsp;
                            if (data == "success") {
                                emailOnPasswordChange();
                                alert("Password is changed");

                            }
                            else {
                                alert(data);
                            }

                        } catch (err) {
                            console.log(err);
                        }
                    }
                });

            }
        }
    }

}


function emailOnPasswordChange()
{
    $.ajax({
        type: "GET",
        url: "/data/emailOnChangePassword/"+localStorage.getItem("empId"),
        success: function (rsp) {

            try {
                Console.log("email sent to me");
            } catch (err) {
                console.log(err);
            }
        }
    });




}
