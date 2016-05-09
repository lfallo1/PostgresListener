package com.mygene2.postgreslistener.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mygene2.postgreslistener.model.Account;

public class AccountRowMapper {

    public static final String ACCOUNT_ID_COL = "id";
    public static final String EMAIL_COL = "email";
    public static final String USERNAME_COL = "username";
    public static final String FIRSTNAME_COL = "firstname";
    public static final String LASTNAME_COL = "lastname";
    public static final String DATE_CREATED_COL = "datecreated";
    public static final String USERROLEID_COL = "userroleid";

    public Account mapRow(ResultSet rs) throws SQLException {

        Account account = new Account();
        account.setId(rs.getInt(ACCOUNT_ID_COL));
        account.setEmail(rs.getString(EMAIL_COL));
        account.setUsername(rs.getString(USERNAME_COL));
        account.setFirstName(rs.getString(FIRSTNAME_COL));
        account.setLastName(rs.getString(LASTNAME_COL));
        account.setDateCreated(rs.getDate(DATE_CREATED_COL));
        
        Integer role = rs.getInt(USERROLEID_COL);
        account.setIsClinician(role != 1);

        return account;
    }
}
