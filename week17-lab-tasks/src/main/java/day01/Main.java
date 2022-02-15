package day01;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
//language=sql

public class Main {

    public static void main(String[] args) {

        MariaDbDataSource dataSource = new MariaDbDataSource();

        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/movies-actors?useUnicode=true");
            dataSource.setUser("root");
            dataSource.setPassword("root");
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot reach database", se);
        }

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();

        ActorsRepository actorsRepository = new ActorsRepository(dataSource);

        MoviesRepository moviesRepository = new MoviesRepository(dataSource);

        moviesRepository.saveMovie("Titanic", LocalDate.of(1997, 12, 11));
        moviesRepository.saveMovie("T2", LocalDate.of(1990, 12, 11));

        System.out.println(moviesRepository.findAllMovies());

    }
}
