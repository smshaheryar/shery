
app.controller('viewFenceInfoCtrl', function($rootScope, $scope, $http, FenceInfoService)
{
    $scope.viewFenceDetails = function(fenceCode)
    {
        FenceInfoService.viewFenceDetails(fenceCode);
    };

    $scope.filterFenceRecords = function()
    {
        if ($('.accordion-section'))
        {
            var searchFenceTxt = $("#searchFenceName").val().toUpperCase();

            for (i = NUMBER_ZERO; i <= $('.accordion-section').length; i++)
            {
                if ($('#fenceNameDiv_' + i))
                {
                    $('#fenceNameDiv_' + i).css('display', $('#fenceNameDiv_' + i + ' a h4').text().toUpperCase().indexOf(searchFenceTxt) > -1 ? 'block' : 'none');
                }
            }
        }
    };

    $scope.getSelectedFenceCheckBox = function()
    {
        var selectedFenceCodes = $('.accordion-section input[type=checkbox]:checked').map(function(){
            return this.value;
        }).get();
        return selectedFenceCodes;
    };

    $scope.plotFence = function()
    {
        var selectedFenceCodes = $scope.getSelectedFenceCheckBox();

        if (selectedFenceCodes == "")
        {
            alert("Please Select Fence for plot.");
            return;
        }
        FenceInfoService.populateFenceDAOs('plotFence', selectedFenceCodes);
    };

    $scope.editFence = function()
    {
        var selectedFenceCodes = $scope.getSelectedFenceCheckBox();

        if (selectedFenceCodes.length != NUMBER_ONE)
        {
            alert("Please Select One Fence for Edit.");
            return;
        }
        FenceInfoService.initializeFenceInfo();
        FenceInfoService.populateFenceDAOs('editFence', selectedFenceCodes);
    };

    $scope.deleteFence = function()
    {
        var selectedFenceCodes = $scope.getSelectedFenceCheckBox();

        if (selectedFenceCodes.length != NUMBER_ONE)
        {
            alert("Please Select One Fence for Delete.");
            return;
        }
        /*
        FenceInfoService.initializeFenceInfo();
        FenceInfoService.populateFenceDAOs('editFence', selectedFenceCodes);
        */
    };

    $scope.closeViewFenceInfoPopup = function()
    {
        $scope.closePopupAndToggleLeftPanel();
    };
});