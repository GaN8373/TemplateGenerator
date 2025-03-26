package generator

enum class DatabaseType {
    MYSQL,
    SQLSERVER,
    ORACLE,
    POSTGRESQL,
    SQLITE,
    H2,
    MARIADB,
    DB2,
    OTHER;

    companion object {
        fun getDatabaseType(databaseName: String): DatabaseType {
            return when (databaseName.lowercase()) {
                "mysql" -> MYSQL
                "sqlserver" -> SQLSERVER
                "oracle" -> ORACLE
                "postgresql" -> POSTGRESQL
                "sqlite" -> SQLITE
                "h2" -> H2
                "mariadb" -> MARIADB
                "db2" -> DB2
                else -> OTHER
            }
        }
    }
}