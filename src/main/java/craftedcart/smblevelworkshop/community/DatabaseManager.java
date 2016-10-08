package craftedcart.smblevelworkshop.community;

import craftedcart.smblevelworkshop.community.creator.CommunityRepo;
import craftedcart.smblevelworkshop.community.creator.AbstractCommunityCreator;
import craftedcart.smblevelworkshop.data.AppDataManager;
import craftedcart.smbworkshopexporter.util.LogHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 07/10/2016 (DD/MM/YYYY)
 */
public class DatabaseManager {

    private Connection connection = null;

    public DatabaseManager() {
        init();
    }

    public void init() {
        //Create a database connection
        connectToDatabase();

        //Add a shutdown hook to close the database connection on quit
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeDatabaseConnection));
    }

    public void buildCommunityDatabase(List<AbstractCommunityCreator> creatorList) throws SQLException {
        //In case files were deleted
        closeDatabaseConnection();
        connectToDatabase();

        buildUsersTable(creatorList);
        buildLevelsTable(creatorList);
    }

    private void buildUsersTable(List<AbstractCommunityCreator> creatorList) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30); //Set timeout to 30 sec

        //(Re)create the users table
        statement.executeUpdate("drop table if exists users");
        statement.executeUpdate("create table users (" +
                "username string," +
                "displayName string," +
                "isSingleRepo boolean," +
                "repo string," + //Repo will only be used if the user is using a single repo instead of a full account
                "bioPath string" +
                ")");

        PreparedStatement addUserStatement = connection.prepareStatement("insert into users values (?, ?, ?, ?, ?)");

        for (AbstractCommunityCreator creator : creatorList) {
            addUserStatement.setString(1, creator.getUsername()); //Username
            addUserStatement.setString(2, creator.getDisplayName()); //User Display Name
            addUserStatement.setBoolean(3, creator instanceof CommunityRepo); //Is Single Repo?
            addUserStatement.setString(4, creator instanceof CommunityRepo ? ((CommunityRepo) creator).getRepoName() : null); //Repo (Not single repo, so null)
            addUserStatement.setString(5, creator.getBioPath()); //Bio Path

            addUserStatement.execute();
        }
    }

    private void buildLevelsTable(List<AbstractCommunityCreator> creatorList) throws SQLException {
        File supportDir = AppDataManager.getAppSupportDirectory();

        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30); //Set timeout to 30 sec

        //(Re)create the levels table
        statement.executeUpdate("drop table if exists levels");
        statement.executeUpdate("create table levels (" +
                "username string," +
                "userDisplayName string," +
                "id string," +
                "levelName string," +
                "shortDescription string," +
                "creationTime bigint" +
                ")");

        PreparedStatement addLevelStatement = connection.prepareStatement("insert into levels values (?, ?, ?, ?, ?, ?)");

        for (AbstractCommunityCreator creator : creatorList) {
            addLevelStatement.setString(1, creator.getUsername()); //Username

            File levelsXML = new File(supportDir, "community/users/" + creator.getUsername() + "/root/levels/LevelList.xml");

            if (levelsXML.exists() && !levelsXML.isDirectory()) {
                try {
                    List<CommunityLevel> levelList = CommunityLevel.getCommunityLevelsFromXML(levelsXML, creator.getUsername(), creator.getDisplayName());

                    for (CommunityLevel level : levelList) {
                        addLevelStatement.setString(2, level.getUserDisplayName()); //User Display Name
                        addLevelStatement.setString(3, level.getId()); //ID
                        addLevelStatement.setString(4, level.getName()); //Name
                        addLevelStatement.setString(5, level.getShortDescription()); //Short Description
                        addLevelStatement.setLong(6, level.getCreationTime()); //Creation Time
                    }

                    addLevelStatement.execute();

                } catch (ParserConfigurationException | IOException | SAXException e) {
                    LogHelper.error(getClass(), "Failed to parse LevelList.xml for " + creator.getUsername());
                    LogHelper.error(getClass(), "Skipping user");
                    LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));
                }
            }
        }
    }

    public boolean isDatabaseOk() throws SQLException {
        if (connection != null) {
            DatabaseMetaData meta = connection.getMetaData();

            ResultSet usersTables = meta.getTables(null, null, "users", null);
            if (usersTables.next()) { //If "users" table exists
                ResultSet levelsTables = meta.getTables(null, null, "levels", null); //If "levels" table exists
                if (levelsTables.next()) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<CommunityLevel> getNewestLevels(int startIndex, int endIndex) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30); //Set timeout to 30 sec

        ResultSet results = statement.executeQuery(String.format("select * from levels order by creationTime desc limit %d,%d", startIndex, endIndex));

        List<CommunityLevel> levelList = new ArrayList<>();

        while (results.next()) {
            CommunityLevel level = new CommunityLevel();

            level.setUsername(results.getString("username"));
            level.setUserDisplayName(results.getString("userDisplayName"));
            level.setId(results.getString("id"));
            level.setName(results.getString("levelName"));
            level.setShortDescription(results.getString("shortDescription"));
            level.setCreationTime(results.getLong("creationTime"));

            levelList.add(level);
        }

        return levelList;
    }

    private void connectToDatabase() {
        File supportDir = AppDataManager.getAppSupportDirectory();
        File databaseFile = new File(supportDir, "community/communityDatabase.sqlite");

        try {
            LogHelper.info(getClass(), "Connecting to community/communityDatabase.sqlite");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        } catch (SQLException e) {
            LogHelper.error(getClass(), "Failed to get connection to community/communityDatabase.sqlite");
            LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));
        }
    }

    private void closeDatabaseConnection() {
        try {
            LogHelper.info(getClass(), "Closing connection to community/communityDatabase.sqlite");

            if (connection != null) {
                connection.close();
            }
        } catch(SQLException e) {
            //Connection close failed
            LogHelper.error(getClass(), "Failed to close connection to community/communityDatabase.sqlite");
            LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));
        }
    }

}
