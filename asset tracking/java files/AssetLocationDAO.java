package com.conure.sam.entities;

import com.conure.sam.dto.SearchCriteriaDTO;
import com.conure.sam.utilities.Constants;
import com.conure.sam.utilities.Utility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = Constants.DB_TABLE_NAME.ASSETS_LOCATION_INFO_DAO)
public class AssetLocationDAO implements IDAO<AssetLocationDAO>, Comparable<AssetLocationDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "asset_code", tag = true)
    private String assetCode;

    @Column(name = "alert_code", tag = true)
    private String alertCode;

    @Column(name = "user_code", tag = true)
    private String userCode;

    @Column(name = "serial_no")
    private Integer serialNo;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "asset_location_status")
    private String assetLocationStatus;

    @Column(name = "alertsDAO")
    private AlertsDAO alertsDAO;

    @Override
    public String toString()
    {
        return "AssetLocationDAO{" +
                "time=" + time +
                ", assetCode='" + assetCode + '\'' +
                ", alertCode='" + alertCode + '\'' +
                ", userCode='" + userCode + '\'' +
                ", serialNo=" + serialNo +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", assetLocationStatus='" + assetLocationStatus + '\'' +
                ", alertsDAO=" + alertsDAO +
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

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    public String getAlertCode()
    {
        return alertCode;
    }

    public void setAlertCode(String alertCode)
    {
        this.alertCode = alertCode;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public Integer getSerialNo()
    {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo)
    {
        this.serialNo = serialNo;
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

    public String getAssetLocationStatus()
    {
        return assetLocationStatus;
    }

    public void setAssetLocationStatus(String assetLocationStatus)
    {
        this.assetLocationStatus = assetLocationStatus;
    }

    public AlertsDAO getAlertsDAO()
    {
        return alertsDAO;
    }

    public void setAlertsDAO(AlertsDAO alertsDAO)
    {
        this.alertsDAO = alertsDAO;
    }

    @Override
    public List<AssetLocationDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<AssetLocationDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (AssetLocationDAO assetLocationDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, assetLocationDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public AssetLocationDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, AssetLocationDAO daoToSave) throws Exception
    {
        try
        {
            SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
            searchCriteriaDTO.setAssetCode(daoToSave.getAssetCode());

            daoToSave.setSerialNo(IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(searchCriteriaDTO)));

            Point point = Point.measurement(Constants.DB_TABLE_NAME.ASSETS_LOCATION_INFO_DAO)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("asset_code", daoToSave.getAssetCode())
                    .tag("alert_code", daoToSave.getAlertCode())
                    .tag("user_code", daoToSave.getUserCode())
                    .addField("serial_no", daoToSave.getSerialNo())
                    .addField("latitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLatitude(), Constants.NUMBER.SIX))
                    .addField("longitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLongitude(), Constants.NUMBER.SIX))
                    .addField("asset_location_status", daoToSave.getAssetLocationStatus())
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
    public List<AssetLocationDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" from ");
            selectQuery.append(Constants.DB_TABLE_NAME.ASSETS_LOCATION_INFO_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" asset_code = ").append(StringUtils.quote(searchCriteriaDTO.getAssetCode()));

            if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getAlertCode()))
            {
                selectQuery.append(" and alert_code = ").append(StringUtils.quote(searchCriteriaDTO.getAlertCode()));
            }

            if (StringUtils.hasText(searchCriteriaDTO.getAssetLocationStatus()))
            {
                selectQuery.append(" and asset_location_status = ").append(StringUtils.quote(searchCriteriaDTO.getAssetLocationStatus()));
            }

            List<AssetLocationDAO> assetLocationDAOsList = IDAO.fetchDAOs(databaseInfoDAO, AssetLocationDAO.class, selectQuery.toString());
            if (assetLocationDAOsList != null && !assetLocationDAOsList.isEmpty())
            {
                Collections.sort(assetLocationDAOsList);
            }
            return assetLocationDAOsList;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AssetLocationDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<AssetLocationDAO> assetDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return assetDAOsList != null && !assetDAOsList.isEmpty() ? assetDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AssetLocationDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        return null;
    }

    @Override
    public String getSelectQuery(SearchCriteriaDTO searchCriteriaDTO)
    {
        StringBuilder selectQuery = new StringBuilder(" select ");
        selectQuery.append(" max(serial_no) as serialNo ");
        selectQuery.append(" from ");
        selectQuery.append(Constants.DB_TABLE_NAME.ASSETS_LOCATION_INFO_DAO);
        selectQuery.append(" where ");
        selectQuery.append(" asset_code = " ).append(StringUtils.quote(searchCriteriaDTO.getAssetCode()));

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<AssetLocationDAO> daoListToDelete) throws Exception
    {
        if (daoListToDelete != null && !daoListToDelete.isEmpty())
        {
            for (AssetLocationDAO assetLocationDAO : daoListToDelete)
            {
                deleteDAO(databaseInfoDAO, assetLocationDAO);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, AssetLocationDAO daoToDelete) throws Exception
    {
        try
        {
            StringBuilder deleteQuery = new StringBuilder(" delete ");
            deleteQuery.append(" from ");
            deleteQuery.append(Constants.DB_TABLE_NAME.ASSETS_LOCATION_INFO_DAO);
            deleteQuery.append(" where ");
            deleteQuery.append(" asset_code = " ).append(StringUtils.quote(daoToDelete.getAssetCode()));

            List<AssetLocationDAO> deleteDAOsList = IDAO.fetchDAOs(databaseInfoDAO, AssetLocationDAO.class, deleteQuery.toString());
            return deleteDAOsList == null || deleteDAOsList.isEmpty();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public int compareTo(AssetLocationDAO that)
    {
        return that.serialNo - this.serialNo;
    }
}