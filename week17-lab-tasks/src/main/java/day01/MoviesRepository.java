package day01;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//language=sql

public class MoviesRepository {

    private DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long saveMovie(String title, LocalDate releaseDate) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt =
                     connection.prepareStatement("insert into movies(title, release_date) values (?,?)",
                             Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setDate(2, Date.valueOf(releaseDate));
            stmt.executeUpdate();

            return getIdByStatement(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot connect", sqle);
        }
    }

    private long getIdByStatement(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new IllegalStateException("Cannot insert to movies");
        }
    }


    public List<Movie> findAllMovies() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from movies order by id")
        ) {
            return processResult(rs);
        } catch (SQLException se) {
            throw new IllegalArgumentException("Cannot select employees", se);
        }
    }

    private List<Movie> processResult(ResultSet rs) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            movies.add(new Movie(id, title, releaseDate));
        }
        return movies;
    }
}
