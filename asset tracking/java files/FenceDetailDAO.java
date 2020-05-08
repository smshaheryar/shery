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
@Measurement(name = Constants.DB_TABLE_NAME.FENCE_DETAIL_DAO)
public class FenceDetailDAO implements IDAO<FenceDetailDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "fence_code", tag = true)
    private String fenceCode;

    @Column(name = "fence_detail_code")
    private Integer fenceDetailCode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "serial_no")
    private Integer serialNo;

    @Override
    public String toString()
    {
        return "FenceDetailDAO{" +
                "time=" + time +
                ", fenceCode='" + fenceCode + '\'' +
                ", fenceDetailCode=" + fenceDetailCode +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", serialNo=" + serialNo +
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

    public String getFenceCode()
    {
        return fenceCode;
    }

    public void setFenceCode(String fenceCode)
    {
        this.fenceCode = fenceCode;
    }

    public Integer getFenceDetailCode()
    {
        return fenceDetailCode;
    }

    public void setFenceDetailCode(Integer fenceDetailCode)
    {
        this.fenceDetailCode = fenceDetailCode;
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

    public Integer getSerialNo()
    {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo)
    {
        this.serialNo = serialNo;
    }

    @Override
    public List<FenceDetailDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<FenceDetailDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (FenceDetailDAO fenceDetailDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, fenceDetailDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public FenceDetailDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, FenceDetailDAO daoToSave) throws Exception
    {
        try
        {
            daoToSave.setFenceDetailCode(IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(null)));

            Point point = Point.measurement(Constants.DB_TABLE_NAME.FENCE_DETAIL_DAO)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("fence_code", daoToSave.getFenceCode())
                    .addField("fence_detail_code", daoToSave.getFenceDetailCode())
                    .addField("latitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLatitude(), Constants.NUMBER.SIX))
                    .addField("longitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLongitude(), Constants.NUMBER.SIX))
                    .addField("serial_no", daoToSave.getSerialNo())
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
    public List<FenceDetailDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" FROM ");
            selectQuery.append(Constants.DB_TABLE_NAME.FENCE_DETAIL_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" 1 = 1 ");

            if (searchCriteriaDTO != null)
            {
                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getFenceCode()))
                {
                    selectQuery.append(" and fence_code = ").append(StringUtils.quote(searchCriteriaDTO.getFenceCode()));
                }
            }

            return IDAO.fetchDAOs(databaseInfoDAO, FenceDetailDAO.class, selectQuery.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FenceDetailDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<FenceDetailDAO> fenceDetailDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return fenceDetailDAOsList != null && !fenceDetailDAOsList.isEmpty() ? fenceDetailDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FenceDetailDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        return null;
    }

    @Override
    public String getSelectQuery(SearchCriteriaDTO searchCriteriaDTO)
    {
        StringBuilder selectQuery = new StringBuilder(" select ");
        selectQuery.append(" max(fence_detail_code) as fenceDetailCode ");
        selectQuery.append(" from ");
        selectQuery.append(Constants.DB_TABLE_NAME.FENCE_DETAIL_DAO);
        selectQuery.append(" where ");
        selectQuery.append(" 1 = 1 ");

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<FenceDetailDAO> daoListToDelete) throws Exception
    {
        if (daoListToDelete != null && !daoListToDelete.isEmpty())
        {
            for (FenceDetailDAO fenceDetailDAO : daoListToDelete)
            {
                deleteDAO(databaseInfoDAO, fenceDetailDAO);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, FenceDetailDAO daoToDelete) throws Exception
    {
        try
        {
            StringBuilder deleteQuery = new StringBuilder(" delete ");
            deleteQuery.append(" from ");
            deleteQuery.append(Constants.DB_TABLE_NAME.FENCE_DETAIL_DAO);
            deleteQuery.append(" where ");
            deleteQuery.append(" fence_code = ").append(StringUtils.quote(daoToDelete.getFenceCode()));

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