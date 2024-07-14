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


@WebServlet("/actrices")

public class ControllerActrices extends HttpServlet {

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
            Persona newActriz = mapper.readValue(request.getInputStream(), Persona.class);

            String query = "INSERT INTO actrices (name, description, wikilink, image) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, newActriz.getName());
            statement.setString(2, newActriz.getDescription());
            statement.setString(3, newActriz.getwikiLink());
            statement.setString(4, newActriz.getImage());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                Long idactriz = rs.getLong(1);
                String name = newActriz.getName();

                String mensaje = "La actriz " + name + " con el ID " + idactriz + " Fue agregada correctamente";

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


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                String query = "SELECT * FROM actrices";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
    
                List<Persona> actrices = new ArrayList<>();
    
                while (resultSet.next()) {
                    Persona actriz = new Persona(
                        resultSet.getInt("id_actriz"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("wikiLink"),
                        resultSet.getString("image")
                    );
                    actrices.add(actriz);
                }
    
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(actrices);
    
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
// Obtener el parámetro 'id_actriz' de la solicitud
String IdActriz = request.getParameter("id_actriz");

// Verificar que el parámetro no sea nulo y sea un número válido
if (IdActriz == null || !IdActriz.matches("\\d+")) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getWriter().write("Invalid id_actriz parameter");
    return;
}
int Idactriz = Integer.parseInt(IdActriz);

String deleteQuery = "DELETE FROM actrices WHERE id_actriz = ?";

try (Connection conn = new Conexion().getConnection();
        PreparedStatement statement = conn.prepareStatement(deleteQuery)) {

    // Establecer el valor del parámetro en la consulta
    statement.setInt(1, Idactriz);
    int rowsAffected = statement.executeUpdate();

    // Verificar cuántas filas fueron afectadas
    if (rowsAffected > 0) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("La actriz con el ID " + Idactriz + " fue eliminada correctamente");
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
        String idActriz = request.getParameter("id_actriz");
        if (idActriz == null || !idActriz.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID de actriz no válido");
            return;
        }

        // Obtener los datos del cuerpo de la solicitud JSON
        ObjectMapper mapper = new ObjectMapper();
        Persona actriz = mapper.readValue(request.getReader(), Persona.class);

        if (actriz.getName() == null || actriz.getDescription() == null || actriz.getwikiLink() == null
                || actriz.getImage() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Todos los campos (name, description, wikiLink, image) son requeridos");
            return;
        }

        // Construir la consulta SQL para la actualización
        String updateQuery = "UPDATE actrices SET name = ?, description = ?, wikiLink = ?, image = ? WHERE id_actriz = ?";

        try (Connection conn = new Conexion().getConnection();
                PreparedStatement statement = conn.prepareStatement(updateQuery)) {

            statement.setString(1, actriz.getName());
            statement.setString(2, actriz.getDescription());
            statement.setString(3, actriz.getwikiLink());
            statement.setString(4, actriz.getImage());
            statement.setInt(5, Integer.parseInt(idActriz));

            int responseActualizacion = statement.executeUpdate();

            if (responseActualizacion > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("La actriz con el ID " + idActriz + " fue actualizada correctamente");
            } else {

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("No se encontró ningun actriz con ese ID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al actualizar la actriz en la base de datos: " + e.getMessage());
        }
    }
    
    }

    
