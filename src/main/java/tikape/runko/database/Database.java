/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Pelle
 */
public class Database {
    private String osoite;
    
    public Database (String osoite) {
        this.osoite = osoite;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.osoite);
    }
}