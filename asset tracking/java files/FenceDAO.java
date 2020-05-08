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


@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = Constants.DB_TABLE_NAME.FENCE_DAO)
public class FenceDAO implements IDAO<FenceDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "client_code", tag = true)
    private String clientCode;

    @Column(name = "is_active", tag = true)
    private String isActive;

    @Column(name = "master_fence_code", tag = true)
    private String masterFenceCode;

    @Column(name = "created_by", tag = true)
    private String createdBy;

    @Column(name = "fence_code")
    private Integer fenceCode;

    @Column(name = "fence_name")
    private String fenceName;

    @Column(name = "address")
    private String address;

    @Column(name = "total_no_of_data_points")
    private int totalNoOfDataPoints;

    @Column(name = "fenceDetailDAOsList")
    private List<FenceDetailDAO> fenceDetailDAOsList;

    private List<AssetDAO> assetDAOsList;

    private UserInfoDAO userInfoDAO;

    @Override
    public String toString()
    {
        return "FenceDAO{" +
                "time=" + time +
                ", clientCode='" + clientCode + '\'' +
                ", isActive='" + isActive + '\'' +
                ", masterFenceCode='" + masterFenceCode + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", fenceCode=" + fenceCode +
                ", fenceName='" + fenceName + '\'' +
                ", address='" + address + '\'' +
                ", totalNoOfDataPoints=" + totalNoOfDataPoints +
                ", fenceDetailDAOsList=" + fenceDetailDAOsList +
                ", assetDAOsList=" + assetDAOsList +
                ", userInfoDAO=" + userInfoDAO +
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

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getMasterFenceCode()
    {
        return masterFenceCode;
    }

    public void setMasterFenceCode(String masterFenceCode)
    {
        this.masterFenceCode = masterFenceCode;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public Integer getFenceCode()
    {
        return fenceCode;
    }

    public void setFenceCode(Integer fenceCode)
    {
        this.fenceCode = fenceCode;
    }

    public String getFenceName()
    {
        return fenceName;
    }

    public void setFenceName(String fenceName)
    {
        this.fenceName = fenceName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getTotalNoOfDataPoints()
    {
        return totalNoOfDataPoints;
    }

    public void setTotalNoOfDataPoints(int totalNoOfDataPoints)
    {
        this.totalNoOfDataPoints = totalNoOfDataPoints;
    }

    public List<FenceDetailDAO> getFenceDetailDAOsList()
    {
        return fenceDetailDAOsList;
    }

    public void setFenceDetailDAOsList(List<FenceDetailDAO> fenceDetailDAOsList)
    {
        this.fenceDetailDAOsList = fenceDetailDAOsList;
    }

    public List<AssetDAO> getAssetDAOsList()
    {
        return assetDAOsList;
    }

    public void setAssetDAOsList(List<AssetDAO> assetDAOsList)
    {
        this.assetDAOsList = assetDAOsList;
    }

    public UserInfoDAO getUserInfoDAO()
    {
        return userInfoDAO;
    }

    public void setUserInfoDAO(UserInfoDAO userInfoDAO)
    {
        this.userInfoDAO = userInfoDAO;
    }

    @Override
    public List<FenceDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<FenceDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (FenceDAO fenceDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, fenceDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public FenceDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, FenceDAO daoToSave) throws Exception
    {
        try
        {
            daoToSave.setFenceCode(IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(null)));

            Point point = Point.measurement(Constants.DB_TABLE_NAME.FENCE_DAO)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("client_code", daoToSave.getClientCode())
                    .tag("is_active", daoToSave.getIsActive())
                    .tag("master_fence_code", daoToSave.getMasterFenceCode())
                    .tag("created_by", daoToSave.getCreatedBy())
                    .addField("fence_code", daoToSave.getFenceCode())
                    .addField("fence_name", daoToSave.getFenceName())
                    .addField("address", daoToSave.getAddress())
                    .addField("total_no_of_data_points", daoToSave.getTotalNoOfDataPoints())
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
    public List<FenceDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" from ");
            selectQuery.append(Constants.DB_TABLE_NAME.FENCE_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" 1 = 1 ");

            if (searchCriteriaDTO != null)
            {
                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getClientCode()))
                {
                    selectQuery.append(" and client_code = " + StringUtils.quote(searchCriteriaDTO.getClientCode()));
                }

                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getFenceCode()))
                {
                    selectQuery.append(" and fence_code = " + searchCriteriaDTO.getFenceCode());
                }
            }

            return IDAO.fetchDAOs(databaseInfoDAO, FenceDAO.class, selectQuery.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FenceDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<FenceDAO> fenceDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return fenceDAOsList != null && !fenceDAOsList.isEmpty() ? fenceDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FenceDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        try
        {
            if (code != null && StringUtils.hasText(code.toString()))
            {
                SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
                searchCriteriaDTO.setFenceCode(code.toString());

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
        StringBuilder selectQuery = new StringBuilder(" select ");
        selectQuery.append(" max(fence_code) as fenceCode ");
        selectQuery.append(" from ");
        selectQuery.append(Constants.DB_TABLE_NAME.FENCE_DAO);
        selectQuery.append(" where ");
        selectQuery.append(" is_active = 'True' ");

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<FenceDAO> daoListToDelete) throws Exception
    {
        if (daoListToDelete != null && !daoListToDelete.isEmpty())
        {
            for (FenceDAO fenceDAO : daoListToDelete)
            {
                deleteDAO(databaseInfoDAO, fenceDAO);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, FenceDAO daoToDelete) throws Exception
    {
        try
        {
            StringBuilder deleteQuery = new StringBuilder(" delete ");
            deleteQuery.append(" from ");
            deleteQuery.append(Constants.DB_TABLE_NAME.FENCE_DAO);
            deleteQuery.append(" where ");
            deleteQuery.append(" fence_code = ").append(StringUtils.quote(daoToDelete.getFenceCode().toString()));

            List<FenceDAO> deleteDAOsList = IDAO.fetchDAOs(databaseInfoDAO, FenceDAO.class, deleteQuery.toString());
            return deleteDAOsList == null || deleteDAOsList.isEmpty();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}