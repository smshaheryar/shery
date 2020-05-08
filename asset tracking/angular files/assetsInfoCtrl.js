
// Assets Info
app.controller('assetsInfoCtrl', function($scope, ClientService, AssetsInfoService)
{
    $scope.initializeAssetsInfo = function()
    {
        AssetsInfoService.initializeAssetsInfoData('assetsInfoTbl', ClientService.getClientInfoDAO());
    };

    $scope.saveAsset = function()
    {
        AssetsInfoService.saveAssetDAO();
    };

    $scope.editAsset = function()
    {
        AssetsInfoService.editAssetDAO();
    }

    $scope.closeAssetsInfoPopup = function()
    {
        $scope.closePopupAndToggleLeftPanel();
    };
});