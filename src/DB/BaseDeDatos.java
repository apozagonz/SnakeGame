package DB;

import java.sql.*;

public class BaseDeDatos {
    private Connection connection;

    public BaseDeDatos() {
        try {
            // Cargar el driver
            connection = DriverManager.getConnection("jdbc:sqlite:snakegame.db");

            String crearTabla = "CREATE TABLE IF NOT EXISTS puntuaciones (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "score INTEGER, nombre TEXT)";
            Statement statement = connection.createStatement();
            statement.execute(crearTabla);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guardarPuntuacion(String nombre, int score) {
        try {
            String query = "INSERT INTO puntuaciones (nombre, score) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nombre);
            preparedStatement.setInt(2, score);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String obtenerRanking() {
        StringBuilder ranking = new StringBuilder();
        try {
            String query = "SELECT nombre, score FROM puntuaciones ORDER BY score DESC LIMIT 5";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int posicion = 1;
            while (resultSet.next()) {
                ranking.append(posicion).append(". ").append(resultSet.getString("nombre"))
                        .append(" - ").append(resultSet.getInt("score")).append("\n");
                posicion++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranking.toString();
    }
}

