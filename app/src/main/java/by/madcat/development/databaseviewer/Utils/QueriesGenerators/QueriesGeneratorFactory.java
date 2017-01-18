package by.madcat.development.databaseviewer.Utils.QueriesGenerators;

import by.madcat.development.databaseviewer.Utils.QueriesGenerators.MSSQL.MSSQLQueriesGenerator;

public class QueriesGeneratorFactory {
    public static final QueriesGeneratorInterface getGenerator(DatabasesTypes databaseType) throws Exception {
        switch (databaseType){
            case MSSQL:
                return new MSSQLQueriesGenerator();
            default:
                throw new Exception("database type error");
        }
    }
}
