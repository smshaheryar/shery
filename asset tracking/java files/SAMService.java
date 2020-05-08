package com.conure.sam.service;

import com.conure.sam.dto.*;
import com.conure.sam.entities.*;
import com.conure.sam.utilities.Constants;
import com.conure.sam.utilities.DateFormat;
import com.conure.sam.utilities.Utility;
import org.opengts.util.GeoPoint;
import org.opengts.util.GeoPolygon;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileOutputStream;

public class SAMService
{
    public static LocationPointDTO isAssetInsideFence(DatabaseInfoDAO databaseInfoDAO, CheckPositionDTO checkPositionDTO)
    {
        LocationPointDTO locationPointDTO = checkPositionDTO.getLocationPointDTO();
        UserInfoDAO userInfoDAO = checkPositionDTO.getUserInfoDAO();

        locationPointDTO.setUserCode(userInfoDAO.getUserCode().toString());

        AssetDAO assetDAO = locationPointDTO.getAssetDAO();
        FenceDAO fenceDAO = assetDAO.getFenceDAO();

        if (fenceDAO != null)
        {
            List<GeoPoint> geoPointsList = new ArrayList<>();

            for (FenceDetailDAO fenceDetailDAO : fenceDAO.getFenceDetailDAOsList())
            {
                geoPointsList.add(new GeoPoint(fenceDetailDAO.getLatitude(), fenceDetailDAO.getLongitude()));
            }

            GeoPoint geoPoint = new GeoPoint(locationPointDTO.getLatitude(), locationPointDTO.getLongitude());
            GeoPolygon fence = new GeoPolygon(geoPointsList);

            boolean isAssetInsideFence = fence.isPointInside(geoPoint);
            locationPointDTO.setAssetInsideFence(isAssetInsideFence);

            fillAndSaveAlertDAO(databaseInfoDAO, locationPointDTO);
            fillAndSaveAssetLocationInfoDAO(databaseInfoDAO, locationPointDTO);

            assetDAO = SAMService.getAssetDAODetailsByAssetCode(databaseInfoDAO, assetDAO.getAssetCode().toString());
            locationPointDTO.setAssetDAO(assetDAO);
        }
        return checkPositionDTO.getLocationPointDTO();
    }

    private static void fillAndSaveAssetLocationInfoDAO(DatabaseInfoDAO databaseInfoDAO, LocationPointDTO locationPointDTO)
    {
        try
        {
            AssetDAO assetDAO = locationPointDTO.getAssetDAO();
            AlertsDAO alertsDAO = locationPointDTO.getAlertsDAO();

            AssetLocationDAO assetLocationDAO = new AssetLocationDAO();

            assetLocationDAO.setAssetCode(assetDAO.getAssetCode().toString());
            assetLocationDAO.setAlertCode(alertsDAO.getAlertCode().toString());
            assetLocationDAO.setLatitude(locationPointDTO.getLatitude());
            assetLocationDAO.setLongitude(locationPointDTO.getLongitude());
            assetLocationDAO.setUserCode(locationPointDTO.getUserCode());
            assetLocationDAO.setAssetLocationStatus(locationPointDTO.isAssetInsideFence() ? Constants.FENCE.INSIDE : Constants.FENCE.OUTSIDE);

            assetLocationDAO.saveDAO(databaseInfoDAO, assetLocationDAO);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void fillAndSaveAlertDAO(DatabaseInfoDAO databaseInfoDAO, LocationPointDTO locationPointDTO)
    {
        try
        {
            AlertsDAO alertsDAO = new AlertsDAO();

            AssetDAO assetDAO = locationPointDTO.getAssetDAO();
            FenceDAO fenceDAO = assetDAO.getFenceDAO();

            alertsDAO.setClientCode(fenceDAO.getClientCode());
            alertsDAO.setFenceCode(fenceDAO.getFenceCode().toString());
            alertsDAO.setAssetCode(assetDAO.getAssetCode().toString());
            alertsDAO.setUserCode(locationPointDTO.getUserCode());

            alertsDAO.setAlertType(Constants.ALERT_TYPE.NOTIFICATION);
            alertsDAO.setLatitude(locationPointDTO.getLatitude());
            alertsDAO.setLongitude(locationPointDTO.getLongitude());
            alertsDAO.setStatus(locationPointDTO.isAssetInsideFence() ? Constants.FENCE.INSIDE : Constants.FENCE.OUTSIDE);

            calculateDurationAndDistance(databaseInfoDAO, alertsDAO);

            alertsDAO.saveDAO(databaseInfoDAO, alertsDAO);

            locationPointDTO.setAlertsDAO(alertsDAO);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void calculateDurationAndDistance(DatabaseInfoDAO databaseInfoDAO, AlertsDAO alertsDAO)
    {
        SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
        searchCriteriaDTO.setAssetCode(alertsDAO.getAssetCode());
        searchCriteriaDTO.setAssetLocationStatus(Constants.FENCE.INSIDE);

        AssetLocationDAO assetLocationDAO = getAssetPositionInsideFence(databaseInfoDAO, searchCriteriaDTO);
        if (assetLocationDAO != null)
        {
            alertsDAO.setDuration(calculateTimeInterval(assetLocationDAO.getTime().toEpochMilli(), System.currentTimeMillis()));
            alertsDAO.setDistance(calculateDistance(assetLocationDAO.getLatitude(), assetLocationDAO.getLongitude(), alertsDAO.getLatitude(), alertsDAO.getLongitude()));
        }
    }

    public static List<CountryInfoDAO> getCountryInfoDAOsByCriteria(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new CountryInfoDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    private static CountryInfoDAO getCountryInfoDAOByCityCode(DatabaseInfoDAO databaseInfoDAO, Integer cityCode)
    {
        return new CountryInfoDAO().fetchDAOByCode(databaseInfoDAO, cityCode);
    }

    public static Collection<StateDTO> getAllDistinctStates(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<CountryInfoDAO> countryInfoDAOList = getCountryInfoDAOsByCriteria(databaseInfoDAO, searchCriteriaDTO);
        if (countryInfoDAOList != null && !countryInfoDAOList.isEmpty())
        {
            LinkedHashSet<StateDTO> stateDTOs = new LinkedHashSet<>();

            for (CountryInfoDAO countryInfoDAO : countryInfoDAOList)
            {
                StateDTO stateDTO = new StateDTO();

                stateDTO.setStateName(countryInfoDAO.getStateName());
                stateDTO.setCountryName(countryInfoDAO.getCountryName());

                stateDTOs.add(stateDTO);
            }

            TreeSet<StateDTO> sortedStateDTOs = new TreeSet<>();
            sortedStateDTOs.addAll(stateDTOs);

            return sortedStateDTOs;
        }
        return null;
    }

    public static Collection<CityDTO> getAllDistinctCities(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<CountryInfoDAO> countryInfoDAOList = getCountryInfoDAOsByCriteria(databaseInfoDAO, searchCriteriaDTO);
        if (countryInfoDAOList != null && !countryInfoDAOList.isEmpty())
        {
            LinkedHashSet<CityDTO> cityDTOs = new LinkedHashSet<>();

            for (CountryInfoDAO countryInfoDAO : countryInfoDAOList)
            {
                CityDTO cityDTO = new CityDTO();

                cityDTO.setCityCode(countryInfoDAO.getCityCode());
                cityDTO.setCityName(countryInfoDAO.getCityName());
                cityDTO.setStateName(countryInfoDAO.getStateName());
                cityDTO.setCountryName(countryInfoDAO.getCountryName());

                cityDTO.setLatitude(countryInfoDAO.getLatitude());
                cityDTO.setLongitude(countryInfoDAO.getLongitude());

                cityDTOs.add(cityDTO);
            }

            TreeSet<CityDTO> sortedCityDTOs = new TreeSet<>();
            sortedCityDTOs.addAll(cityDTOs);

            return sortedCityDTOs;
        }
        return null;
    }

    public static CityDTO getCityDTO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        Collection<CityDTO> cityDTOsList = getAllDistinctCities(databaseInfoDAO, searchCriteriaDTO);
        return cityDTOsList != null && !cityDTOsList.isEmpty() ? cityDTOsList.iterator().next() : null;
    }

    public static void addFenceDAO(DatabaseInfoDAO databaseInfoDAO, FenceDAO fenceDAO) throws Exception
    {
        try
        {
            if (fenceDAO != null && fenceDAO.getFenceDetailDAOsList() != null && !fenceDAO.getFenceDetailDAOsList().isEmpty())
            {
                List<FenceDetailDAO> fenceDetailDAOsList = fenceDAO.getFenceDetailDAOsList();
                if (fenceDetailDAOsList != null && !fenceDetailDAOsList.isEmpty())
                {
                    fenceDAO.saveDAO(databaseInfoDAO, fenceDAO);

                    for (FenceDetailDAO fenceDetailDAO : fenceDetailDAOsList)
                    {
                        fenceDetailDAO.setFenceCode(fenceDAO.getFenceCode().toString());
                    }
                    new FenceDetailDAO().saveDAOs(databaseInfoDAO, fenceDetailDAOsList);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static List<FenceDAO> getAllDistinctFenceDAOsByFenceCodes(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<FenceDAO> fenceDAOsList = new ArrayList<>();

        if (searchCriteriaDTO != null && searchCriteriaDTO.getFenceCodes() != null)
        {
            for (String fenceCode : searchCriteriaDTO.getFenceCodes())
            {
                if (StringUtils.hasText(fenceCode))
                {
                    searchCriteriaDTO.setAssetCode(null);
                    searchCriteriaDTO.setFenceCode(fenceCode);

                    FenceDAO fenceDAO = getFenceDAOByFenceCode(databaseInfoDAO, fenceCode);
                    if (fenceDAO != null)
                    {
                        fenceDAO.setFenceDetailDAOsList(getFenceDetailDAOsListByFenceCode(databaseInfoDAO, fenceCode));
                        fenceDAO.setAssetDAOsList(getAssetDAOsDetails(databaseInfoDAO, searchCriteriaDTO));
                    }
                    fenceDAOsList.add(fenceDAO);
                }
            }
            return fenceDAOsList;
        }
        return null;
    }
    public static void editFenceDAO(DatabaseInfoDAO databaseInfoDAO, FenceDAO fenceDAO)
    {
        try
        {
            if (fenceDAO != null && fenceDAO.getFenceDetailDAOsList() != null && !fenceDAO.getFenceDetailDAOsList().isEmpty())
            {
                List<FenceDetailDAO> fenceDetailDAOsList = fenceDAO.getFenceDetailDAOsList();
                if (fenceDetailDAOsList != null && !fenceDetailDAOsList.isEmpty())
                {
                    String fenceCode = fenceDAO.getFenceCode().toString();
                    new FenceDetailDAO().deleteDAOs(databaseInfoDAO, fenceDetailDAOsList);

                    for (FenceDetailDAO fenceDetailDAO : fenceDetailDAOsList)
                    {
                        fenceDetailDAO.setFenceCode(fenceCode);
                    }
                    new FenceDetailDAO().saveDAOs(databaseInfoDAO, fenceDetailDAOsList);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void editClientDAO(DatabaseInfoDAO databaseInfoDAO, ClientDAO clientDAO)
    {
        try
        {
            if (clientDAO != null && Utility.isNumberGreaterThanZero(clientDAO.getClientCode()))
            {
                clientDAO.deleteDAO(databaseInfoDAO, clientDAO);
                clientDAO.saveDAO(databaseInfoDAO, clientDAO);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void editUserDAO(DatabaseInfoDAO databaseInfoDAO, UserInfoDAO userDAO)
    {
        try
        {
            if (userDAO != null && Utility.isNumberGreaterThanZero(userDAO.getUserCode()))
            {
                userDAO.deleteDAO(databaseInfoDAO,userDAO);
                userDAO.saveDAO(databaseInfoDAO,userDAO);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static List<FenceDetailDAO> getFenceDetailDAOsByFenceCode(DatabaseInfoDAO databaseInfoDAO, String fenceCode)
    {
        SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
        searchCriteriaDTO.setFenceCode(fenceCode);

        return new FenceDetailDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    public static void addAssetInfoDAO(DatabaseInfoDAO databaseInfoDAO, AssetDAO assetDAO) throws Exception
    {
        try
        {
            if (assetDAO != null)
            {
                assetDAO.saveDAO(databaseInfoDAO, assetDAO);

                List<AssetLocationDAO> assetLocationDAOsList = assetDAO.getAssetLocationDAOsList();
                if (assetLocationDAOsList != null && !assetLocationDAOsList.isEmpty())
                {
                    String fenceCode = assetDAO.getFenceCode();
                    FenceDAO fenceDAO = getFenceDAODetailsByFenceCode(databaseInfoDAO, fenceCode);
                    assetDAO.setFenceDAO(fenceDAO);

                    CheckPositionDTO checkPositionDTO = new CheckPositionDTO();
                    checkPositionDTO.setFenceDAO(fenceDAO);

                    for (AssetLocationDAO assetLocationDAO : assetLocationDAOsList)
                    {
                        LocationPointDTO locationPointDTO = new LocationPointDTO();

                        locationPointDTO.setLatitude(assetLocationDAO.getLatitude());
                        locationPointDTO.setLongitude(assetLocationDAO.getLongitude());
                        locationPointDTO.setAssetDAO(assetDAO);

                        checkPositionDTO.setUserInfoDAO(assetDAO.getUserInfoDAO());
                        checkPositionDTO.setLocationPointDTO(locationPointDTO);

                        isAssetInsideFence(databaseInfoDAO, checkPositionDTO);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public static void editAssetDAO(DatabaseInfoDAO databaseInfoDAO, AssetDAO assetDAO)
    {
        try
        {
            if (assetDAO != null && Utility.isNumberGreaterThanZero(assetDAO.getAssetCode()))
            {
                List<AssetLocationDAO> assetLocationDAOsList = assetDAO.getAssetLocationDAOsList();
                if (assetLocationDAOsList != null && !assetLocationDAOsList.isEmpty())
                {
                    assetDAO.deleteDAO(databaseInfoDAO,assetDAO);
                    addAssetInfoDAO(databaseInfoDAO, assetDAO);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static AssetDAO getAssetDAODetailsByAssetCode(DatabaseInfoDAO databaseInfoDAO, String assetCode)
    {
        SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
        searchCriteriaDTO.setAssetCode(assetCode);

        AssetDAO assetDAO = getAssetDAOByAssetCode(databaseInfoDAO, assetCode);
        fillAssetDAODetails(databaseInfoDAO, assetDAO, searchCriteriaDTO);

        return assetDAO;
    }

    public static List<AssetDAO> getAssetDAOsDetails(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<AssetDAO> assetDAOsList = getAssetDAOs(databaseInfoDAO, searchCriteriaDTO);
        if (assetDAOsList != null && !assetDAOsList.isEmpty())
        {
            for (AssetDAO assetDAO : assetDAOsList)
            {
                fillAssetDAODetails(databaseInfoDAO, assetDAO, searchCriteriaDTO);
            }
        }
        return assetDAOsList;
    }

    private static void fillAssetDAODetails(DatabaseInfoDAO databaseInfoDAO, AssetDAO assetDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        String fenceCode = assetDAO.getFenceCode();

        searchCriteriaDTO.setFenceCode(fenceCode);
        searchCriteriaDTO.setAssetCode(assetDAO.getAssetCode().toString());

        FenceDAO fenceDAO = getFenceDAODetailsByFenceCode(databaseInfoDAO, fenceCode);
        assetDAO.setFenceDAO(fenceDAO);
        assetDAO.setUserInfoDAO(fenceDAO.getUserInfoDAO());

        List<AssetLocationDAO> assetLocationDAOsList = getAssetLocationDAOs(databaseInfoDAO, searchCriteriaDTO);
        if (assetLocationDAOsList != null && !assetLocationDAOsList.isEmpty())
        {
            AssetLocationDAO assetLocationDAO = assetLocationDAOsList.get(Constants.NUMBER.ZERO);
            assetDAO.setAssetLocationStatus(assetLocationDAO.getAssetLocationStatus());
            assetDAO.setAssetLocationDAOsList(assetLocationDAOsList);

            List<AlertsDAO> alertsDAOs = getAlertsDAOsDetails(databaseInfoDAO, searchCriteriaDTO);
            if (alertsDAOs != null && !alertsDAOs.isEmpty())
            {
                AlertsDAO alertsDAO = alertsDAOs.get(Constants.NUMBER.ZERO);
                assetLocationDAO.setAlertsDAO(alertsDAO);
            }
        }
    }

    private static List<AlertsDAO> getAlertsDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new AlertsDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    public static List<AlertsDAO> getAlertsDAOsDetails(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<AlertsDAO> alertDAOs = getAlertsDAOs(databaseInfoDAO, searchCriteriaDTO);
        fillAlertsDAOsDetails(databaseInfoDAO, alertDAOs);

        return alertDAOs;
    }

    private static void fillAlertsDAOsDetails(DatabaseInfoDAO databaseInfoDAO, List<AlertsDAO> alertDAOsList)
    {
        if (alertDAOsList != null && !alertDAOsList.isEmpty())
        {
            for (AlertsDAO alertDAO : alertDAOsList)
            {
                String assetCode = alertDAO.getAssetCode();
                String alertCode = alertDAO.getAlertCode().toString();

                SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
                searchCriteriaDTO.setAssetCode(assetCode);
                searchCriteriaDTO.setAlertCode(alertCode);

                alertDAO.setAssetDAO(getAssetDAOByAssetCode(databaseInfoDAO, assetCode));
                alertDAO.setFenceDAO(getFenceDAOByFenceCode(databaseInfoDAO, alertDAO.getFenceCode()));
                alertDAO.setUserInfoDAO(getUserInfoDAOByUserCode(databaseInfoDAO, alertDAO.getUserCode()));

                alertDAO.setAssetLocationDAO(getAssetLocationDAO(databaseInfoDAO, searchCriteriaDTO));
            }
        }
    }

    public static List<ClientDAO> getClientDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<ClientDAO> clientDAOs = new ClientDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);

        if (clientDAOs != null && !clientDAOs.isEmpty())
        {
            for (ClientDAO clientDAO : clientDAOs)
            {
                clientDAO.setCountryInfoDAO(getCountryInfoDAOByCityCode(databaseInfoDAO, Integer.parseInt(clientDAO.getCityCode())));
            }
        }
        return clientDAOs;
    }

    public static UserInfoDAO validateUserDAO(DatabaseInfoDAO databaseInfoDAO, UserInfoDAO userInfoDAO)
    {
        if (userInfoDAO != null)
        {
            String loginEmail = userInfoDAO.getLoginEmail();
            String password = userInfoDAO.getPassword();

            if (!StringUtils.hasText(loginEmail))
            {
                userInfoDAO.setMessage("Login User cannot be empty. Please enter Login User.");
                return userInfoDAO;
            }
            else if (!StringUtils.hasText(password))
            {
                userInfoDAO.setMessage("Password cannot be empty. Please enter password.");
                return userInfoDAO;
            }

            SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
            searchCriteriaDTO.setLoginEmail(loginEmail);

            UserInfoDAO loginUserInfoDAO = getUserDAOByLoginEmail(databaseInfoDAO, loginEmail);

            if (loginUserInfoDAO == null)
            {
                userInfoDAO.setMessage("Invalid User. Login User NOT Found. Please enter correct Login User.");
                return userInfoDAO;
            }
            //else if (!loginUserInfoDAO.getPassword().equals(Crypto.encrypt(password)))
            else if (!loginUserInfoDAO.getPassword().equals(password))
            {
                userInfoDAO.setMessage("Password Mismatch. Unable to Login. Please enter correct password.");
                return userInfoDAO;
            }
            return loginUserInfoDAO;
        }
        return null;
    }

    public static void addClientInfoDAO(DatabaseInfoDAO databaseInfoDAO, ClientDAO clientDAO) throws Exception
    {
        try
        {
            clientDAO.saveDAO(databaseInfoDAO, clientDAO);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void addUserInfoDAO(DatabaseInfoDAO databaseInfoDAO, UserInfoDAO userInfoDAO) throws Exception
    {
        try
        {
            userInfoDAO.saveDAO(databaseInfoDAO, userInfoDAO);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static String calculateTimeInterval(long startTime, long endTime)
    {
        String timeInterval = "";
        try
        {
            long timeDifference = Math.abs(endTime - startTime);
            long secondsDifference = (timeDifference / 1000) % 60;
            long minutesDifference = (timeDifference / (60 * 1000)) % 60;
            long hoursDifference = timeDifference / (60 * 60 * 1000);

            if (hoursDifference >= 24)
            {
                long noOfDays = (hoursDifference / 24);
                hoursDifference = hoursDifference - (noOfDays * 24);
                timeInterval = noOfDays + "D:" + hoursDifference + "H:" + minutesDifference + "M:" + secondsDifference + "S";
            }
            else
            {
                timeInterval = hoursDifference + "H:" + minutesDifference + "M:" + secondsDifference + "S";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return timeInterval;
    }

    private static String calculateDistance(double latitudeStart, double longitudeStart, double latitudeEnd, double longitudeEnd)
    {
        double latitudeDifference = Math.abs(latitudeEnd - latitudeStart);
        double longitudeDifference = Math.abs(longitudeEnd - longitudeStart);

        double latitudeInRadians = Math.toRadians(latitudeDifference);
        double longitudeInRadians = Math.toRadians(longitudeDifference);

        double angleInRadians = Math.sin(latitudeInRadians / Constants.NUMBER.TWO) * Math.sin(latitudeInRadians / Constants.NUMBER.TWO) +
                Math.cos(Math.toRadians(latitudeStart)) * Math.cos(Math.toRadians(latitudeEnd)) *
                        Math.sin(longitudeInRadians / Constants.NUMBER.TWO) * Math.sin(longitudeInRadians / Constants.NUMBER.TWO);

        double curvature = Constants.NUMBER.TWO * Math.atan2(Math.sqrt(angleInRadians), Math.sqrt(Constants.NUMBER.ONE - angleInRadians));
        double distanceInKilometers = Constants.EARTH_RADIUS_IN_METERS * curvature / Constants.NUMBER.THOUSAND;

        return String.format("%.2f", distanceInKilometers) + " " + Constants.DISTANCE_UNITS.KM;
    }

    private static UserInfoDAO getUserInfoDAOByUserCode(DatabaseInfoDAO databaseInfoDAO, String userCode)
    {
        try
        {
            if (StringUtils.hasText(userCode))
            {
                return new UserInfoDAO().fetchDAOByCode(databaseInfoDAO, userCode);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static UserInfoDAO getUserDAOByLoginEmail(DatabaseInfoDAO databaseInfoDAO, String loginEmail)
    {
        try
        {
            if (StringUtils.hasText(loginEmail))
            {
                SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
                searchCriteriaDTO.setLoginEmail(loginEmail);

                return new UserInfoDAO().fetchDAO(databaseInfoDAO, searchCriteriaDTO);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    private static FenceDAO getFenceDAODetailsByFenceCode(DatabaseInfoDAO databaseInfoDAO, String fenceCode)
    {
        FenceDAO fenceDAO = getFenceDAOByFenceCode(databaseInfoDAO, fenceCode);
        if (fenceDAO != null)
        {
            fenceDAO.setFenceDetailDAOsList(getFenceDetailDAOsByFenceCode(databaseInfoDAO, fenceCode));
            fenceDAO.setUserInfoDAO(getUserInfoDAOByUserCode(databaseInfoDAO, fenceDAO.getCreatedBy()));
        }
        return fenceDAO;
    }

    private static FenceDAO getFenceDAOByFenceCode(DatabaseInfoDAO databaseInfoDAO, String fenceCode)
    {
        return new FenceDAO().fetchDAOByCode(databaseInfoDAO, fenceCode);
    }

    public static List<FenceDAO> getAllFenceDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<FenceDAO> fenceDAOsList = new FenceDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);

        if (fenceDAOsList != null && !fenceDAOsList.isEmpty())
        {
            for (FenceDAO fenceDAO : fenceDAOsList)
            {
                fenceDAO.setUserInfoDAO(getUserInfoDAOByUserCode(databaseInfoDAO, fenceDAO.getCreatedBy()));
            }
        }
        return fenceDAOsList;
    }

    public static List<FenceDetailDAO> getFenceDetailDAOsListByCriteria(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new FenceDetailDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    private static List<FenceDetailDAO> getFenceDetailDAOsListByFenceCode(DatabaseInfoDAO databaseInfoDAO, String fenceCode)
    {
        SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
        searchCriteriaDTO.setFenceCode(fenceCode);
        return new FenceDetailDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    public static List<UserInfoDAO> getUserInfoDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        List<UserInfoDAO> userInfoDAOs = new UserInfoDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);

        if (userInfoDAOs != null && !userInfoDAOs.isEmpty())
        {
            for (UserInfoDAO userInfoDAO : userInfoDAOs)
            {
                userInfoDAO.setCountryInfoDAO(getCountryInfoDAOByCityCode(databaseInfoDAO, userInfoDAO.getCityCode()));
            }
        }
        return userInfoDAOs;
    }

    private static AssetDAO getAssetDAOByAssetCode(DatabaseInfoDAO databaseInfoDAO, String assetCode)
    {
        return new AssetDAO().fetchDAOByCode(databaseInfoDAO, assetCode);
    }

    private static List<AssetDAO> getAssetDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new AssetDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    private static List<AssetLocationDAO> getAssetLocationDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new AssetLocationDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    private static AssetLocationDAO getAssetLocationDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new AssetLocationDAO().fetchDAO(databaseInfoDAO, searchCriteriaDTO);
    }

    public static AssetLocationDAO getAssetPositionInsideFence(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        searchCriteriaDTO.setAssetLocationStatus(Constants.FENCE.INSIDE);

        List<AssetLocationDAO> insideFenceAssetLocationDAOs = getAssetLocationDAOs(databaseInfoDAO, searchCriteriaDTO);
        return insideFenceAssetLocationDAOs != null && !insideFenceAssetLocationDAOs.isEmpty() ? insideFenceAssetLocationDAOs.get(Constants.NUMBER.ZERO) : null;
    }

    public static List<DeviceDAO> getDeviceDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        return new DeviceDAO().fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
    }

    public static AnalyticsInfoDTO getAnalyticsInfoDTO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        AnalyticsInfoDTO analyticsInfoDTO = new AnalyticsInfoDTO();

        List<AlertsDAO> alertDAOs = getAlertsDAOs(databaseInfoDAO, searchCriteriaDTO);

        if (alertDAOs != null && !alertDAOs.isEmpty())
        {
            int totalAlertsGeneratedCount = alertDAOs.size();

            if (totalAlertsGeneratedCount > Constants.NUMBER.ZERO)
            {
                //List<String> distinctAssetsList = alertDAOs.stream().map(alertDAO -> alertDAO.getAssetCode()).distinct().collect(Collectors.toList());
                List<AlertsDAO> distinctAssetsInAlertsDAOList = alertDAOs.stream().filter(Utility.distinctByKey(p -> p.getAssetCode())).collect(Collectors.toList());

                List<AlertsDAO> insideFenceAlertDAOs = alertDAOs.stream().filter(alertDAO -> alertDAO.getStatus().equalsIgnoreCase(Constants.FENCE.INSIDE)).collect(Collectors.toList());

                int totalAssetsCount = distinctAssetsInAlertsDAOList.size();
                int totalAssetInsideCount = insideFenceAlertDAOs != null ? insideFenceAlertDAOs.size() : Constants.NUMBER.ZERO;
                int totalAssetOutsideCount = totalAlertsGeneratedCount - totalAssetInsideCount;

                analyticsInfoDTO.setTotalAssetsCount(totalAssetsCount);
                analyticsInfoDTO.setTotalAlertsGeneratedCount(totalAlertsGeneratedCount);
                analyticsInfoDTO.setTotalAssetsInsideCount(totalAssetInsideCount);
                analyticsInfoDTO.setTotalAssetsOutsideCount(totalAssetOutsideCount);

                if (!distinctAssetsInAlertsDAOList.isEmpty())
                {
                    List<AssetDayWiseCountDTO> assetDayWiseCountDTOsList = new ArrayList<>();

                    for (AlertsDAO _alertsDAO : distinctAssetsInAlertsDAOList)
                    {
                        String assetCode = _alertsDAO.getAssetCode();
                        /*_searchCriteriaDTO.setAssetCode(_alertsDAO.getAssetCode());
                        _searchCriteriaDTO.setAssetLocationStatus(Constants.FENCE.OUTSIDE);*/

                        List<AlertsDAO> outsideFenceAlertDAOs = alertDAOs.stream().filter(alertDAO ->
                                alertDAO.getStatus().equalsIgnoreCase(Constants.FENCE.OUTSIDE) && alertDAO.getAssetCode().equalsIgnoreCase(assetCode)
                        ).collect(Collectors.toList());

                        AssetDayWiseCountDTO assetDayWiseCountDTO = new AssetDayWiseCountDTO();

                        //AssetDAO assetDAO = getAssetDAOByAssetCode(databaseInfoDAO, _alertsDAO.getAssetCode());
                        assetDayWiseCountDTO.setAssetDAO(getAssetDAOByAssetCode(databaseInfoDAO, _alertsDAO.getAssetCode()));
                        assetDayWiseCountDTO.setAssetCount(outsideFenceAlertDAOs.size());

                        assetDayWiseCountDTOsList.add(assetDayWiseCountDTO);
                    }

                    analyticsInfoDTO.setAssetDayWiseCountDTOsList(assetDayWiseCountDTOsList);
                }
            }
        }
        return analyticsInfoDTO;
    }

    public static List<WeekDTO> getWeeksBetweenDates(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        LocalDate startDate = DateFormat.getDateFromUIFormatSlash(searchCriteriaDTO.getStartDateUI());
        LocalDate endDate = DateFormat.getDateFromUIFormatSlash(searchCriteriaDTO.getEndDateUI());

        List<WeekDTO> weekDTOsList = new ArrayList<>();
        if (startDate != null && endDate != null)
        {
            while (startDate.isBefore(endDate) || startDate.equals(endDate))
            {
                LocalDate weekEndDate = startDate.with(DayOfWeek.SUNDAY);

                if (endDate.isBefore(weekEndDate))
                {
                    weekEndDate = endDate;
                }

                WeekDTO weekDTO = fillWeekDTO(startDate, weekEndDate);

                searchCriteriaDTO.setStartDateUI(startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toString());
                searchCriteriaDTO.setEndDateUI(weekEndDate.atTime(23, 59, 59, 000).toInstant(ZoneOffset.UTC).toString());

                AnalyticsInfoDTO analyticsInfoDTO = getAnalyticsInfoDTO(databaseInfoDAO, searchCriteriaDTO);
                weekDTO.setAnalyticsInfoDTO(analyticsInfoDTO);

                weekDTOsList.add(weekDTO);

                startDate = weekEndDate.plusDays(1);
            }
        }
        return weekDTOsList;
    }

    public static WeekDTO fillWeekDTO(LocalDate weekStartDate, LocalDate weekEndDate)
    {
        WeekDTO weekDTO = new WeekDTO();

        weekDTO.setStartDateOfWeek(weekStartDate);
        weekDTO.setEndDateOfWeek(weekEndDate);
        weekDTO.setWeekNo(1);

        return weekDTO;
    }

    public static List<DayDTO> getDaysBetweenDates(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        LocalDate startDate = DateFormat.getDateFromUIFormatSlash(searchCriteriaDTO.getStartDateUI());
        LocalDate endDate = DateFormat.getDateFromUIFormatSlash(searchCriteriaDTO.getEndDateUI());

        List<DayDTO> dayDTOsList = new ArrayList<>();
        if (startDate != null && endDate != null)
        {
            while (startDate.isBefore(endDate) || startDate.equals(endDate))
            {
                searchCriteriaDTO.setStartDateUI(startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toString());
                searchCriteriaDTO.setEndDateUI(startDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toString());

                AnalyticsInfoDTO analyticsInfoDTO = getAnalyticsInfoDTO(databaseInfoDAO, searchCriteriaDTO);

                DayDTO dayDTO = fillDayDTO(startDate);
                dayDTO.setAnalyticsInfoDTO(analyticsInfoDTO);

                dayDTOsList.add(dayDTO);

                startDate = startDate.plusDays(1);
            }
        }
        return dayDTOsList;
    }

    public static DayDTO fillDayDTO(LocalDate date)
    {
        DayDTO dayDTO = new DayDTO();

        dayDTO.setDate(date);
        dayDTO.setDayName(date.getDayOfWeek().name());

        return dayDTO;
    }



    public static String sendEmail(EmailDTO emailDTO){
       String response = null;
        String addressList="";
        String recipients[] =emailDTO.getRecipient().toString().split(";");
        for(int i=0;i<recipients.length;i++) {
            if(addressList.equals("")){
                addressList=recipients[i];
            }
            else{
                addressList =addressList+","+recipients[i];
            }}

        final String username = Constants.SUPPORT_EMAIL_ADDRESS_FOR_EMAIL;
        final String password = Constants.SUPPORT_EMAIL_ADDRESS_PASSWORD;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "secure.emailsrvr.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Constants.SUPPORT_EMAIL_ADDRESS_FOR_EMAIL));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(addressList));
            message.setSubject(emailDTO.getSubject());
          //  BodyPart messageBodyPart = new MimeBodyPart();
            message.setText(emailDTO.getEmailContent());

        //            Multipart multipart = new MimeMultipart();
        //            multipart.addBodyPart(messageBodyPart);
        //            messageBodyPart = new MimeBodyPart();
        //            String filename =emailDTO.getFilePath();
        //            DataSource source = new FileDataSource(filename);
        //            messageBodyPart.setDataHandler(new DataHandler(source));
        //            messageBodyPart.setFileName(filename);
        //            multipart.addBodyPart(messageBodyPart);
        //            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("email Sent");

        response="email Sent";


        return response;


    }

}