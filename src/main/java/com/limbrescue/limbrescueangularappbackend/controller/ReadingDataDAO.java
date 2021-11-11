package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.ReadingData;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class ReadingDataDAO {
    //All attributes read from the properties file.
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public ReadingDataDAO() {
        //Determine what file to read
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find the file");
        }
        try {
            p.load(reader);
        } catch (IOException e) {
            System.out.println("Cannot load file");
        }
        table = p.getProperty("spring.datasource.ReadingDataTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the reading data table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the reading data table.
     * @throws SQLException
     */
    @GetMapping("/allreadingdata")
    @ResponseBody
    public List<ReadingData> getAllReadingData() throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<ReadingData> readings = new ArrayList<>();
        while (result.next()) {
            ReadingData data = new ReadingData(result.getInt("id"), result.getInt("reading_id"),
                    result.getDouble("time"), result.getDouble("ppg_reading"));
            readings.add(data);
        }
        connection.close();
        return readings;
    }

    /**
     * Retrieves a single reading data based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the group readings table.
     * @throws SQLException
     */
    @GetMapping("/singlereadingdata/{id}")
    @ResponseBody
    public ReadingData getReadingData(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        ReadingData data = null;
        if (result.next()) {
            data = new ReadingData();
            data.setId(id);
            data.setReading_id(result.getInt("reading_id"));
            data.setTime(result.getDouble("time"));
            data.setPpg_reading(result.getDouble("ppg_reading"));
        }
        connection.close();
        return data;
    }

    /**
     * Inserts a reading data to the table.
     *
     * @param data
     *              The group reading to be inserted.
     * @throws SQLException
     */
    @PostMapping(path = "/readingdata")
    @ResponseBody
    public void insertReadingData(@RequestBody ReadingData data) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getReadingData(data.getId()) != null) {
            updateReadingData(data, data.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " (id, reading_id, time, ppg_reading) VALUES(?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, data.getId());
            statement.setInt(2, data.getReading_id());
            statement.setDouble(3, data.getTime());
            statement.setDouble(4, data.getPpg_reading());
            statement.executeQuery();
        }
        connection.close();
    }

    /**
     * Updates a reading data based on the ID.
     *
     * @param data
     *          The variable values of the columns.
     * @param id
     *          The reading data ID to be updated.
     * @throws SQLException
     */
    @PutMapping(path="/readingdata/{id}")
    @ResponseBody
    public void updateReadingData(@RequestBody ReadingData data, @PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET reading_id = ?, time = ?, ppg_reading = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, data.getReading_id());
        statement.setDouble(2, data.getTime());
        statement.setDouble(3, data.getPpg_reading());
        statement.setInt(4, id);
        statement.executeUpdate();
        connection.close();
    }

    /**
     * Deletes a reading data based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     * @throws SQLException
     */
    @DeleteMapping("/readingdata/{id}")
    @ResponseBody
    public void deleteReadingData(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
