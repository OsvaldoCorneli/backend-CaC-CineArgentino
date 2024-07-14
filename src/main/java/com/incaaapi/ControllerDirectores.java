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

@WebServlet("/directores")
public class ControllerDirectores extends HttpServlet {

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
            Persona newDirector = mapper.readValue(request.getInputStream(), Persona.class);

            String query = "INSERT INTO directores (name, description, wikilink, image) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, newDirector.getName());
            statement.setString(2, newDirector.getDescription());
            statement.setString(3, newDirector.getwikiLink());
            statement.setString(4, newDirector.getImage());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                Long iddirec = rs.getLong(1);
                String name = newDirector.getName();

                String mensaje = "El director o directora " + name + " con el ID " + iddirec
                        + " Fue agregado/a correctamente";

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
                String query = "SELECT * FROM directores";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                List<Persona> directores = new ArrayList<>();

                while (resultSet.next()) {
                    Persona director = new Persona(
                            resultSet.getInt("id_director"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getString("wikiLink"),
                            resultSet.getString("image"));
                    directores.add(director);
                }

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(directores);

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

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        // Obtener el parámetro 'id_director' de la solicitud
        String IdDirector = request.getParameter("id_director");

        // Verificar que el parámetro no sea nulo y sea un número válido
        if (IdDirector == null || !IdDirector.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid id_director parameter");
            return;
        }
        int directorid = Integer.parseInt(IdDirector);

        String deleteQuery = "DELETE FROM directores WHERE id_director = ?";

        try (Connection conn = new Conexion().getConnection();
                PreparedStatement statement = conn.prepareStatement(deleteQuery)) {

            // Establecer el valor del parámetro en la consulta
            statement.setInt(1, directorid);
            int rowsAffected = statement.executeUpdate();

            // Verificar cuántas filas fueron afectadas
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter()
                        .write("El director o directora con el ID " + directorid + " fue eliminado/a correctamente");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("No records found to delete");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Configurar CORS para permitir solicitudes desde cualquier origen
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Obtener el ID desde el parametro
        String directorID = request.getParameter("id_director");
        if (directorID == null || !directorID.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID de director/a no válido");
            return;
        }

        // Obtener los datos del cuerpo de la solicitud JSON
        ObjectMapper mapper = new ObjectMapper();
        Persona director = mapper.readValue(request.getReader(), Persona.class);

        if (director.getName() == null || director.getDescription() == null || director.getwikiLink() == null
                || director.getImage() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Todos los campos (name, description, wikiLink, image) son requeridos");
            return;
        }

        // Construir la consulta SQL para la actualización
        String updateQuery = "UPDATE directores SET name = ?, description = ?, wikiLink = ?, image = ? WHERE id_director = ?";

        try (Connection conn = new Conexion().getConnection();
                PreparedStatement statement = conn.prepareStatement(updateQuery)) {

            statement.setString(1, director.getName());
            statement.setString(2, director.getDescription());
            statement.setString(3, director.getwikiLink());
            statement.setString(4, director.getImage());
            statement.setInt(5, Integer.parseInt(directorID));

            int responseActualizacion = statement.executeUpdate();

            if (responseActualizacion > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter()
                        .write("El director o directora con el ID " + directorID + " fue actualizado/a correctamente");
            } else {

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("No se encontró ningun director/a con el ID especificado");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al actualizar el o la directora en la base de datos: " + e.getMessage());
        }
    }
}
