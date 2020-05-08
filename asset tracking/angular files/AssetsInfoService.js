
app.service('AssetsInfoService', ['$rootScope', '$http', 'UserService', 'ClientService', function($rootScope, $http, UserService, ClientService)
{
    let clientDAO;
    return{
        initializeAssetsInfoData : function(fillOption, _clientDAO)
        {
            clientDAO = _clientDAO;
            this.fillAssetsData(fillOption);
        },
        fillAssetsData : function(fillOption, _assetCode)
        {
            let _AssetsInfoService = this;
            let searchCriteriaDTO = {
                "clientCode" : (clientDAO === null || clientDAO === undefined || clientDAO === "") ? -1 : clientDAO.clientCode,
                "assetCode": (_assetCode && Number(_assetCode) > NUMBER_ZERO) ? _assetCode : -1
            };

            $http({
                method: 'POST',
                url: 'fillAllAssets',
                dataType: "json",
                data: JSON.stringify(searchCriteriaDTO)
            }).then(function (response)
            {
                if (fillOption === 'assetsInfoTbl')
                {
                    _AssetsInfoService.fillAssetsDataTbl(response.data);
                }
                else if (fillOption === 'assetsInfoPopup')
                {
                    _AssetsInfoService.fillAssetsInfoPopup(response.data);
                }
            }, function(error)
            {
                alert("ERROR: " + error);
            });
        },
        resetAssetInfoPopup : function(assetDAO)
        {
            $("#assetTimeTxt").val('');
            $("#assetCodeTxt").val('');
            $("#fenceCodeAndNameDD").val('-1');
            $rootScope.fenceCode = '-1';
            $("#assetVinTxt").val('');
            $("#assetModelTxt").val('');
            $("#assetMakeTxt").val('');
            $("#assetLatitudeTxt").val('');
            $("#assetLongitudeTxt").val('');

            ClientService.viewFenceInfo();

            let toggleEditAssetInfoBtn = !(assetDAO && Number(assetDAO.assetCode) > NUMBER_ZERO);
            let toggleAddAssetInfoBtn = !toggleEditAssetInfoBtn;

            $("#editAssetInfoBtn").prop("disabled", toggleEditAssetInfoBtn);
            $("#addAssetInfoBtn").prop("disabled", toggleAddAssetInfoBtn);
        },
        fillAssetsInfoPopup : function(assetDAOsList)
        {
            if (assetDAOsList && assetDAOsList.length === NUMBER_ONE)
            {
                //$("#editAssetInfoBtn").prop("disabled", false);
                let assetDAO = assetDAOsList[NUMBER_ZERO];

                this.resetAssetInfoPopup(assetDAO);

                $("#assetTimeTxt").val(assetDAO.time);
                $("#assetCodeTxt").val(assetDAO.assetCode);
                $("#fenceCodeAndNameDD").val(assetDAO.fenceCode);
                $rootScope.fenceCode = assetDAO.fenceCode;

                $("#assetVinTxt").val(assetDAO.vin);
                $("#assetModelTxt").val(assetDAO.model);
                $("#assetMakeTxt").val(assetDAO.make);
                $("#assetLatitudeTxt").val(assetDAO.assetLocationDAOsList[0].latitude);
                $("#assetLongitudeTxt").val(assetDAO.assetLocationDAOsList[0].longitude);

                $("#assets-info-popup").modal('show');
            }
        },
        fillAssetsDataTbl : function(assetDAOsList)
        {
            let assetsInfoTbl = $('#assetsInfoTbl').DataTable();
            assetsInfoTbl.clear().draw();

            if (assetDAOsList && assetDAOsList.length > NUMBER_ZERO)
            {
                let myRows = [];
                for (let i = NUMBER_ZERO; i < assetDAOsList.length; i++)
                {
                    let assetDAO = assetDAOsList[i];
                    let fenceDAO = assetDAO.fenceDAO;
                    let myColumn = [];

                    let formattedDate = moment(assetDAO.time).format(DATE_FORMAT);
                    let formattedTime = moment(assetDAO.time).format(TIME_FORMAT);

                    myColumn.push(formattedDate + "<br/>" + formattedTime);
                    myColumn.push(assetDAO.assetCode);
                    myColumn.push(assetDAO.vin);
                    myColumn.push(assetDAO.make);
                    myColumn.push(assetDAO.model);
                    myColumn.push(fenceDAO.fenceCode + " - " + fenceDAO.fenceName);
                    myColumn.push(assetDAO.assetLocationStatus);

                    myRows.push(myColumn);
                }
                assetsInfoTbl.rows.add(myRows).draw();

                let _AssetsInfoService = this;
                $('#assetsInfoTbl').on("click", "tr", function()
                {
                    //ClientService.viewFenceInfo('assetsInfoPopup');
                    let table = $('#assetsInfoTbl').DataTable();
                    let data = table.row(this).data();
                    let assetCode = data[NUMBER_ONE];

                    _AssetsInfoService.fillAssetsData('assetsInfoPopup', assetCode);
                });
            }
        },
        validateAssetDAO : function()
        {
            let assetTimeTxt = $("#assetTimeTxt").val();
            let assetCodeTxt = $("#assetCodeTxt").val();
            let fenceCodeVal = $("#fenceCodeAndNameDD").val();
            let assetMakeTxt = $("#assetMakeTxt").val();
            let assetModelTxt = $("#assetModelTxt").val();
            let assetVinTxt = $("#assetVinTxt").val();
            let assetLatitudeTxt = parseFloat($("#assetLatitudeTxt").val());
            let assetLongitudeTxt = parseFloat($("#assetLongitudeTxt").val());

            let assetLocationDAOsList = [{
                latitude: assetLatitudeTxt,
                longitude: assetLongitudeTxt,
            }];

            let isDataValidated = true;
            if (Number(fenceCodeVal) <= NUMBER_ZERO)
            {
                isDataValidated = false;
                alert("Please Select Fence");
                $("#fenceCodeAndNameDD").focus();
                return;
            }
            else if (assetMakeTxt.trim().length === NUMBER_ZERO)
            {
                isDataValidated = false;
                alert("Please enter Make");
                $("#assetMakeTxt").focus();
                return;
            }
            else if (assetModelTxt.trim().length === NUMBER_ZERO)
            {
                isDataValidated = false;
                alert("Please enter Model");
                $("#assetModelTxt").focus();
                return;
            }
            else if (assetVinTxt.trim().length === NUMBER_ZERO)
            {
                isDataValidated = false;
                alert("Please enter VIN");
                $("#assetVinTxt").focus();
                return;
            }
            else if (!validateLatitude(assetLatitudeTxt))
            {
                isDataValidated = false;
                alert("Please enter Valid Latitude");
                $("#assetLatitudeTxt").focus();
                return;
            }
            else if (!validateLongitude(assetLatitudeTxt))
            {
                isDataValidated = false;
                alert("Please enter Valid Longitude");
                $("#assetLongitudeTxt").focus();
                return;
            }

            if (isDataValidated)
            {
                let clientDAO = ClientService.getClientInfoDAO();
                let userInfoDAO = UserService.getUserInfoDAO();

                let assetDAO = {
                    time: assetTimeTxt,
                    assetCode: assetCodeTxt,
                    clientCode: clientDAO.clientCode,
                    fenceCode: fenceCodeVal,
                    isActive: "True",
                    userCode: userInfoDAO.userCode,
                    make: assetMakeTxt,
                    model: assetModelTxt,
                    vin: assetVinTxt,
                    userInfoDAO: userInfoDAO,
                    assetLocationDAOsList: assetLocationDAOsList
                };
                return assetDAO;
            }
            return isDataValidated;
        },
        saveAssetDAO : function()
        {
            let assetDAO = this.validateAssetDAO();
            if (assetDAO)
            {
                let _AssetsInfoService = this;
                $http({
                    method: 'POST',
                    url: 'saveAssetDAO',
                    data: JSON.stringify(assetDAO),
                }).then(function(response)
                {
                    _AssetsInfoService.callbackSaveAssetDAO(response.data);
                }, function (error)
                {
                    alert("ERROR IN ADDING ASSET INFO. " + error);
                });
            }
        },
        editAssetDAO : function()
        {
            let assetDAO = this.validateAssetDAO();
            if (assetDAO)
            {
                let _FenceInfoService = this;
                $http({
                    method: 'POST',
                    url: 'editAssetDAO',
                    data : JSON.stringify(assetDAO),
                }).then(function (response)
                {
                    _FenceInfoService.callbackEditAssetDAO(response.data);
                }, function (error)
                {
                    console.log("ERROR IN EDIT ASSET INFO. " + error);
                });
            }
        },
        callbackEditAssetDAO : function(assetDAO)
        {
            if (assetDAO && assetDAO.assetCode > NUMBER_ZERO)
            {
                $("#alertMessageBody").html("Asset Code " + assetDAO.assetCode +" has been updated.");
                $("#alert-modal-popup").modal('show');
                $('#assets-info-popup').modal('hide');
                $('#menu-close').click();
                this.fillAssetsData('assetsInfoTbl');
            }
        },
        callbackSaveAssetDAO : function(assetDAO)
        {
            if (assetDAO && assetDAO.assetCode > NUMBER_ZERO)
            {
                $("#alertMessageBody").html("Asset has been added & assigned to Fence. Asset Code is: '" + assetDAO.assetCode  + "'");
                $("#alert-modal-popup").modal('show');
                $('#assets-info-popup').modal('hide');
                $('#menu-close').click();
                this.fillAssetsData('assetsInfoTbl');
            }
        }
    };
}]);