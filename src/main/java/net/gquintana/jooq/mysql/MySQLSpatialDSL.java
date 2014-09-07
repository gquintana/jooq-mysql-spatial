/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Geometry;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.function;
import org.jooq.impl.DefaultDataType;

/**
 *
 */
public class MySQLSpatialDSL extends DSL {
    // -------------------------------------------------------------------------
    // MBRContains
    public static Field<Boolean> MBRContains(Field<Geometry> g1, Field<Geometry> g2) {
        return function("MBRContains", Boolean.TYPE, g1, g2);
    }
    public static Field<Boolean> MBRContains(Geometry g1, Field<Geometry> g2) {
        return MBRContains(val(g1, Geometry.class), g2);
    }
    public static Field<Boolean> MBRContains(Field<Geometry> g1, Geometry g2) {
        return MBRContains(g1, val(g2, Geometry.class));
    }
    // -------------------------------------------------------------------------
    // MBRWithin
    public static Field<Boolean> MBRWithin(Field<Geometry> g1, Field<Geometry> g2) {
        return function("MBRWithin", Boolean.TYPE, g1, g2);
    }
    public static Field<Boolean> MBRWithin(Geometry g1, Field<Geometry> g2) {
        return MBRWithin(val(g1, Geometry.class), g2);
    }
    public static Field<Boolean> MBRWithin(Field<Geometry> g1, Geometry g2) {
        return MBRWithin(g1, val(g2, Geometry.class));
    }
    // -------------------------------------------------------------------------
    // AsWKT Geometry to WKT conversion
    public static Field<String> AsWKT(Field<Geometry> g) {
        return function("AsWKT", String.class, g);
    }
    public static Field<String> AsWKT(Geometry g) {
        return AsWKT(val(g, Geometry.class));
    }
    // -------------------------------------------------------------------------
    // GeomFromText WKT to Geometry conversion
    public static Field<Geometry> GeomFromText(Field<String> s) {
        return function("GeomFromText", Geometry.class, s);
    }
    public static Field<Geometry> GeomFromText(String s) {
        return GeomFromText(val(s, String.class));
    }
}
