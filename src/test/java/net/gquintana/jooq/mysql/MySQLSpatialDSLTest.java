/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Polygon;
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
/**
 *
 * @author gerald
 */
public class MySQLSpatialDSLTest {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final DataSource dataSource;
    private final GeometryHelper gh;
    public MySQLSpatialDSLTest() {
        this.dataSourceConfiguration = new DataSourceConfiguration();
        this.dataSource = dataSourceConfiguration.dataSource();
        this.gh = new GeometryHelper();
    }

    
    @Test
    public void testSelectByMBRContains() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
                // Insert
                dslContext.insertInto(TEST_GEOMETRY, TEST_GEOMETRY.ID, TEST_GEOMETRY.GEOM)
                        .values(1, gh.createPoint(gh.parisCoord))
                        .values(2, gh.createPoint(gh.lyonCoord))
                        .values(3, gh.createPoint(gh.londonCoord))
                        .execute();
                // Select by MBR Contains
                Polygon france = gh.createPolygon(gh.lilleCoord, gh.strasbourgCoord, gh.niceCoord, gh.perpignanCoord, gh.biarritzCoord, gh.brestCoord, gh.lilleCoord);
                List<Integer> result = dslContext.select(TEST_GEOMETRY.ID)
                        .from(TEST_GEOMETRY)
                        .where(mbrContains(france, TEST_GEOMETRY.GEOM))
                        .fetch(TEST_GEOMETRY.ID);
                // London is outside France MBR
                assertThat(result, hasItems(1,2));
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
            }
        });
    }

    @After
    public void tearDown() {
    }
    
}
