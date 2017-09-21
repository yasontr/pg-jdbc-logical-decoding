# pg-jdbc-logical-decoding

## install wal2json decoding plugin

- git clone https://github.com/eulerto/wal2json.git
- cd wal2json
- USE_PGXS=1 sudo make
- USE_PGXS=1 sudo make install

**pg_hba.conf**

- local   replication     pg-jdbc-logical-decoding                                peer
- host    replication     pg-jdbc-logical-decoding        127.0.0.1/32            md5
- host    replication     pg-jdbc-logical-decoding        ::1/128                 md5

**postgres.conf**

- wal_level = logical
- max_wal_senders = 8
- wal_keep_segments = 4
- wal_sender_timeout = 60s
- max_replication_slots = 4

## create database, role, replication slot and table

- ./database/setup.sh

This script assumes that there is a superuser 'pgsql'.