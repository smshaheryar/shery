app.controller('leftPanelCtrl', function($scope, $http, ClientService, AssetsInfoService, LocationService)
{
    $scope.prePopulateFields = function(modalPopup)
    {
        LocationService.resetClientInfo();
        LocationService.fillAllCountries();

        $('#' + modalPopup).modal('show');
    };

    $scope.changeCountry = function(country)
    {
        LocationService.changeCountry(country);
    };

    $scope.changeState = function(country, state)
    {
        LocationService.fillAllCities(country, state);
    };

    //Assets data tab
    $scope.prePopulateAssetsInfoFields = function()
    {
        AssetsInfoService.resetAssetInfoPopup();
        //ClientService.viewFenceInfo();
    };

    $scope.closePopupAndToggleLeftPanel = function()
    {
        $('#menu-close').click();
    };
});