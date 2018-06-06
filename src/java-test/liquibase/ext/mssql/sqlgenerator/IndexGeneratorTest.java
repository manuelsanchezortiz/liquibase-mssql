package liquibase.ext.mssql.sqlgenerator;

import liquibase.change.AddColumnConfig;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.OfflineConnection;
import liquibase.exception.DatabaseException;
import liquibase.ext.mssql.statement.CreateIndexStatementMSSQL;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.core.CreateIndexStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IndexGeneratorTest {
    @Test
    public void integrates() throws DatabaseException {
        final AddColumnConfig firstColumnConfig = new AddColumnConfig();
        firstColumnConfig.setName("id");
        final AddColumnConfig secondColumnConfig = new AddColumnConfig();
        secondColumnConfig.setName("name");

        //Liquibase must find our mssql impl.
        Database database= DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new OfflineConnection("offline:mssql", null));

        CreateIndexStatement statement = new CreateIndexStatement(null, null, null, "TABLE_NAME", true, null, firstColumnConfig, secondColumnConfig);
        Sql[] sql = SqlGeneratorFactory.getInstance().generateSql(statement, database);
        assertEquals("CREATE UNIQUE INDEX ON [TABLE_NAME]([id], [name])", sql[0].toSql());

        statement = new CreateIndexStatementMSSQL(statement, "included, includedtoo", null);
        sql = SqlGeneratorFactory.getInstance().generateSql(statement, database);
        assertEquals("CREATE UNIQUE INDEX ON [TABLE_NAME]([id], [name]) INCLUDE ([included], [includedtoo])", sql[0].toSql());

        statement = new CreateIndexStatementMSSQL(statement, null, 50);
        sql = SqlGeneratorFactory.getInstance().generateSql(statement, database);
        assertEquals("CREATE UNIQUE INDEX ON [TABLE_NAME]([id], [name]) WITH (FILLFACTOR = 50)", sql[0].toSql());

        statement = new CreateIndexStatementMSSQL(statement, "included, includedtoo", 50);
        sql = SqlGeneratorFactory.getInstance().generateSql(statement, database);
        assertEquals("CREATE UNIQUE INDEX ON [TABLE_NAME]([id], [name]) INCLUDE ([included], [includedtoo]) WITH (FILLFACTOR = 50)", sql[0].toSql());
    }
}
