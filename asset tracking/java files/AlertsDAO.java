package com.conure.sam.entities;

import com.conure.sam.dto.SearchCriteriaDTO;
import com.conure.sam.utilities.Constants;
import com.conure.sam.utilities.Utility;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Measurement(name = Constants.DB_TABLE_NAME.ALERTS_DAO)
public class AlertsDAO implements IDAO<AlertsDAO>, Comparable<AlertsDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "client_code", tag = true)
    private String clientCode;

    @Column(name = "fence_code", tag = true)
    private String fenceCode;

    @Column(name = "asset_code", tag = true)
    private String assetCode;

    @Column(name = "user_code", tag = true)
    private String userCode;

    @Column(name = "alert_code")
    private Integer alertCode;

    @Column(name = "alert_type")
    private String alertType;

    @Column(name = "distance")
    private String distance;

    @Column(name = "duration")
    private String duration;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "status")
    private String status;

    private AssetDAO assetDAO;

    private FenceDAO fenceDAO;

    private UserInfoDAO userInfoDAO;

    private AssetLocationDAO assetLocationDAO;

    @Override
    public String toString()
    {
        return "AlertsDAO{" +
                "time=" + time +
                ", clientCode='" + clientCode + '\'' +
                ", fenceCode='" + fenceCode + '\'' +
                ", assetCode='" + assetCode + '\'' +
                ", userCode='" + userCode + '\'' +
                ", alertCode=" + alertCode +
                ", alertType='" + alertType + '\'' +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status='" + status + '\'' +
                ", assetDAO=" + assetDAO +
                ", fenceDAO=" + fenceDAO +
                ", userInfoDAO=" + userInfoDAO +
                ", assetLocationDAO=" + assetLocationDAO +
                '}';
    }

    public Instant getTime()
    {
        return time;
    }

    public void setTime(Instant time)
    {
        this.time = time;
    }

    public String getClientCode()
    {
        return clientCode;
    }

    public void setClientCode(String clientCode)
    {
        this.clientCode = clientCode;
    }

    public String getFenceCode()
    {
        return fenceCode;
    }

    public void setFenceCode(String fenceCode)
    {
        this.fenceCode = fenceCode;
    }

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public Integer getAlertCode()
    {
        return alertCode;
    }

    public void setAlertCode(Integer alertCode)
    {
        this.alertCode = alertCode;
    }

    public String getAlertType()
    {
        return alertType;
    }

    public void setAlertType(String alertType)
    {
        this.alertType = alertType;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public AssetDAO getAssetDAO()
    {
        return assetDAO;
    }

    public void setAssetDAO(AssetDAO assetDAO)
    {
        this.assetDAO = assetDAO;
    }

    public FenceDAO getFenceDAO()
    {
        return fenceDAO;
    }

    public void setFenceDAO(FenceDAO fenceDAO)
    {
        this.fenceDAO = fenceDAO;
    }

    public UserInfoDAO getUserInfoDAO()
    {
        return userInfoDAO;
    }

    public void setUserInfoDAO(UserInfoDAO userInfoDAO)
    {
        this.userInfoDAO = userInfoDAO;
    }

    public AssetLocationDAO getAssetLocationDAO()
    {
        return assetLocationDAO;
    }

    public void setAssetLocationDAO(AssetLocationDAO assetLocationDAO)
    {
        this.assetLocationDAO = assetLocationDAO;
    }

    @Override
    public List<AlertsDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<AlertsDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (AlertsDAO alertsDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, alertsDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public AlertsDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, AlertsDAO daoToSave) throws Exception
    {
        try
        {
            daoToSave.setAlertCode(IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(null)));

            Point point = Point.measurement(Constants.DB_TABLE_NAME.ALERTS_DAO)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("client_code", daoToSave.getClientCode())
                    .tag("fence_code", daoToSave.getFenceCode())
                    .tag("asset_code", daoToSave.getAssetCode())
                    .tag("user_code", daoToSave.getUserCode())
                    .addField("alert_code", daoToSave.getAlertCode())
                    .addField("alert_type", daoToSave.getAlertType())
                    .addField("distance", StringUtils.hasText(daoToSave.getDistance()) ? daoToSave.getDistance() : "")
                    .addField("duration", StringUtils.hasText(daoToSave.getDuration()) ? daoToSave.getDuration() : "")
                    .addField("latitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLatitude(), Constants.NUMBER.SIX))
                    .addField("longitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLongitude(), Constants.NUMBER.SIX))
                    .addField("status", daoToSave.getStatus())
                    .build();

            IDAO.saveDAO(databaseInfoDAO, point);

            return daoToSave;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AlertsDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" from ");
            selectQuery.append(Constants.DB_TABLE_NAME.ALERTS_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" 1 = 1 ");

            if (searchCriteriaDTO != null)
            {
                if (StringUtils.hasText(searchCriteriaDTO.getStartDateUI()))
                {
                    selectQuery.append(" and time >= " + StringUtils.quote(searchCriteriaDTO.getStartDateUI()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getEndDateUI()))
                {
                    selectQuery.append(" and time <= " + StringUtils.quote(searchCriteriaDTO.getEndDateUI()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getClientCode()))
                {
                    selectQuery.append(" and client_code = " + StringUtils.quote(searchCriteriaDTO.getClientCode()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getFenceCode()))
                {
                    selectQuery.append(" and fence_code = " + StringUtils.quote(searchCriteriaDTO.getFenceCode()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getAssetCode()))
                {
                    selectQuery.append(" and asset_code = " + StringUtils.quote(searchCriteriaDTO.getAssetCode()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getAssetLocationStatus()))
                {
                    selectQuery.append(" and status = " + StringUtils.quote(searchCriteriaDTO.getAssetLocationStatus()));
                }

                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getAlertCode()))
                {
                    selectQuery.append(" and alert_code = " + Integer.parseInt(searchCriteriaDTO.getAlertCode()));
                }
            }

            List<AlertsDAO> alertsDAOs = IDAO.fetchDAOs(databaseInfoDAO, AlertsDAO.class, selectQuery.toString());
            if (alertsDAOs != null && !alertsDAOs.isEmpty())
            {
                Collections.sort(alertsDAOs);
            }
            return alertsDAOs;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AlertsDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<AlertsDAO> alertDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return alertDAOsList != null && !alertDAOsList.isEmpty() ? alertDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AlertsDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        return null;
    }

    @Override
    public String getSelectQuery(SearchCriteriaDTO searchCriteriaDTO)
    {
        StringBuilder selectQuery = new StringBuilder("SELECT ");
        selectQuery.append(" max(alert_code) as alertCode ");
        selectQuery.append(" from ");
        selectQuery.append(Constants.DB_TABLE_NAME.ALERTS_DAO);

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<AlertsDAO> daoListToDelete) throws Exception
    {
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, AlertsDAO daoToDelete) throws Exception
    {
        return false;
    }

    @Override
    public int compareTo(AlertsDAO that)
    {
        return that.alertCode - this.alertCode;
    }
}