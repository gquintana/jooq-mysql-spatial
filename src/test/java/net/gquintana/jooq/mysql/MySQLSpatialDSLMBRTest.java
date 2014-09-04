/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import java.util.List;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.gquintana.jooq.mysql.Tables.*;
import org.jooq.Configuration;
import org.jooq.TransactionalRunnable;
import static net.gquintana.jooq.mysql.MySQLSpatialDSL.*;
import static org.hamcrest.CoreMatchers.*;
import org.jooq.exception.DataAccessException;
/**
 * Unit test for {@link MySQLSpatialDSL}.MBR* functions
 * @author gerald
 */
public class MySQLSpatialDSLMBRTest {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final DataSource dataSource;
    private final GeometryHelper gh;
    public MySQLSpatialDSLMBRTest() {
        this.dataSourceConfiguration = new DataSourceConfiguration();
        this.dataSource = dataSourceConfiguration.dataSource();
        this.gh = new GeometryHelper();
    }
    private void insertPoints(DSLContext dslContext) throws DataAccessException {
        // Insert
        dslContext.insertInto(TEST_GEOMETRY, TEST_GEOMETRY.ID, TEST_GEOMETRY.GEOM)
                .values(1, gh.createPoint(gh.parisCoord))
                .values(2, gh.createPoint(gh.lyonCoord))
                .values(3, gh.createPoint(gh.londonCoord))
                .execute();
    }

    private void insertPolygons(DSLContext dslContext) throws DataAccessException {
        // Insert
        dslContext.insertInto(TEST_GEOMETRY, TEST_GEOMETRY.ID, TEST_GEOMETRY.GEOM)
                .values(1, gh.createPolygon(gh.lilleCoord, gh.strasbourgCoord, gh.niceCoord, gh.perpignanCoord, gh.lilleCoord))
                .values(2, gh.createPolygon(gh.lilleCoord, gh.brestCoord, gh.biarritzCoord, gh.perpignanCoord, gh.lilleCoord))
                .execute();
    }
    
    @Test
    public void testSelectByMBRContains_1() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
                insertPoints(dslContext);
                // Select by MBR Contains
                List<Integer> result = dslContext.select(TEST_GEOMETRY.ID)
                        .from(TEST_GEOMETRY)
                        .where(MBRContains(gh.createFrancePolygon(), TEST_GEOMETRY.GEOM))
                        .fetch(TEST_GEOMETRY.ID);
                // London is outside France MBR
                assertThat(result, hasItems(1,2));
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
            }

        });
    }
    
    @Test
    public void testSelectByMBRContains_2() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
                insertPolygons(dslContext);
                // Select by MBR Contains
                List<Integer> result = dslContext.select(TEST_GEOMETRY.ID)
                        .from(TEST_GEOMETRY)
                        .where(MBRContains(TEST_GEOMETRY.GEOM, gh.createLyonPoint()))
                        .fetch(TEST_GEOMETRY.ID);
                // London is outside France MBR
                assertThat(result, hasItems(1));
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
            }

        });
    }

    @Test
    public void testSelectByMBRWithin_1() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
                insertPoints(dslContext);
                // Select by MBR Contains
                List<Integer> result = dslContext.select(TEST_GEOMETRY.ID)
                        .from(TEST_GEOMETRY)
                        .where(MBRWithin(TEST_GEOMETRY.GEOM, gh.createFrancePolygon()))
                        .fetch(TEST_GEOMETRY.ID);
                // London is outside France MBR
                assertThat(result, hasItems(1,2));
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
            }

        });
    }
    
    @Test
    public void testSelectByMBRWithin_2() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
                insertPolygons(dslContext);
                // Select by MBR Contains
                List<Integer> result = dslContext.select(TEST_GEOMETRY.ID)
                        .from(TEST_GEOMETRY)
                        .where(MBRWithin(gh.createLyonPoint(), TEST_GEOMETRY.GEOM))
                        .fetch(TEST_GEOMETRY.ID);
                // London is outside France MBR
                assertThat(result, hasItems(1));
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
            }
        });
    }

    @After
    public void tearDown() {
    }
    
}
