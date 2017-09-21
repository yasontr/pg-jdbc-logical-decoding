package yasontr.pg_jdbc_logical_decoding;

import org.postgresql.PGConnection;
import org.postgresql.PGProperty;
import org.postgresql.replication.PGReplicationStream;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(final String[] arguments) {
        final Properties properties = new Properties();

        PGProperty.USER.set(properties, "pg-jdbc-logical-decoding");
        PGProperty.PASSWORD.set(properties, "pg-jdbc-logical-decoding");
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(properties, "9.4");
        PGProperty.REPLICATION.set(properties, "database");
        PGProperty.PREFER_QUERY_MODE.set(properties, "simple");

        while (true) {
            Connection          connection = null;
            PGReplicationStream stream     = null;

            try {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pg-jdbc-logical-decoding", properties);
                stream = connection.unwrap(PGConnection.class).getReplicationAPI().replicationStream().logical().withSlotName("pg_jdbc_logical_decoding").start();

                while (true) {
                    final ByteBuffer message = stream.read();
                    final int        offset  = message.arrayOffset();
                    final byte[]     source  = message.array();
                    final int        length  = source.length - offset;

                    System.out.println("event: " + new String(source, offset, length));
                    System.out.println("last received LSN: " + stream.getLastReceiveLSN());
                    System.out.println();

                    stream.setAppliedLSN(stream.getLastReceiveLSN());
                    stream.setFlushedLSN(stream.getLastReceiveLSN());
                    stream.forceUpdateStatus();
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (final SQLException exception) {
                        exception.printStackTrace();
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (final SQLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (final InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }
}