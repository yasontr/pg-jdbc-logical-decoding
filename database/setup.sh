#!/bin/bash

export PGPASSWORD='pg-jdbc-logical-decoding'

psql -U pgsql -d postgres -c 'select pg_drop_replication_slot($$pg_jdbc_logical_decoding$$)'
psql -U pgsql -d postgres -c 'drop database if exists "pg-jdbc-logical-decoding"'
psql -U pgsql -d postgres -c 'drop role if exists "pg-jdbc-logical-decoding"'
psql -U pgsql -d postgres -c 'create role "pg-jdbc-logical-decoding" with login replication password $$pg-jdbc-logical-decoding$$'
psql -U pgsql -d postgres -c 'create database "pg-jdbc-logical-decoding" with owner agenda encoding "utf-8"'
# psql -U pgsql -d pg-jdbc-logical-decoding -c 'select pg_create_logical_replication_slot($$pg_jdbc_logical_decoding$$, $$test_decoding$$)'
psql -U pgsql -d pg-jdbc-logical-decoding -c 'select pg_create_logical_replication_slot($$pg_jdbc_logical_decoding$$, $$wal2json$$)'
psql -U pg-jdbc-logical-decoding -d pg-jdbc-logical-decoding -c 'create table test(key integer primary key, value text not null)'