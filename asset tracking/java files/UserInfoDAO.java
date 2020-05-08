package com.conure.sam.entities;

import com.conure.sam.dto.SearchCriteriaDTO;
import com.conure.sam.utilities.Constants;
import com.conure.sam.utilities.Crypto;
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
 * Created by Shaheryar Nadeem on 11/13/2018.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = Constants.DB_TABLE_NAME.USER_INFO_DAO)
public class UserInfoDAO implements IDAO<UserInfoDAO>
{
    @Column(name = "time")
    private Instant time;

    @Column(name = "client_code", tag = true)
    private String clientCode;

    @Column(name="is_active", tag = true)
    private String isActive;

    @Column(name = "user_role", tag = true)
    private String userRole;

    @Column(name = "user_code")
    private Integer userCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "login_email")
    private String loginEmail;

    @Column(name = "password")
    private String password;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "city_code")
    private Integer cityCode;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "message")
    private String message;

    @Column(name = "full_name")
    private String fullName;

    private CountryInfoDAO countryInfoDAO;
    @Override
    public String toString()
    {
        return "UserInfoDAO{" +
                "time=" + time +
                ", clientCode='" + clientCode + '\'' +
                ", isActive='" + isActive + '\'' +
                ", userRole='" + userRole + '\'' +
                ", userCode=" + userCode +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loginEmail='" + loginEmail + '\'' +
                ", password='" + password + '\'' +
                ", createdBy=" + createdBy +
                ", cityCode=" + cityCode +
                ", contactNo='" + contactNo + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", message='" + message + '\'' +
                ", fullName='" + fullName + '\'' +
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

    public String getUserRole()
    {
        return userRole;
    }

    public void setUserRole(String userRole)
    {
        this.userRole = userRole;
    }

    public Integer getUserCode()
    {
        return userCode;
    }

    public void setUserCode(Integer userCode)
    {
        this.userCode = userCode;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getLoginEmail()
    {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail)
    {
        this.loginEmail = loginEmail;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy)
    {
        this.createdBy = createdBy;
    }

    public Integer getCityCode()
    {
        return cityCode;
    }

    public void setCityCode(Integer cityCode)
    {
        this.cityCode = cityCode;
    }

    public String getContactNo()
    {
        return contactNo;
    }

    public void setContactNo(String contactNo)
    {
        this.contactNo = contactNo;
    }

    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public String getAddress2()
    {
        return address2;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getFullName()
    {
        return this.firstName + " " + this.lastName;
    }

    public CountryInfoDAO getCountryInfoDAO()
    {
        return countryInfoDAO;
    }

    public void setCountryInfoDAO(CountryInfoDAO countryInfoDAO)
    {
        this.countryInfoDAO = countryInfoDAO;
    }

    @Override
    public List<UserInfoDAO> saveDAOs(DatabaseInfoDAO databaseInfoDAO, List<UserInfoDAO> daoListToSave) throws Exception
    {
        if (daoListToSave != null && !daoListToSave.isEmpty())
        {
            for (UserInfoDAO userInfoDAO : daoListToSave)
            {
                saveDAO(databaseInfoDAO, userInfoDAO);
            }
        }
        return daoListToSave;
    }

    @Override
    public UserInfoDAO saveDAO(DatabaseInfoDAO databaseInfoDAO, UserInfoDAO daoToSave) throws Exception
    {
        try
        {
            daoToSave.setUserCode(IDAO.getDAONewCode(databaseInfoDAO, getSelectQuery(null)));

            Point point = Point.measurement(Constants.DB_TABLE_NAME.USER_INFO_DAO)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("client_code", daoToSave.getClientCode())
                    .tag("user_role", daoToSave.getUserRole())
                    .tag("is_active", daoToSave.getIsActive())
                    .addField("user_code", daoToSave.getUserCode())
                    .addField("created_by", daoToSave.getCreatedBy())
                    .addField("login_email", daoToSave.getLoginEmail())
                    .addField("password", Crypto.encrypt(daoToSave.getPassword()))
                    .addField("first_name", daoToSave.getFirstName())
                    .addField("last_name", daoToSave.getLastName())
                    .addField("city_code", daoToSave.getCityCode())
                    .addField("address1", daoToSave.getAddress1())
                    .addField("address2", daoToSave.getAddress2())
                    .addField("contact_no", daoToSave.getContactNo())
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
    public List<UserInfoDAO> fetchDAOs(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            StringBuilder selectQuery = new StringBuilder(" select ");
            selectQuery.append(" * ");
            selectQuery.append(" from ");
            selectQuery.append(Constants.DB_TABLE_NAME.USER_INFO_DAO);
            selectQuery.append(" where ");
            selectQuery.append(" 1 = 1 ");

            if (searchCriteriaDTO != null)
            {
                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getClientCode()))
                {
                    selectQuery.append(" and client_code = " + StringUtils.quote(searchCriteriaDTO.getClientCode()));
                }

                if (Utility.isNumberGreaterThanZero(searchCriteriaDTO.getUserCode()))
                {
                    selectQuery.append(" and user_code = " + Integer.parseInt(searchCriteriaDTO.getUserCode()));
                }

                if (StringUtils.hasText(searchCriteriaDTO.getLoginEmail()))
                {
                    selectQuery.append(" and login_email = " + StringUtils.quote(searchCriteriaDTO.getLoginEmail()));
                }
            }

            List<UserInfoDAO> userInfoDAOs = IDAO.fetchDAOs(databaseInfoDAO, UserInfoDAO.class, selectQuery.toString());

            if (userInfoDAOs != null && !userInfoDAOs.isEmpty())
            {
                for (UserInfoDAO userInfoDAO : userInfoDAOs)
                {
                    userInfoDAO.setPassword(Crypto.decrypt(userInfoDAO.getPassword()));
                }
            }

            return userInfoDAOs;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public UserInfoDAO fetchDAO(DatabaseInfoDAO databaseInfoDAO, SearchCriteriaDTO searchCriteriaDTO)
    {
        try
        {
            List<UserInfoDAO> userDAOsList = fetchDAOs(databaseInfoDAO, searchCriteriaDTO);
            return userDAOsList != null && !userDAOsList.isEmpty() ? userDAOsList.get(Constants.NUMBER.ZERO) : null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public UserInfoDAO fetchDAOByCode(DatabaseInfoDAO databaseInfoDAO, Object code)
    {
        try
        {
            if (code != null && StringUtils.hasText(code.toString()))
            {
                SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
                searchCriteriaDTO.setUserCode(code.toString());

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
        StringBuilder selectQuery = new StringBuilder(" Select ");
        selectQuery.append(" max(user_code) as userCode ");
        selectQuery.append(" from ");
        selectQuery.append(Constants.DB_TABLE_NAME.USER_INFO_DAO);
        selectQuery.append(" where ");
        selectQuery.append(" is_active = 'True' ");

        return selectQuery.toString();
    }

    @Override
    public boolean deleteDAOs(DatabaseInfoDAO databaseInfoDAO, List<UserInfoDAO> daoListToDelete) throws Exception
    {
        if (daoListToDelete != null && !daoListToDelete.isEmpty())
        {
            for (UserInfoDAO userInfoDAO : daoListToDelete)
            {
                deleteDAO(databaseInfoDAO, userInfoDAO);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDAO(DatabaseInfoDAO databaseInfoDAO, UserInfoDAO daoToDelete) throws Exception
    {
        try
        {
            StringBuilder deleteQuery = new StringBuilder(" delete ");
            deleteQuery.append(" from ");
            deleteQuery.append(Constants.DB_TABLE_NAME.USER_INFO_DAO);
            deleteQuery.append(" where ");
            deleteQuery.append(" time = " + StringUtils.quote(daoToDelete.getTime().toString()));

            List<UserInfoDAO> deleteDAOsList = IDAO.fetchDAOs(databaseInfoDAO, UserInfoDAO.class, deleteQuery.toString());
            return deleteDAOsList == null || deleteDAOsList.isEmpty();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}