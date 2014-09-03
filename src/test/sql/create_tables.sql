create table test_geometry (
    id int not null primary key,
    geom geometry not null
) ENGINE=MyISAM;

create spatial index idx_test_geometry_spatial on test_geometry(geom);