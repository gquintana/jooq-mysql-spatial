/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import javax.sql.DataSource;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class ColumnMetadataTest {
    private DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration();
    private DataSource dataSource;
    @Before
    public void setUp() {
        dataSource = dataSourceConfiguration.dataSource();
    }
    @Test
    public void testMetaData() throws SQLException {
        try (Connection connection= dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select geom from test_geometry limit 1");) {
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnType=metaData.getColumnType(1);
            String columnTypeName = metaData.getColumnTypeName(1);
            assertEquals(Types.BINARY, columnType);
            assertEquals("geometry", columnTypeName.toLowerCase());
        }
    }

}
