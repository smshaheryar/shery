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
@Measurement(name = "country_info")
public class CountryInfoDAO implements IDAO<CountryInfoDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "country", tag = true)
    private String countryName;

    @Column(name = "county", tag = true)
    private String countyName;

    @Column(name = "state", tag = true)
    private String stateName;

    @Column(name = "short", tag = true)
    private String shortCountryName;

    @Column(name = "code")
    private Integer cityCode;

    @Column(name = "city")
    private String cityName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Override
    public String toString()
    {
        return "CountryInfoDAO{" +
                "time=" + time +
                ", countryName='" + countryName + '\'' +
                ", countyName='" + countyName + '\'' +
                ", stateName='" + stateName + '\'' +
                ", shortCountryName='" + shortCountryName + '\'' +
                ", code=" + cityCode +
                ", cityName='" + cityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
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

    public String getCountryName()
    {
        return countryName;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    public String getCountyName()
    {
        return countyName;
    }

    public void setCountyName(String countyName)
    {
        this.countyName = countyName;
    }

    public String getStateName()
    {
        return stateName;
    }

    public void setStateName(String stateName)
    {
        this.stateName = stateName;
    }

    public String getShortCountryName()
    {
        return shortCountryName;
    }

    public void setShortCountryName(String shortCountryName)
    {
        this.shortCountryName = shortCountryName;
    }

    public Integer getCityCode()
    {
        return cityCode;
    }

    public void setCityCode(Integer cityCode)
    {
        this.cityCode = cityCode;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
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

    @Override
    public List<CountryInfoDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<CountryInfoDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (CountryInfoDAO countryInfoDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, countryInfoDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public CountryInfoDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, CountryInfoDAO daoToSave) throws Exception
    {
        try
        {
            daoToSave.setCityCode(IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(null)));

            Point point = Point.measurement(Constants.DB_TABLE_NAME.COUNTRY_INFO_DAO)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("country", daoToSave.getCountryName())
                    .tag("county", daoToSave.getCountyName())
                    .tag("short", daoToSave.getShortCountryName())
                    .tag("state", daoToSave.getStateName())
                    .addField("code", daoToSave.getCityCode())
                    .addField("city", daoToSave.getCityName())
                    .addField("latitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLatitude(), Constants.NUMBER.SIX))
                    .addField("longitude", Utility.roundingOffDoubleToDecimal(daoToSave.getLongitude(), Constants.NUMBER.SIX))
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
    public List<CountryInfoDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" from ");
            selectQuery.append(Constants.DB_TABLE_NAME.COUNTRY_INFO_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" 1 = 1 ");

            if (searchCriteriaDTO != null)
            {
                if (StringUtils.hasText(searchCriteriaDTO.getCityCode()))
                {
                    selectQuery.append(" and code = " + searchCriteriaDTO.getCityCode());
                }

                if (StringUtils.hasText(searchCriteriaDTO.getCountryName()))
                {
                    selectQuery.append(" and country = " + StringUtils.quote(searchCriteriaDTO.getCountryName()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getStateName()))
                {
                    selectQuery.append(" and state = " + StringUtils.quote(searchCriteriaDTO.getStateName()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getCityName()))
                {
                    selectQuery.append(" and city = " + StringUtils.quote(searchCriteriaDTO.getCityName()));
                }
            }
            return IDAO.fetchDAOs(databaseInfoDAO, CountryInfoDAO.class, selectQuery.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public CountryInfoDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<CountryInfoDAO> countryInfoDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return countryInfoDAOsList != null && !countryInfoDAOsList.isEmpty() ? countryInfoDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public CountryInfoDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        try
        {
            if (code != null && StringUtils.hasText(code.toString()))
            {
                SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
                searchCriteriaDTO.setCityCode(code.toString());

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
        selectQuery.append(" max(city_code) as cityCode ");
        selectQuery.append(" from ");
        selectQuery.append(Constants.DB_TABLE_NAME.COUNTRY_INFO_DAO);

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<CountryInfoDAO> daoListToDelete) throws Exception
    {
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, CountryInfoDAO daoToDelete) throws Exception
    {
        return false;
    }
}