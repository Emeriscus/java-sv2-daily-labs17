package day01;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//language=sql

public class ActorsRepository {

    private DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveActor(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("insert into actors(actor_name) values (?)")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot update" + name, se);
        }
    }

    public List<String> findActorsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt =
                     connection.prepareStatement("select actor_name from actors where actor_name like ?")) {

            stmt.setString(1, prefix);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String actorname = rs.getString("actor_name");
                    result.add(actorname);
                }
            }

        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query", sqle);
        }
        return result;
    }
}
