/**
 * Created by Shaheryar Nadeem on 7/24/2018.
 */

let employeeMasterId = 0;
let employeeDetailId =0;
let ld=0;
var passwordoriginal;


$('#select2, #search,#employeeid,#tt,#select2title,#select2box').hide();
$(":input").inputmask();
function call() {

    var x = document.getElementById("password");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}




function empty() {
    /*
     alert("sdasd")
     */
    $('#fullname').val("");
    $('#cnic1').val("");
    $('#fathersname').val("");
    $('#mothername').val("");
    $('#gender').val("");
    $('#nationality1').val("");
    $('#dateofjoining').val("");
    $('#dateofbirth').val("");
    $('#mobilenumber').val("");
    $('#ptclnumber').val("");
    $('#address12').val("");
    $('#bloodgroup').val("");
    $('#emailofficial').val("");
    /*   document.getElementById("emailofficial").disabled = true;*/
    $('#emailpersonal').val("");
    $('#Gender').val("");
    $('#maritalstatus').val("");
    $('#designation1').val("");
    $('#department1').val("");
    $('#role1').val("");
    $('#incharge1').val("");
    $('#password').val("");
    $('#relation').val("");
    $('#ice1').val("");
    $('#employeeid').val("");
    /*  $('#employeeid').attr("disabled", true);*/
    $('#employeestatus').val("");
}

function save() {

    //  alert("adasdasd")
    var fullName = $('#fullname').val();
    var cnic = $('#cnic1').val();
    var fatherName = $('#fathersname').val();
    var motherName = $('#mothername').val();
    var dateofjoining= $('#dateofjoining').val();
    var dateofbirth = $('#dateofbirth').val();

    var designation = $('#designation').val();

    var mobileNumber =$('#mobilenumber').val();
    var ptclNumber =$('#ptclnumber').val();
    var emailIdOfficial =$('#emailofficial').val();

    var emailIdPersonal =$('#emailpersonal').val();
    var address =$('#address12').val();
    var bloodgroup = $('#bloodgroup').val();
    var ice = $('#ice1').val();
    var relation = $('#relation').val();
//roles
    var role = $('#role1').val();
    var e = document.getElementById("role1");
    var strUser = e.options[e.selectedIndex].text;
    var rolecode=strUser;

//shifts
    var shift= $('#shift1').val();
    var sc=document.getElementById("shift1");
    var shco=sc.options[sc.selectedIndex].text;
    var shiftcode =shco;
    //department
    var department =$('#department1').val();
    var a = document.getElementById("department1");
    var deptuser = a.options[a.selectedIndex].text;
    var departmentcode = deptuser;

//designation
    var designation =$('#designation1').val();
    var d=document.getElementById("designation1");
    var code=d.options[d.selectedIndex].text;
    var designationcode=code;
//employement stattus
    var employemntstatus = $('#employeestatus').val();
    var e = document.getElementById("employeestatus");
    var strUser = e.options[e.selectedIndex].text;
    var employementcode=strUser;


//incharge
    var incharge = $('#incharge1').val();
    var i =document.getElementById("incharge1");
    var iid=i.options[i.selectedIndex].text;

    var nationality=$('#nationality1').val();
    var gender=$('#Gender').val();
    var maritalstatus = $('#maritalstatus').val();


    var isChecked = $("#checkbox").is(":checked");
    var password = $('#password').val();
    var setPassword = '';

    if (employeeMasterId === 0) {
        setPassword = password;
    } else {
        if (isChecked) {
            setPassword = password;
        } else {
            setPassword = passwordoriginal;
        }
    }

    /*if(employeeMasterId!=0)
    {
        if (isChecked){
            var password = $('#password').val();
        }
        else{
         var password=passwordoriginal;
        }

    }

    else if(employeeMasterId === 0){

      var  password= $('#password').val();
    }*/


    var timezone = $('#timezone').val();
    var file = $('#fileUpload').get(0).files[0];

    var userProfileInformation = {
        fullName: fullName,
        cnic: cnic,
        fatherName: fatherName,
        motherName: motherName,
        dateofjoining:dateofjoining,
        dateofbirth:dateofbirth,
        gender:gender,
        maritalstatus:maritalstatus,
        incharge:incharge,
        mobileNumber:mobileNumber,
        ptclNumber:ptclNumber,
        emailIdOfficial:emailIdOfficial,
        emailIdPersonal:emailIdPersonal,
        address:address,
        bloodgroup:bloodgroup,
        ice:ice,
        relation:relation,
        role:role,
        rolecode:rolecode,
        department:department,
        departmentcode:departmentcode,
        employemntstatus:employemntstatus,
        employementcode:employementcode,
        designation:designation,
        designationcode:designationcode,
        iid:iid,
        nationality:nationality,
        password:setPassword,
        description:description,
        search:search,
        employeeMasterId: employeeMasterId,
        employeeDetailId:employeeDetailId,
        timezone:timezone,
        shift:shift,
        shiftcode:shiftcode
    };

    /*var formData = new FormData();
     formData.append('file', file);
     formData.append('userProfileInformation', userProfileInformation);*/

    if (employeeMasterId === 0) {
        ajaxRequest("/profile", "POST", userProfileInformation, saveEmployeeHandler)

    } else {
        ajaxRequest("/data/updateUser", "POST", userProfileInformation, saveEmployeeHandler)
    }


}

function saveEmployeeHandler(response) {
    try {
        if (response != null) {
            if (response === "") {
                //var z = document.getElementById("fileUpload1");
              //  var x =z.src.val();

                    if ($('#fileUpload')[0].files.length > 0 ) {

                        uploadFile();

                    }
                alert("User details has been updated")


            } else if (response.id !== undefined) {
                alert("User details have been inserted with this employee  "+response.id)
                employeeMasterId = response.id;
                uploadFile();
            } else {
                alert(response)
            }
        }
    } catch (error) {

    }
}

function ajaxRequest(url, type, params, callbackMethod) {
    $.ajax({
        type: type,
        url: url,
        contentType: "application/json",
        data: JSON.stringify(params),
        success: callbackMethod,
        error: function() {
            alert(error);
        }
    });
}

function ajaxImageRequest(url, type, params, callbackMethod) {
    $.ajax({
        type: type,
        url: url,
        contentType: "application/json",
        data: params,
        success: callbackMethod,
        error: function() {
            alert(error);
        }
    });
}

/*function ajaxRequest(url, type, callbackHandler) {
 $.ajax({
 type: type,
 url: url,
 contentType: "application/json",
 success: callbackHandler,
 error: function() {
 alert(error);
 }
 });
 }*/


function search() {
    var search = $('#userName').val();


    // alert(""+search);
    getinfo(" /data/findUserDetails/"+search,"GET")
}




function getinfo(url,type) {

    $.ajax({
        type: type,
        url: url,
        success:function (rsp) {
            try{
                employeeMasterId =0;
                var data= rsp;
                employeeMasterId =
                    employeeMasterId = data.employeeMaster.id;

                employeeDetailId = data.employeeDetailId;
                id=data.employeeDetailId;
                //  alert(""+id);
                $('#fullname').val(data.employeeMaster.full_name);
                $('#cnic1').val(data.employeeMaster.cnic);
                $('#cnic1').attr("disabled", true);

                $('#fathersname').val(data.employeeMaster.father_name);
                $('#mothername').val(data.employeeMaster.mother_name);
                //$('#gender').val(data.employeeMaster.gender);
                $('#nationality1').val(data.employeeMaster.nationality_code);
                $('#dateofjoining').val(data.employeeMaster.doj);
                $('#dateofjoining').attr("disabled", true);
                $('#dateofbirth').val(data.employeeMaster.dob);
                $('#dateofbirth').attr("disabled", true);
                $('#mobilenumber').val(data.employeeMaster.mobile);
                $('#ptclnumber').val(data.employeeMaster.ptcl);
                // $('#ptcl').val(data.gender);
                $('#address12').val(data.employeeMaster.address);
                $('#bloodgroup').val(data.employeeMaster.blood_group);
                $('#bloodgroup').attr("disabled", true);
                $('#relation').val(data.employeeMaster.relation);
                $('#emailofficial').val(data.employeeMaster.email_id_official);
                document.getElementById("emailofficial").disabled = true;
                $('#emailpersonal').val(data.employeeMaster.email_id_personal);
                $('#Gender').val(data.employeeMaster.gender);
                $('#Gender').attr("disabled", true);
                $('#maritalstatus').val(data.employeeMaster.marital_status);
                $('#designation1').val(data.designation_id);
                $('#department1').val(data.department_id);
                $('#role1').val(data.role_id);
                $('#incharge1').val(data.incharge_id);
                passwordoriginal=data.password;
                $('#password').val(passwordoriginal);
                $('#relation').val(data.employeeMaster.relation);
                $('#ice1').val(data.employeeMaster.ice);
                $('#employeeid').val(data.employee_id);
                $('#employeeid').attr("disabled", true);
                $('#employeestatus').val(data.employeeStatusId);
                $('#timezone').val(data.employeeMaster.timezone);

                $('#shift1').val(data.shift_id);


                var z = document.getElementById("fileUpload1");
                z.src = "/userprofile/"+data.employeeMaster.image_path;

                //http://localhost:9000/userprofile/shujaat_khan.png
                //readimage("/userprofile/"+data.employeeMaster.image_path, "GET")

                /*var z=document.getElementById("fileUpload1");
                 z.src=""+data.imagePath;*/
                //$('#fileUpload1').append(data.imagePath);

                // $('#incharge').val(data.inchargeName).trigger('change');
                //$('#Gender').val(data.gender).replaceWith(value[0]);

                console.log(data)
            }catch(err){
                console.log(err)
            }



        }
    })
}

function readimage(url, type) {
    $.ajax({
        type: type,
        url: url,

        success: function (rsp) {
            try {
                var data= rsp;
                var z=document.getElementById("fileUpload1");
                z.src=""+url;
            } catch (err) {
                console.log(err)
            }


        }
    });
}



function edit() {


    $('#select2, #search,#employeeid,#tt,#select2title,#select2box').show();
    let searchvalue = $('#userName').val();
    $('#emailofficial').val("");
    $('#password').val("");
    $("#checkbox").prop("disabled", false);
    //alert("ddsf");

  /*  if (searchvalue.length > 4) {
        ajaxRequest("/data/autofill/"+searchvalue, "GET", searchByNameHandler);
    } else {
        //$('#searchResults').hide();
    }*/
}



$(function(){
    // turn the element to select2 select style
    $('.select2').select2({
        placeholder: "Select a state"
    });
    $("#select2").prop("disabled", false);
    $("#select2").prop("disabled", false);

    var data = $(".select2 option:selected").text();
    $("#test").val(data);
});





function uploadFile() {
    try {

        var file = $('#fileUpload').get(0).files[0];

        console.log("file:" + file);

        //if(formData.has('file')){

        if(typeof file !='undefined'){
            dataSheetName = file.name;
            var formData = new FormData();
            formData.append('file', file);
            formData.append('employeeMasterId', employeeMasterId);

            globalData = [];

            $.ajax({
                url: '/upload',
                data: formData,
                type: 'POST',
                contentType: false,
                processData: false,
                success: function (rsp) {
                    globalData = JSON.parse(rsp);
                    console.log(globalData);


                },
                error: function (err){
                    alert(err.responseText);
                }
            });
        }else{
            alert("Please Select File To Upload!");
        }
    } catch(error) {
        alert(error);
    }

    return false;
}


