package craftedcart.smblevelworkshop.community;

import craftedcart.smblevelworkshop.community.creator.CommunityRepo;
import craftedcart.smblevelworkshop.community.creator.CommunityUser;
import craftedcart.smblevelworkshop.community.creator.ICommunityCreator;
import craftedcart.smblevelworkshop.data.AppDataManager;
import craftedcart.smbworkshopexporter.util.LogHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
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

    public void buildCommunityDatabase(List<ICommunityCreator> creatorList) throws SQLException {
        replaceLevelsTable(creatorList);
    }

    private void replaceLevelsTable(List<ICommunityCreator> creatorList) throws SQLException {
        File supportDir = AppDataManager.getAppSupportDirectory();

        //In case files were deleted
        closeDatabaseConnection();
        connectToDatabase();

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

        for (ICommunityCreator creator : creatorList) {
            if (creator instanceof CommunityUser) {
                CommunityUser user = (CommunityUser) creator;

                addLevelStatement.setString(1, user.getUsername());

                File levelsXML = new File(supportDir, "community/users/" + user.getUsername() + "/root/levels/LevelList.xml");

                if (levelsXML.exists() && !levelsXML.isDirectory()) {
                    try {
                        List<CommunityLevel> levelList = CommunityLevel.getCommunityLevelsFromXML(levelsXML, user.getUsername(), user.getDisplayName());

                        for (CommunityLevel level : levelList) {
                            addLevelStatement.setString(2, level.getUserDisplayName()); //User Display Name
                            addLevelStatement.setString(3, level.getId()); //ID
                            addLevelStatement.setString(4, level.getName()); //Name
                            addLevelStatement.setString(5, level.getShortDescription()); //Short Description
                            addLevelStatement.setLong(6, level.getTime()); //Creation Time
                        }

                        addLevelStatement.execute();

                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        LogHelper.error(getClass(), "Failed to parse LevelList.xml for " + user.getUsername());
                        LogHelper.error(getClass(), "Skipping user");
                        LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));
                    }
                }

            } else if (creator instanceof CommunityRepo) {
                CommunityRepo repo = (CommunityRepo) creator;
                //TODO Handle root branch for single repo

            }
        }
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
