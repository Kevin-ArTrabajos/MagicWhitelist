package me.TheNocht.magicWhitelist.structures;

import me.TheNocht.magicWhitelist.MagicWhitelist;
import me.TheNocht.magicWhitelist.WhitelistManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {
    MagicWhitelist magicWhitelist;
    private Connection conn = null;


    public DatabaseManager(String path, MagicWhitelist magicWhitelist) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.magicWhitelist = magicWhitelist;

        try (Statement statement = conn.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT, " +
                    "timestamp INTEGER, " +
                    "addby TEXT, " +
                    "rolelevel INTEGER )");

            statement.execute("CREATE TABLE IF NOT EXISTS config (" +
                    "accesslevel INTEGER )");

            // insert if not exists
            statement.execute("INSERT OR IGNORE INTO config (accesslevel) VALUES (0)");
        }
    }

    public boolean savePlayer(WhitelistedPlayer player) {
        if (conn == null) throw new RuntimeException("Database connection is null");

        if (this.playerExists(player)) return false;

        try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO players (uuid, username, timestamp, addby, rolelevel) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, player.uuid.toString());
            preparedStatement.setString(2, player.username);
            preparedStatement.setLong(3, player.timestamp);
            preparedStatement.setString(4, player.addBy);
            preparedStatement.setInt(5, player.role.level);

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
//            this.magicWhitelist.getLogger().log(Level.WARNING, "Database error: ", e);
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    public boolean playerExists(WhitelistedPlayer player) {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.uuid.toString());

            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
//            this.magicWhitelist.getLogger().log(Level.WARNING, "Database error: ", e);
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    public WhitelistedPlayer getPlayer(UUID uuid) {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return null;

            return new WhitelistedPlayer(resultSet.getString("username"), uuid, resultSet.getLong("timestamp"), resultSet.getString("addby"), Roles.getRole(resultSet.getInt("rolelevel")));
        } catch (SQLException e) {
//            this.magicWhitelist.getLogger().log(Level.WARNING, "Database error: ", e);
            throw new RuntimeException("Error accessing the database", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removePlayer(UUID uuid) {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    public WhitelistedPlayer updateParent(WhitelistedPlayer player) {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE players SET rolelevel = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, player.role.level);
            preparedStatement.setString(2, player.uuid.toString());

            preparedStatement.executeUpdate();
            return player;
        } catch (SQLException e) {
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    public boolean setAccessLevel(int level) {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE config SET accesslevel = ?")) {
            preparedStatement.setInt(1, level);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    public int getAccessLevel() {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM config LIMIT 1")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return 0;

            return resultSet.getInt("accesslevel");
        } catch (SQLException e) {
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    public List<WhitelistedPlayer> getAllPlayers() {
        if (conn == null) throw new RuntimeException("Database connection is null");

        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM players")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<WhitelistedPlayer> players = new ArrayList<>();
            while (resultSet.next()) {
                String username = null;
                String uuid = null;
                try {
                    username = resultSet.getString("username");
                    uuid = resultSet.getString("uuid");
                    players.add(new WhitelistedPlayer(resultSet.getString("username"), UUID.fromString(resultSet.getString("uuid")), resultSet.getLong("timestamp"), resultSet.getString("addby"), Roles.getRole(resultSet.getInt("rolelevel"))));
                } catch (Exception e) {
                    this.magicWhitelist.getLogger().log(Level.WARNING, "Error parsing player %s (%s) from database: ".formatted(username, uuid), e);
                }
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException("Error accessing the database", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
