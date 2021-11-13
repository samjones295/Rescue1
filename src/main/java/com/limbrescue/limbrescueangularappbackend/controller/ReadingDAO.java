package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Reading;
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
public class ReadingDAO {
    //All attributes read from the properties file.
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public ReadingDAO() {
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        table = p.getProperty("spring.datasource.ReadingTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the readings table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the readings table.
     */
    @GetMapping("/readings")
    @ResponseBody
    public List<Reading> getAllReadings() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        List<Reading> readings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Reading reading = new Reading(result.getInt("id"), result.getString("patient_no"),
                        result.getDate("date_created"), result.getString("laterality"), /*result.getString("active_or_rest"),*/ result.getString("comments"));
                readings.add(reading);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return readings;
    }

    /**
     * Retrieves a single reading based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the readings table.
     */
    @GetMapping("/reading/{id}")
    @ResponseBody
    public Reading getReading(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        Reading reading = null;
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                reading = new Reading();
                reading.setId(id);
                reading.setPatient_no(result.getString("patient_no"));
                reading.setDate_created(result.getDate("date_created"));
                reading.setLaterality(result.getString("laterality"));
                //reading.setActive_or_rest(result.getString("active_or_rest"));
                reading.setComments(result.getString("comments"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return reading;
    }

    /**
     * Inserts a reading to the table.
     *
     * @param reading
     *              The reading to be inserted.
     */
    @PostMapping(path = "/reading")
    @ResponseBody
    public void insertReading(@RequestBody Reading reading) {
        Connection connection = dbConnection.getConnection();
        if (getReading(reading.getId()) != null) {
            updateReading(reading, reading.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " (id, patient_no, date_created, laterality, comments) VALUES(?, ?, ?, ?, ?)";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, reading.getId());
                statement.setString(2, reading.getPatient_no());
                statement.setDate(3, reading.getDate_created());
                statement.setString(4, reading.getLaterality());
                //statement.setString(4, reading.getActive_or_rest());
                statement.setString(5, reading.getComments());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a reading based on the ID.
     *
     * @param reading
     *          The variable values of the columns.
     * @param id
     *          The reading ID to be updated.
     */
    @PutMapping(path="/reading/{id}")
    @ResponseBody
    public void updateReading(@RequestBody Reading reading, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET patient_no = ?, date_created = ?, laterality = ?, " +
                " WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, reading.getPatient_no());
            statement.setDate(2, reading.getDate_created());
            statement.setString(3, reading.getLaterality());
            //statement.setString(3, reading.getActive_or_rest());
            statement.setInt(4, id);
            statement.executeUpdate();
            //reading.setActive_or_rest(result.getString("active_or_rest"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the comments of a patient.
     *
     * @param id
     *          The id to be updated.
     * @param comment
     *          The updated comment.
     */
    @PutMapping("/readingcomment/{id}")
    @ResponseBody
    public void updateComments(@PathVariable("id") int id, @RequestParam String comment) {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET comments = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, comment);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Deletes a reading based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/reading/{id}")
    @ResponseBody
    public void deleteReading(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
