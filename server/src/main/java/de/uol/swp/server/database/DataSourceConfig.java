package de.uol.swp.server.database;

import com.google.inject.Inject;
import de.uol.swp.server.env.EnvReader;
import lombok.Getter;


/**
 * This class is responsible for the connection to the database.
 * It uses the HikariCP connection pool.
 */
@Getter
public class DataSourceConfig  {
    private final String dbType;
    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructor for the DataSourceConfig
     */
    @Inject
    public DataSourceConfig(final EnvReader envReader) {
        this.dbType = envReader.readString("DB_TYPE");
        this.dbHost = envReader.readString("DB_HOST");
        this.dbPort = envReader.readInt("DB_PORT");
        this.dbName = envReader.readString("DB_NAME");
        this.url = "jdbc:" + this.dbType + "://" + this.dbHost + ":" + this.dbPort + "/" + this.dbName;
        this.username = envReader.readString("DB_USER");
        this.password = envReader.readString("DB_PASSWORD");
    }

}
