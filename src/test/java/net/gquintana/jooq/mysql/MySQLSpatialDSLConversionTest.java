/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTWriter;
import java.util.Collection;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static net.gquintana.jooq.mysql.Tables.*;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.TransactionalRunnable;
import static net.gquintana.jooq.mysql.MySQLSpatialDSL.*;
import org.jooq.DataType;
/**
 *
 * @author gerald
 */
@RunWith(Parameterized.class)
public class MySQLSpatialDSLConversionTest {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final DataSource dataSource;
    private final GeometryFactory geometryFactory;
    private final Geometry geometry;
    private final String wkt;

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration();
        GeometryHelper gh = new GeometryHelper();        
        GeometryFactory gf = gh.geometryFactory;
        return new ParametersBuilder()
                .add(dataSourceConfiguration, gf, gh.createLyonPoint())
                .add(dataSourceConfiguration, gf, gh.createParisLyonLineString())
                .add(dataSourceConfiguration, gf, gh.createFrancePolygon())
                .build();
    }
    public MySQLSpatialDSLConversionTest(DataSourceConfiguration dataSourceConfiguration, GeometryFactory geometryFactory, Geometry geometry) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.dataSource = dataSourceConfiguration.dataSource();
        this.geometryFactory = geometryFactory;
        this.geometry = geometry;
        this.wkt = new WKTWriter().write(geometry);
        new GeometryConverter().registerDataType();
    }

    private void assertEqualsGeometry(Geometry result) {
        GeometryHelper.assertEqualsGeometry(geometry, result);
    }
    private static String removeSpace(String s) {
        return s.replaceAll("\\s+", "");
    }
    @Test
    public void testInsertSelect() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        dslContext.transaction(new TransactionalRunnable() {

            @Override
            public void run(Configuration configuration) throws Exception {
                dslContext.delete(TEST_GEOMETRY).execute();
                dslContext.insertInto(TEST_GEOMETRY, TEST_GEOMETRY.ID, TEST_GEOMETRY.GEOM)
                        .values(val(1), GeomFromText(wkt))
                        .execute();
                final Field<String> wktField = AsWKT(TEST_GEOMETRY.GEOM).as("wkt");
                String wkt2 = dslContext.select(wktField)
                        .from(TEST_GEOMETRY)
                        .fetchOne(wktField);
                assertEquals(removeSpace(wkt), removeSpace(wkt2));
            }
        });
        Geometry result=dslContext.select(GeomFromText(wkt)).fetchOne().value1();
        GeometryHelper.assertEqualsGeometry(geometry, result);
    }
    
    @Test
    public void testTextToGeom() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        final Field<Geometry> geomField = GeomFromText(wkt).as("geom");
        Geometry result=dslContext.select(geomField).fetchOne(geomField);
        GeometryHelper.assertEqualsGeometry(geometry, result);
    }
    @Test
    public void testGeomToText() {
        final DSLContext dslContext = dataSourceConfiguration.jooq();
        final Field<String> wktField = AsWKT(geometry).as("wkt");
        String result=dslContext.select(wktField).fetchOne(wktField);
        assertEquals(removeSpace(wkt), removeSpace(result));        
    }
    
}
