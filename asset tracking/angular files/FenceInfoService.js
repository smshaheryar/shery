
app.service('FenceInfoService', ['$rootScope', '$http', 'ClientService', 'UserService', 'MapService', function($rootScope, $http, ClientService, UserService, MapService)
{
    return{
        initializeFenceInfo : function()
        {
            this.resetFenceDataPoints();
        },
        populateFenceDAOs : function(fillOption, fenceCodes)
        {
            if (fenceCodes)
            {
                var _FenceInfoService = this;

                var searchCriteriaDTO = {
                    fenceCodes: fenceCodes
                };

                $http({
                    method: 'POST',
                    url: 'fetchFenceDAOs',
                    dataType: 'json',
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(searchCriteriaDTO),
                }).then(function (response)
                    {
                        var fenceDAOsList = response.data;

                        if (fillOption == 'plotFence')
                        {
                            _FenceInfoService.plotFence(fenceDAOsList);
                        }
                        else if (fillOption == 'editFence')
                        {
                            _FenceInfoService.fillEditFenceDAOInfo(fenceDAOsList);
                        }
                    },
                    function(error)
                    {
                        console.log("ERROR: " + error);
                    }
                );
            }
        },
        plotFence : function(fenceDAOsList)
        {
            if (fenceDAOsList && fenceDAOsList.length > NUMBER_ZERO)
            {
                for (var dataCounter = NUMBER_ZERO; dataCounter < fenceDAOsList.length; dataCounter++)
                {
                    var fenceDAO = fenceDAOsList[dataCounter];
                    MapService.plotFenceAndAddAssets(dataCounter, fenceDAO);
                }
                $('#viewFenceInfoPopup').modal('hide');
            }
        },
        fillEditFenceDAOInfo : function(fenceDAOsList)
        {
            if (fenceDAOsList && fenceDAOsList.length == NUMBER_ONE)
            {
                $("#viewFenceInfoPopup").modal('hide');
                $('#fence-data-info-popup').modal('show');
                //this.resetFenceDataPoints();
                var fenceDAO = fenceDAOsList[NUMBER_ZERO];
                var fenceDetailDAOs = fenceDAO.fenceDetailDAOsList;

                $("#fenceCodeTxt").val(fenceDAO.fenceCode);
                $("#fenceNameTxt").val(fenceDAO.fenceName);
                $("#fenceAddressTxt").val(fenceDAO.address);
                $('#fenceTotalDataPointsTxt').val(fenceDetailDAOs.length);
                this.createDivAndAddFenceDataPoints();
                //$('#fenceTotalDataPointsTxt').val(fenceDetailDAOs.length);

                var _elements = $('#fence-data-info-popup').find('input[cordinates="true"]');
                if (_elements != null)
                {
                    for (var i = NUMBER_ZERO, j = NUMBER_ZERO; i < fenceDetailDAOs.length; i++, j = j + NUMBER_TWO)
                    {
                        var fenceDetailDAO = fenceDetailDAOs[i];
                        var latitudeData = $(_elements[j]);
                        var longitudeData = $(_elements[j + NUMBER_ONE]);
                        latitudeData.val(fenceDetailDAO.latitude);
                        longitudeData.val(fenceDetailDAO.longitude);
                    }
                }
            }
        },
        createDivAndAddFenceDataPoints : function()
        {

            var pointValue = Number($('#innerDivAddDataPointTbl').attr('point-value'));
            var fenceTotalDataPointsTxt = Number($('#fenceTotalDataPointsTxt').val());
            fenceTotalDataPointsTxt += pointValue;

            for(var i = (pointValue + NUMBER_ONE); i <= fenceTotalDataPointsTxt; i++)
            {
                pointValue = i;
                $('#innerDivAddDataPointTbl').append(this.addDataPointTR(pointValue));
            }

            $('#innerDivAddDataPointTbl').attr('point-value', pointValue);
        },
        addDataPointTR : function(pointValue)
        {
            var dataPointTR =
                '<tr id="dataPointTr_' + pointValue + '" class="data-point-box">' +
                    '<td id="tdPointValue_' + pointValue + '" width="30" style="font-weight: bold">' + pointValue + '</td>' +
                    '<td width="160">' +
                        '<input type="text" class="form-control form-control-sm" cordinates="true" onkeypress="return isNumeric(this, event);" onpaste="return false;">' +
                    '</td>' +
                    '<td width="160">' +
                        '<input type="text" class="form-control form-control-sm" cordinates="true" onkeypress="return isNumeric(this, event);" onpaste="return false;">' +
                    '</td>' +
                    '<td width="50">' +
                        '<button id="removeDataPointBtn_' + pointValue +'" class="btn btn-danger btn-sm btn-close trash-icon"><i class="fas fa-trash-alt"></i></button>' +
                    '</td>'+
                '</tr>';

            return dataPointTR;
        },
        reGenerateDataPointsDiv : function (parentDivId, maxDivPointValue)
        {
            var divIdIndex = Number(parentDivId.split(SC_UNDER_SCORE)[NUMBER_ONE]);

            for (var newDivId = divIdIndex; newDivId < maxDivPointValue; newDivId++)
            {
                var nextDivId = newDivId + NUMBER_ONE;
                if ($('#dataPointTr' + SC_UNDER_SCORE + newDivId))
                {
                    $('#dataPointTr' + SC_UNDER_SCORE + nextDivId).attr('id', 'dataPointTr' + SC_UNDER_SCORE + newDivId);
                    $('#tdPointValue' + SC_UNDER_SCORE + nextDivId).attr('id', 'tdPointValue' + SC_UNDER_SCORE + newDivId);
                    $('#removeDataPointBtn' + SC_UNDER_SCORE + nextDivId).attr('id', 'removeDataPointBtn' + SC_UNDER_SCORE + newDivId);

                    $('#tdPointValue' + SC_UNDER_SCORE + newDivId).html(newDivId);
                }
            }
            $('#innerDivAddDataPointTbl').attr('point-value', maxDivPointValue - NUMBER_ONE);
        },
        createFenceDataPointBody : function()
        {
            $('#innerDivAddDataPointTbl').empty();

            var pointValue = NUMBER_ONE;
            this.addDataPointTR(pointValue);

            $('#innerDivAddDataPointTbl').attr('point-value', pointValue);
            $('#innerDivAddDataPointTbl').append(dataPointHtml);

            $('#fenceNameTxt').val("");
            $('#fenceTotalDataPointsTxt').val("");
        },
        resetFenceDataPoints : function()
        {
            $('#innerDivAddDataPointTbl').empty();
            $('#innerDivAddDataPointTbl').attr('point-value', NUMBER_ZERO);
            //$('#innerDivAddDataPointTbl').append(this.addDataPointTR(pointValue));

            var fenceCodeTxt = $('#fenceCodeTxt').val();
            $('#fenceCodeTxt').val("");
            $('#fenceNameTxt').val("");
            $('#fenceAddressTxt').val("");
            $('#fenceTotalDataPointsTxt').val("");

            if (fenceCodeTxt.trim().length > NUMBER_ZERO)
            {
                this.populateFenceDAOs('editFence', [fenceCodeTxt]);
            }
        },
        createFenceFromDataPointsOnMap : function()
        {
            //saving of fence point in database
            var _elements = $('#fence-data-info-popup').find('input[cordinates="true"]');
            if (_elements)
            {
                var latLngArray = [];
                for (var i = NUMBER_ZERO; i < _elements.length; i++)
                {
                    if (i % NUMBER_TWO === NUMBER_ZERO)
                    {
                        var latitudeData = $(_elements[i]);
                        var longitudeData = $(_elements[i + NUMBER_ONE]);

                        var latitudeValue = parseFloat(latitudeData.val());
                        var longitudeValue = parseFloat(longitudeData.val());

                        if (i % NUMBER_TWO === NUMBER_ZERO)
                        {
                            if (isNaN(latitudeValue))
                            {
                                latitudeData.focus();
                                alert("Please enter Valid Latitude");
                                return false;
                            }
                            else if (isNaN(longitudeValue))
                            {
                                longitudeData.focus();
                                alert("Please enter Valid Longitude");
                                return false;
                            }
                            else
                            {
                                latLngArray.push({
                                    lat: latitudeValue,
                                    lng: longitudeValue
                                });
                            }
                        }
                    }
                }

                if (latLngArray.length > NUMBER_ZERO)
                {
                    var fenceDataPointsArray = [];

                    for (var i = NUMBER_ZERO; i < latLngArray.length; i++)
                    {
                        var latitudeValue = parseFloat(latLngArray[i].lat);
                        var longitudeValue = parseFloat(latLngArray[i].lng);
                        var serialNo = i + NUMBER_ONE;

                        fenceDataPointsArray.push({
                            serialNo: serialNo,
                            latitude: latitudeValue,
                            longitude: longitudeValue
                        });
                    }

                    var clientDAO = ClientService.getClientInfoDAO();
                    var userInfoDAO = UserService.getUserInfoDAO();
                    var fenceData = {
                        clientCode: clientDAO.clientCode,
                        createdBy: userInfoDAO.userCode,
                        isActive: "True",
                        masterFenceCode: "NONE",
                        fenceCode: $("#fenceCodeTxt").val(),
                        fenceName: $("#fenceNameTxt").val(),
                        address: $("#fenceAddressTxt").val(),
                        totalNoOfDataPoints: fenceDataPointsArray.length,
                        fenceDetailDAOsList: fenceDataPointsArray
                    };
                    return fenceData;
                }
                return null;
            }
        },
        saveFenceDAO : function()
        {
            var fenceData = this.createFenceFromDataPointsOnMap();
            if (fenceData)
            {
                var _FenceInfoService = this;
                $http({
                    method: 'POST',
                    url: 'saveFenceDAO',
                    data : JSON.stringify(fenceData),
                }).then(function (response)
                {
                    var fenceDAO = response.data;
                    _FenceInfoService.callbackSaveFenceDAO(fenceDAO);
                }, function (response)
                {
                    console.log("ERROR: " + response);
                });
            }
        },
        callbackSaveFenceDAO : function(fenceDAO)
        {
            if (fenceDAO && fenceDAO.fenceCode > NUMBER_ZERO)
            {
                $('#fence-data-info-popup').modal('hide');
                $('#menu-close').click();
                this.populateFenceDAOs('plotFence', [fenceDAO.fenceCode]);
            }
        },
        editFenceDAO : function()
        {
            var fenceData = this.createFenceFromDataPointsOnMap();
            if (fenceData)
            {
                var _FenceInfoService = this;
                $http({
                    method: 'POST',
                    url: 'editFenceDAO',
                    data : JSON.stringify(fenceData),
                }).then(function (response)
                {
                    var fenceDAO = response.data;
                    _FenceInfoService.callbackEditFenceDAO(fenceDAO);
                }, function (response)
                {
                    console.log("ERROR: " + response);
                });
            }
        },
        callbackEditFenceDAO : function(fenceDAO)
        {
            if (fenceDAO && fenceDAO.fenceCode > NUMBER_ZERO)
            {
                $("#alertMessageBody").html("Fence Details Updated.");
                $("#alert-modal-popup").modal('show');
                $('#fence-data-info-popup').modal('hide');
                $('#menu-close').click();
                //this.populateFenceDAOs('plotFence', [fenceDAO.fenceCode]);
            }
        },
        viewFenceDetails : function(fenceCode)
        {
            var fenceDetailData = {
                fenceCode: fenceCode
            };

            $http({
                method: 'POST',
                url: 'getFenceDetails',
                dataType: 'json',
                data: JSON.stringify(fenceDetailData),
            }).then(function (response)
                {
                    $rootScope.fenceDetailDAOs = response.data;

                    // $('.accordion-section a').next().hide();
                    // $('.accordion-section a').removeAttr("style");
                    // $('.accordion-section a').find('i').addClass( "fa-plus-square");
                    //
                    // $("#fenceDetailDiv_" + fenceCode).show();
                    // $("#fenceDetailDiv_" + fenceCode).prev().css("background-color", "#007bff");
                    // $("#fenceDetailDiv_" + fenceCode).prev().find('i').removeClass( "fa-plus-square");



                    $('.accordion-section a').next().toggle();
                    $('.accordion-section a').toggleClass( "highlight_div");
                    $('.accordion-section a').find('i').toggleClass( "fa-minus-square  fa-plus-square");

                    $("#fenceDetailDiv_" + fenceCode).parents('.accordion-block ').siblings().find('.accordion-section a').next().hide();
                    $("#fenceDetailDiv_" + fenceCode).parents('.accordion-block ').siblings().find('.accordion-section a').children().children().removeClass("fa-minus-square ").addClass( "fa-plus-square");
                    $("#fenceDetailDiv_" + fenceCode).parents('.accordion-block ').siblings().find('.accordion-section a').removeClass( "highlight_div");


                },
                function(error)
                {
                    console.log("ERROR: " + error);
                }
            );
        }
    };
}]);