drop table if exists test_geometry;

create table test_geometry (
    id int not null primary key,
    geom geometry
) ENGINE=MyISAM;

-- create spatial index idx_test_geometry_spatial on test_geometry(geom);