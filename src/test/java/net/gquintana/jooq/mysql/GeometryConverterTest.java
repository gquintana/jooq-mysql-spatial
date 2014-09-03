/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
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

    private static final class ParametersBuilder {
        private final List<Object[]> parameters = new ArrayList<>();
        private final DataSourceConfiguration dataSourceConfiguration;
        private final GeometryFactory geometryFactory;
        public ParametersBuilder(DataSourceConfiguration dataSourceConfiguration, GeometryFactory geometryFactory) {
            this.dataSourceConfiguration = dataSourceConfiguration;
            this.geometryFactory = geometryFactory;
        }
        public ParametersBuilder add(Geometry geometry) {            
            parameters.add(new Object[]{this.dataSourceConfiguration, this.geometryFactory, geometry});
            return this;
        }
        public List<Object[]> build() {
            return parameters;
        }
    }
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration();
        GeometryHelper h = new GeometryHelper();        
        return new ParametersBuilder(dataSourceConfiguration, h.geometryFactory)
                .add(h.createPoint(h.lyonCoord))
                .add(h.createLineString(h.parisCoord, h.lyonCoord))
                .add(h.createPolygon(h.lilleCoord, h.strasbourgCoord, h.niceCoord, h.perpignanCoord, h.biarritzCoord, h.brestCoord, h.lilleCoord))
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
        assertEquals(geometry.getClass(), result.getClass());
        assertEquals(geometry.getCoordinates().length, result.getCoordinates().length);
        for(int i=0;i<geometry.getCoordinates().length; i++) {
            assertEquals(geometry.getCoordinates()[i].x, result.getCoordinates()[i].x, 0.1);
            assertEquals(geometry.getCoordinates()[i].y, result.getCoordinates()[i].y, 0.1);
        }
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
