package ru.takeshiko.minecraft.cowsay.bungee.managers

import ru.takeshiko.minecraft.cowsay.bungee.CowSayBungee
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseManager(private val plugin: CowSayBungee) {

    private lateinit var dbConnection: Connection

    fun setupDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            plugin.logger.severe("Failed to load JDBC driver: ${e.message}")
            return
        }

        val host = plugin.configManager.config.getString("database.host")
        val port = plugin.configManager.config.getString("database.port")
        val database = plugin.configManager.config.getString("database.database")
        val username = plugin.configManager.config.getString("database.username")
        val password = plugin.configManager.config.getString("database.password")

        val url = "jdbc:mysql://" +
                host + ":" +
                port + "/" +
                database +
                "?autoReconnect=true"

        try {
            dbConnection = DriverManager.getConnection(url, username, password)
            createTable()
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to connect to database: ${e.message}")
        }
    }

    private fun createTable() {
        try {
            dbConnection.createStatement().use { stmt ->
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cowsay (username VARCHAR(16), lastSay TEXT, sayCount INT)")
                plugin.logger.info("Table \"cowsay\" has been created!")
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to create table: ${e.message}")
        }
    }

    fun closeDatabaseConnection() {
        try {
            if (isConnectionValid()) {
                dbConnection.close()
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to close database connection: ${e.message}")
        }
    }

    private fun isConnectionValid(): Boolean {
        return try {
            dbConnection.isValid(10)
        } catch (e: SQLException) {
            plugin.logger.severe("Database connection is not valid: ${e.message}")
            false
        }
    }

    @Synchronized
    fun updateOrInsertSay(username: String, text: String) {
        if (!recordExists(username)) {
            insertRecord(username, text)
        } else {
            updateRecord(username, text)
        }
    }

    private fun recordExists(username: String): Boolean {
        val selectSql = "SELECT 1 FROM cowsay WHERE username = ?"
        return try {
            dbConnection.prepareStatement(selectSql).use { ps ->
                ps.setString(1, username)
                ps.executeQuery().use { rs ->
                    rs.next()
                }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to check if record exists: ${e.message}")
            false
        }
    }

    private fun insertRecord(username: String, text: String) {
        val insertSql = "INSERT INTO cowsay (username, lastSay, sayCount) VALUES (?, ?, 1)"
        try {
            dbConnection.prepareStatement(insertSql).use { ps ->
                ps.setString(1, username)
                ps.setString(2, text)
                ps.executeUpdate()
                plugin.logger.info("New record created for username: $username")
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to insert record: ${e.message}")
        }
    }

    private fun updateRecord(username: String, text: String) {
        val updateSql = "UPDATE cowsay SET lastSay = ?, sayCount = sayCount + 1 WHERE username = ?"
        try {
            dbConnection.prepareStatement(updateSql).use { ps ->
                ps.setString(1, text)
                ps.setString(2, username)
                val rowsUpdated = ps.executeUpdate()
                if (rowsUpdated > 0) {
                    plugin.logger.info("Table \"cowsay\" has been updated!")
                } else {
                    plugin.logger.warning("No rows were updated.")
                }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to update record: ${e.message}")
        }
    }

    @Synchronized
    fun getSayCount(username: String): Int {
        val sql = "SELECT sayCount FROM cowsay WHERE username = ?"
        return try {
            dbConnection.prepareStatement(sql).use { ps ->
                ps.setString(1, username)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        rs.getInt("sayCount")
                    } else {
                        0
                    }
                }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to get say count: ${e.message}")
            0
        }
    }
}