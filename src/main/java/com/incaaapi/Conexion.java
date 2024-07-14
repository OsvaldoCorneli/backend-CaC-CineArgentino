package com.incaaapi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;  
import java.sql.SQLException;
import java.util.Properties;


public class Conexion {

    
    public static Properties LoadProps(){
        var properties = new Properties();

        try (var stream = Conexion.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (stream != null) {
                properties.load(stream);
            } else {
                System.out.println("No se pudo cargar el archivo config.properties");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }

    private Connection connection;

    public Conexion() {

    var properties = LoadProps();   
        
    String url = properties.getProperty("database");
    String username = properties.getProperty("username");
    String password = properties.getProperty("password");


    try {
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(url, username, password);
        


    }  catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    }



    public Connection getConnection() {
        return connection;
    }

    // Método para cerrar la conexión
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
