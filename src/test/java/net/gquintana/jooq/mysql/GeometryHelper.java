/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class GeometryHelper {
    public final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);
    private static final int WGS84_SRID = 4326;
    public final Coordinate lyonCoord = new Coordinate(4.8357, 45.764);
    public final Coordinate parisCoord = new Coordinate(2.3522, 48.8566);
    public final Coordinate brestCoord = new Coordinate(-4.4861, 48.3904);
    public final Coordinate lilleCoord = new Coordinate(3.0573, 50.6293);
    public final Coordinate strasbourgCoord = new Coordinate(7.7479, 48.5831);
    public final Coordinate perpignanCoord = new Coordinate(2.8959, 42.6987);
    public final Coordinate niceCoord = new Coordinate(7.2656, 43.696);
    public final Coordinate biarritzCoord = new Coordinate(-1.5667, 43.4833);
    public final Coordinate londonCoord = new Coordinate(-0.1257, 51.5085);
    public Point createPoint(Coordinate coordinate) {
        return geometryFactory.createPoint(coordinate);
    }
    public LineString createLineString(Coordinate ... coordinates) {
        return geometryFactory.createLineString(coordinates);
    }
    public Polygon createPolygon(Coordinate ... coordinates) {
        return geometryFactory.createPolygon(coordinates);
    }
    public Point createLyonPoint() {
        return createPoint(lyonCoord);
    }
    public LineString createParisLyonLineString() {
        return createLineString(parisCoord, lyonCoord);
    }
    public Polygon createFrancePolygon() {
        return createPolygon(lilleCoord, strasbourgCoord, niceCoord, perpignanCoord, biarritzCoord, brestCoord, lilleCoord);
    }
    public static void assertEqualsGeometry(Geometry expected, Geometry actual) {
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.getCoordinates().length, actual.getCoordinates().length);
        for(int i=0;i<expected.getCoordinates().length; i++) {
            assertEquals(expected.getCoordinates()[i].x, actual.getCoordinates()[i].x, 0.1);
            assertEquals(expected.getCoordinates()[i].y, actual.getCoordinates()[i].y, 0.1);
        }
    }

}
