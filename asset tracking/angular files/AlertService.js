
app.service('AlertService', ['$rootScope', '$http','AssetsInfoService', function($rootScope, $http,AssetsInfoService)
{
    var clientDAO;
    return{
        initializeAlertData : function(fillOption, _clientDAO)
        {
            clientDAO = _clientDAO;
            this.fillAlertsData(fillOption);
        },
        fillAlertsData : function(fillOption, alertCode)
        {
            var _AlertService = this;
            var searchCriteriaDTO = {
                "clientCode" : (clientDAO === null || clientDAO === undefined || clientDAO === "") ? -1 : clientDAO.clientCode,
                "alertCode" : (alertCode && Number(alertCode) > NUMBER_ZERO) ? alertCode : -1
            };

            $http({
                method: 'POST',
                url: 'fillAllAlerts',
                dataType: 'json',
                data:JSON.stringify(searchCriteriaDTO)
            }).then(function (response)
            {
                if (fillOption === 'alertsTbl')
                {
                    _AlertService.fillAlertsDataTbl(response.data);
                }
                else if (fillOption === 'alert-details')
                {
                    _AlertService.fillAlertDetail(response.data);
                }
            }, function(error)
            {
                console.log("ERROR: " + error);
            });
        },
        fillAlertsDataTbl : function (alertDAOsList)
        {
            let alertsTbl = $('#alertsTbl').DataTable();
            alertsTbl.clear().draw();

            $("#alertsTblDataCount").text("");
            if (alertDAOsList && alertDAOsList.length > NUMBER_ZERO)
            {
                let _AlertService = this;

                $("#alertsTblDataCount").text(alertDAOsList.length);
                let myRows = [];
                for (let i = NUMBER_ZERO; i < alertDAOsList.length; i++)
                {
                    let alertDAO = alertDAOsList[i];
                    let assetDAO = alertDAO.assetDAO;
                    let userInfoDAO = alertDAO.userInfoDAO;

                    let myColumn = [];

                    let formattedDate = moment(alertDAO.time).format(DATE_FORMAT);
                    let formattedTime = moment(alertDAO.time).format(TIME_FORMAT);

                    myColumn.push(formattedDate + "<br/>" + formattedTime);
                    myColumn.push(alertDAO.alertCode);
                    myColumn.push(assetDAO.assetCode + " / [VIN: " + assetDAO.vin + "]");

                    let _userColumnData = '';
                    if (userInfoDAO)
                    {
                        _userColumnData = userInfoDAO.userCode + " - " + userInfoDAO.fullName + "<br/>Contact: " + userInfoDAO.contactNo;
                    }
                    myColumn.push(_userColumnData);
                    myColumn.push(Number(alertDAO.latitude).toFixed(NUMBER_SIX) + ", " + Number(alertDAO.longitude).toFixed(NUMBER_SIX));
                    myColumn.push(alertDAO.distance);
                    myColumn.push(alertDAO.duration);

                    myRows.push(myColumn);
                }
                alertsTbl.rows.add(myRows).draw();

                $("#alertsTbl").on("click", "tr", function ()
                {
                    let table = $('#alertsTbl').DataTable();
                    let data = table.row(this).data();
                    let alertCode = data[NUMBER_ONE];

                    _AlertService.fillAlertsData('alert-details', alertCode);
                });
            }
        },
        fillAlertDetail : function(alertDAOs)
        {
            if (alertDAOs && alertDAOs.length === NUMBER_ONE)
            {
                let alertDAO = alertDAOs[NUMBER_ZERO];
                let assetDAO = alertDAO.assetDAO;
                let userInfoDAO = alertDAO.userInfoDAO;
                let fenceDAO = alertDAO.fenceDAO;
                let assetLocationDAO = alertDAO.assetLocationDAO;

                $("#alertTimeTxt").val(moment(alertDAO.time).format(DATE_TIME_FORMAT));
                $("#alertCodeTxt").val(alertDAO.alertCode);
                $("#alertTypeTxt").val(alertDAO.alertType);

                $("#fenceCodeNameTxt").val(fenceDAO.fenceCode + " - " + fenceDAO.fenceName);
                $("#assetCodeVinTxt").val(assetDAO.assetCode + " - " + assetDAO.vin);
                $("#salesAgentTxt").val(alertDAO.userCode);

                $("#latitudeTxt").val(alertDAO.latitude);
                $("#longitudeTxt").val(alertDAO.longitude);
                $("#assetLocationStatusTxt").val(assetLocationDAO.assetLocationStatus);

                $("#distanceTxt").val(alertDAO.distance);
                $("#durationTxt").val(alertDAO.duration);

                $("#alert-details-popup").modal('show');
            }
        }
    };
}]);