/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Geometry;
import org.jooq.Field;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.function;

/**
 *
 */
public class MySQLSpatialDSL extends DSL {
    public static Field<Boolean> mbrContains(Field<Geometry> g1, Field<Geometry> g2) {
        return function("MBRContains", Boolean.TYPE, g1, g2);
    }
    public static Field<Boolean> mbrContains(Geometry g1, Field<Geometry> g2) {
        return mbrContains(val(g1, Geometry.class), g2);
    }
}
