package by.madcat.development.databaseviewer.Utils.QueriesGenerators;

import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL.MSSQLQueriesGenerator;
import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MySQL.MySQLQueriesGenerator;

public class QueriesGeneratorFactory {
    public static final QueriesGeneratorInterface getGenerator(DatabasesTypes databaseType) throws Exception {
        switch (databaseType){
            case MSSQL:
                return new MSSQLQueriesGenerator();
            case MySQL:
                return new MySQLQueriesGenerator();
            default:
                throw new Exception("database type error");
        }
    }
}
