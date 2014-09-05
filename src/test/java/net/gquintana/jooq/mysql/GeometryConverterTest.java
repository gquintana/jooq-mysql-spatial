/*
 * Default License
 */
package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static net.gquintana.jooq.mysql.Tables.*;
import org.jooq.Configuration;
import org.jooq.TransactionalRunnable;

/**
 *
 * @author gerald
 */
@RunWith(Parameterized.class)
public class GeometryConverterTest {

    private final DataSourceConfiguration dataSourceConfiguration;
    private final DataSource dataSource;
    private final GeometryFactory geometryFactory;
    private final Geometry geometry;
    private final GeometryConverter converter = new GeometryConverter();

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration();
        GeometryHelper gh = new GeometryHelper();
        GeometryFactory gf = gh.geometryFactory;
        return new ParametersBuilder()
                .add(dataSourceConfiguration, gf, gh.createLyonPoint())
                .add(dataSourceConfiguration, gf, gh.createParisLyonLineString())
                .add(dataSourceConfiguration, gf, gh.createFrancePolygon())
                .add(dataSourceConfiguration, gf, null)
                .build();
    }

    public GeometryConverterTest(DataSourceConfiguration dataSourceConfiguration, GeometryFactory geometryFactory, Geometry geometry) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.dataSource = dataSourceConfiguration.dataSource();
        this.geometryFactory = geometryFactory;
        this.geometry = geometry;
        this.converter.setCoordinateSequenceFactory(geometryFactory.getCoordinateSequenceFactory());
    }

    private void assertEqualsGeometry(Geometry result) {
        GeometryHelper.assertEqualsGeometry(geometry, result);
    }

    @Test
    public void testToFrom() {
        byte[] bytes = (byte[]) converter.to(geometry);
        Geometry result = converter.from(bytes);
        assertEqualsGeometry(result);
    }

    @Test
    public void testInsert_SelectById() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {
            @Override
            public void run(Configuration configuration) throws Exception {
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
                // Insert
                dslContext.insertInto(TEST_GEOMETRY, TEST_GEOMETRY.ID, TEST_GEOMETRY.GEOM)
                        .values(1, geometry)
                        .execute();
                // Select by Id
                Geometry result = dslContext.select(TEST_GEOMETRY.GEOM)
                        .from(TEST_GEOMETRY)
                        .where(TEST_GEOMETRY.ID.eq(1))
                        .fetchOne(TEST_GEOMETRY.GEOM);
                // Clean
                dslContext.delete(TEST_GEOMETRY).execute();
            }
        });
    }

    @After
    public void tearDown() {
    }

}
