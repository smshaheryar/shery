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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shaheryar Nadeem on 10/31/2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = Constants.DB_TABLE_NAME.ASSETS_INFO_DAO)
public class AssetDAO implements IDAO<AssetDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "client_code", tag = true)
    private String clientCode;

    @Column(name = "fence_code", tag = true)
    private String fenceCode;

    @Column(name = "is_active", tag = true)
    private String isActive;

    @Column(name = "user_code", tag = true)
    private String userCode;

    @Column(name = "asset_code")
    private Integer assetCode;

    @Column(name = "vin")
    private String vin;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    private String assetLocationStatus;

    private FenceDAO fenceDAO;

    private UserInfoDAO userInfoDAO;

    private List<AssetLocationDAO> assetLocationDAOsList;

    @Override
    public String toString()
    {
        return "AssetDAO{" +
                "time=" + time +
                ", clientCode='" + clientCode + '\'' +
                ", fenceCode='" + fenceCode + '\'' +
                ", isActive='" + isActive + '\'' +
                ", userCode='" + userCode + '\'' +
                ", assetCode=" + assetCode +
                ", vin='" + vin + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", assetLocationStatus='" + assetLocationStatus + '\'' +
                ", fenceDAO=" + fenceDAO +
                ", userInfoDAO=" + userInfoDAO +
                ", assetLocationDAOsList=" + assetLocationDAOsList +
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

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public Integer getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(Integer assetCode)
    {
        this.assetCode = assetCode;
    }

    public String getVin()
    {
        return vin;
    }

    public void setVin(String vin)
    {
        this.vin = vin;
    }

    public String getMake()
    {
        return make;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getAssetLocationStatus()
    {
        return assetLocationStatus;
    }

    public void setAssetLocationStatus(String assetLocationStatus)
    {
        this.assetLocationStatus = assetLocationStatus;
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

    public List<AssetLocationDAO> getAssetLocationDAOsList()
    {
        return assetLocationDAOsList;
    }

    public void setAssetLocationDAOsList(List<AssetLocationDAO> assetLocationDAOsList)
    {
        this.assetLocationDAOsList = assetLocationDAOsList;
    }

    @Override
    public List<AssetDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<AssetDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (AssetDAO assetDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, assetDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public AssetDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, AssetDAO daoToSave) throws Exception
    {
        try
        {
            Long time;
            Integer assetCode = daoToSave.getAssetCode();

            if (Utility.isNumberGreaterThanZero(assetCode))
            {
                time = daoToSave.getTime().toEpochMilli();
                assetCode = daoToSave.getAssetCode();
            }
            else
            {
                time = System.currentTimeMillis();
                assetCode = IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(null));
            }

            daoToSave.setAssetCode(assetCode);

            Point point = Point.measurement(Constants.DB_TABLE_NAME.ASSETS_INFO_DAO)
                    .time(time, TimeUnit.MILLISECONDS)
                    .tag("client_code", daoToSave.getClientCode())
                    .tag("fence_code", daoToSave.getFenceCode())
                    .tag("user_code", daoToSave.getUserCode())
                    .tag("is_active", daoToSave.getIsActive())
                    .addField("asset_code", daoToSave.getAssetCode())
                    .addField("vin", daoToSave.getVin())
                    .addField("make", daoToSave.getMake())
                    .addField("model", daoToSave.getModel())
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
    public List<AssetDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" from ");
            selectQuery.append(Constants.DB_TABLE_NAME.ASSETS_INFO_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" 1 = 1 ");

            if (searchCriteriaDTO != null)
            {
                if (Utility.isNumberValid(searchCriteriaDTO.getClientCode()))
                {
                    selectQuery.append(" and client_code = " + StringUtils.quote(searchCriteriaDTO.getClientCode()));
                }

                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getFenceCode()))
                {
                    selectQuery.append(" and fence_code = " + StringUtils.quote(searchCriteriaDTO.getFenceCode()));
                }

                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getAssetCode()))
                {
                    selectQuery.append(" and asset_code = " + Integer.parseInt(searchCriteriaDTO.getAssetCode()));
                }
            }

            return IDAO.fetchDAOs(databaseInfoDAO, AssetDAO.class, selectQuery.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AssetDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<AssetDAO> assetDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return assetDAOsList != null && !assetDAOsList.isEmpty() ? assetDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public AssetDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        try
        {
            if (code != null && StringUtils.hasText(code.toString()))
            {
                SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
                searchCriteriaDTO.setAssetCode(code.toString());

                return fetchDAO(databaseInfoDAO, searchCriteriaDTO);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSelectQuery(SearchCriteriaDTO searchCriteriaDTO)
    {
        StringBuilder selectQuery = new StringBuilder(" SELECT ");
        selectQuery.append(" max(asset_code) as assetCode ");
        selectQuery.append(" FROM ");
        selectQuery.append(Constants.DB_TABLE_NAME.ASSETS_INFO_DAO);
        selectQuery.append(" where ");
        selectQuery.append(" is_active = 'True' ");

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<AssetDAO> daoListToDelete) throws Exception
    {
        if (daoListToDelete != null && !daoListToDelete.isEmpty())
        {
            for (AssetDAO assetDAO : daoListToDelete)
            {
                deleteDAO(databaseInfoDAO, assetDAO);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, AssetDAO daoToDelete) throws Exception
    {
        try
        {
            StringBuilder deleteQuery = new StringBuilder(" delete ");
            deleteQuery.append(" from ");
            deleteQuery.append(Constants.DB_TABLE_NAME.ASSETS_INFO_DAO);
            deleteQuery.append(" where ");
            deleteQuery.append(" time = " ).append(StringUtils.quote(String.valueOf(daoToDelete.getTime())));

            List<AssetDAO> deleteDAOsList = IDAO.fetchDAOs(databaseInfoDAO, AssetDAO.class, deleteQuery.toString());
            return deleteDAOsList == null || deleteDAOsList.isEmpty();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}