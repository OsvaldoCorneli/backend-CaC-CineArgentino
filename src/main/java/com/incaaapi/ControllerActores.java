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

@WebServlet("/actores")
public class ControllerActores extends HttpServlet {

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
            Persona newActor = mapper.readValue(request.getInputStream(), Persona.class);

            String query = "INSERT INTO actores (name, description, wikilink, image) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, newActor.getName());
            statement.setString(2, newActor.getDescription());
            statement.setString(3, newActor.getwikiLink());
            statement.setString(4, newActor.getImage());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                Long idactor = rs.getLong(1);
                String name = newActor.getName();

                String mensaje = "El Actor " + name + " con el ID " + idactor + " Fue agregado correctamente";

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
                String query = "SELECT * FROM actores";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                List<Persona> actores = new ArrayList<>();

                while (resultSet.next()) {
                    Persona actor = new Persona(
                            resultSet.getInt("id_actor"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getString("wikiLink"),
                            resultSet.getString("image"));
                    actores.add(actor);
                }

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(actores);

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
        // Obtener el parámetro 'id_actor' de la solicitud
        String IdActors = request.getParameter("id_actor");

        // Verificar que el parámetro no sea nulo y sea un número válido
        if (IdActors == null || !IdActors.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid id_actor parameter");
            return;
        }
        int idactor = Integer.parseInt(IdActors);

        String deleteQuery = "DELETE FROM actores WHERE id_actor = ?";

        try (Connection conn = new Conexion().getConnection();
                PreparedStatement statement = conn.prepareStatement(deleteQuery)) {

            // Establecer el valor del parámetro en la consulta
            statement.setInt(1, idactor);
            int rowsAffected = statement.executeUpdate();

            // Verificar cuántas filas fueron afectadas
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("El actor con el ID " + idactor + " fue eliminado correctamente");
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
        String idActor = request.getParameter("id_actor");
        if (idActor == null || !idActor.matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID de actor no válido");
            return;
        }

        // Obtener los datos del cuerpo de la solicitud JSON
        ObjectMapper mapper = new ObjectMapper();
        Persona actor = mapper.readValue(request.getReader(), Persona.class);

        if (actor.getName() == null || actor.getDescription() == null || actor.getwikiLink() == null
                || actor.getImage() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Todos los campos (name, description, wikiLink, image) son requeridos");
            return;
        }

        // Construir la consulta SQL para la actualización
        String updateQuery = "UPDATE actores SET name = ?, description = ?, wikiLink = ?, image = ? WHERE id_actor = ?";

        try (Connection conn = new Conexion().getConnection();
                PreparedStatement statement = conn.prepareStatement(updateQuery)) {

            statement.setString(1, actor.getName());
            statement.setString(2, actor.getDescription());
            statement.setString(3, actor.getwikiLink());
            statement.setString(4, actor.getImage());
            statement.setInt(5, Integer.parseInt(idActor));

            int responseActualizacion = statement.executeUpdate();

            if (responseActualizacion > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("El actor con el ID " + idActor + " fue actualizado correctamente");
            } else {

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("No se encontró ningun actor con el ID especificado");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al actualizar el actor en la base de datos: " + e.getMessage());
        }
    }

}
