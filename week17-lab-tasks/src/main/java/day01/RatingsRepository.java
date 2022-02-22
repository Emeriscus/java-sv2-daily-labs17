package day01;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
//language=sql

public class RatingsRepository {

    private DataSource dataSource;

    public RatingsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRating(long movieId, List<Integer> ratings) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            insertRatingsFromConnection(movieId, ratings, conn);

        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert", sqle);
        }
    }

    private void insertRatingsFromConnection(long movieId, List<Integer> ratings, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into ratings(movie_id, rating) values (?,?)")) {

            for (Integer actual : ratings) {
                if (actual < 1 || actual > 5) {
                    throw new IllegalArgumentException("invalid rating");
                }
                stmt.setLong(1, movieId);
                stmt.setLong(2, actual);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (IllegalArgumentException iae) {
            conn.rollback();
        }
    }
}
