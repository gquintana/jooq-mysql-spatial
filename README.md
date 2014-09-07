jooq-mysql-spatial
==================

This is an experiment to integrate jOOQ with MySQL Spatial.

# jOOQ Codegen configuration

    <database>
        <name>org.jooq.util.mysql.MySQLDatabase</name>
        <customTypes>
            <customType>
                <name>Geometry</name>
                <type>com.vividsolutions.jts.geom.Geometry</type>
                <converter>net.gquintana.jooq.mysql.GeometryConverter</converter>
            </customType>
        </customTypes>
        <forcedTypes>
            <forcedType>
                <name>Geometry</name>
                <types>(geometry|GEOMETRY)</types>
            </forcedType>
        </forcedTypes>
    </database>

# Writing geometries

    Point point = new Point(...)
    dsl.insertInto(CITY, CITY.ID, CITY.NAME, CITY.GEOM)
            .values(1, "Lyon", point)
            .execute();

# Reading geometries

    Point point = (Point) dsl.select(CITY.GEOM)
            .from(CITY)
            .where(CITY.ID.eq(1))
            .fetchOne(CITY.GEOM);

Casting Geometry to Point is required because there is no way to determine Geometry type from column metadata.

# Querying 

Search cities located in France

    Polygon france = new Polygon(...)
    Result<CityRecord> cities = dsl.selectFrom(CITY)
            .where(MBRWithin(CITY.GEOM, france))
            .fetch();

Search to country containing Lyon

    Point lyon = new Point(...)
    Result<CountryRecord> countries = dsl.selectFrom(COUNTRY)
            .where(MBRContains(COUNTRY.GEOM, lyon))
            .fetch();

# Limitations and TODOs

- The Endianness used to read/write geometries to bytes is not configurable nor autodetectable yet
- The GeometryConverter converts Geometry to/from Object instead of to/from byte[] because DefaultDataType.getDefaultDataType() is of type Object and Codegen produces:

    public final org.jooq.TableField<net.gquintana.jooq.mysql.tables.records.TestGeometryRecord, com.vividsolutions.jts.geom.Geometry> GEOM = 
        createField("geom", 
            org.jooq.impl.DefaultDataType.getDefaultDataType("geometry"), 
            this, 
            "", 
            new net.gquintana.jooq.mysql.GeometryConverter());

