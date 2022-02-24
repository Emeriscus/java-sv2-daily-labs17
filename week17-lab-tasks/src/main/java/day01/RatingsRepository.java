package day01;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            insertAverageRatingToMovies(movieId, getMovieAverageRating(movieId));

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

    public void insertAverageRatingToMovies(long movieID, double rating) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE movies SET avg_rating = (?) where id = (?);")) {
            stmt.setDouble(1, rating);
            stmt.setLong(2, movieID);
            stmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert", sqle);
        }
    }

// Kristóf megoldás:

    private double getMovieAverageRating(long movieId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement
                     ("select avg(rating) as calculated_avg from ratings join movies on movies.id=ratings.movie_id where movies.id = ?")) {
            stmt.setLong(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("calculated_avg");
                }
            }
            throw new IllegalArgumentException("Cannot find movie");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query", sqle);
        }
    }

// Saját megoldás:

//    private double getMovieAverageRating(long movieId) throws SQLException {
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement stmt = conn.prepareStatement("select * from ratings where movie_id = ?")) {
//            stmt.setLong(1, movieId);
//            return getAverageRating(processStatement(stmt));
//        }
//    }
//
//    private List<Double> processStatement(PreparedStatement stmt) throws SQLException {
//        List<Double> ratings = new ArrayList<>();
//        try (ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                ratings.add(rs.getDouble("rating"));
//            }
//        }
//        return ratings;
//    }
//
//    private Double getAverageRating(List<Double> ratings) {
//        double sum = ratings.stream().mapToInt(Double::intValue).sum();
//        long count = ratings.stream().count();
//        return sum / count;
//    }
}
