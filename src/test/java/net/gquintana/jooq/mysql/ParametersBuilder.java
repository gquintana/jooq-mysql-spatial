/*
 * Default License
 */

package net.gquintana.jooq.mysql;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for Paramaterized test classes
 */
final class ParametersBuilder {
    private final List<Object[]> parameters = new ArrayList<>();

    public ParametersBuilder add(Object ... objects) {
        parameters.add(objects);
        return this;
    }

    public List<Object[]> build() {
        return parameters;
    }

}
