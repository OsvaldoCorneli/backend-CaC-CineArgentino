package com.incaaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/peliculas")
public class ControllerPeliculas extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Configurar cabeceras CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConnection();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Pelicula newPelicula = mapper.readValue(request.getInputStream(), Pelicula.class);

            String query = "INSERT INTO peliculas (title, estreno, duracion , sinopsis, image, elenco, director, trailer, wikilink) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, newPelicula.getTitle());
            statement.setString(2, newPelicula.getEstreno());
            statement.setString(3, newPelicula.getDuracion());
            statement.setString(4, newPelicula.getSinopsis());
            statement.setString(5, newPelicula.getImage());
            statement.setString(6, newPelicula.getElenco());
            statement.setString(7, newPelicula.getDirector());
            statement.setString(8, newPelicula.getImage());
            statement.setString(9, newPelicula.getWikiLink());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                Long idPeli = rs.getLong(1);
                String titlemovie = newPelicula.getTitle();

                String mensaje = "La pelicula" + titlemovie + " con el ID " + idPeli + " Fue agregada correctamente";

                response.setContentType("application/json");
                String json = mapper.writeValueAsString(mensaje);
                response.getWriter().write(json);
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            conexion.close();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Configurar cabeceras CORS

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Conexion conexion = new Conexion();
        Connection conn = conexion.getConnection();

        try {
            if (conn != null) {
                String query = "SELECT * FROM peliculas";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                List<Pelicula> peliculas = new ArrayList<>();
                
                while (resultSet.next()) {
                    Pelicula pelicula = new Pelicula(
                            resultSet.getInt("id_pelicula"),
                            resultSet.getString("title"),
                            resultSet.getString("estreno"),
                            resultSet.getString("duracion"),
                            resultSet.getString("sinopsis"),
                            resultSet.getString("image"),
                            resultSet.getString("elenco"),
                            resultSet.getString("director"),
                            resultSet.getString("trailer"),
                            resultSet.getString("wikiLink"));
                    peliculas.add(pelicula);
                }
                
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(peliculas);

                response.setContentType("application/json");
                response.getWriter().write(json);
            } else {
                // Manejo de error si conn es null
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error: No se pudo establecer la conexión a la base de datos.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            conexion.close();
        }
    }

   
}
