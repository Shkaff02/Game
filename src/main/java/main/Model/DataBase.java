package main.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static final String url = "jdbc:mysql://localhost:8889/2048";
    private static final String user = "root";
    private static final String password = "root";

    private  static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Connected to the MySQL server successfully.");
            } else {
                System.out.println("Failed to make connection!");
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return connection;
    }

    //try with resources
    public static void insertSerializable(int size, byte[] arr) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement("INSERT INTO save_game(size, grid) VALUES(?,?)" +
                    "ON DUPLICATE KEY UPDATE grid=? ");
            ps.setInt(1, size);
            ps.setBytes(2, arr);
            ps.setBytes(3, arr);
            ps.executeUpdate();
        }
    }

    public static ResultSet getSerializableData(int size) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT grid FROM save_game WHERE size=? " +
                "AND id=(SELECT MAX(id) " +
                "FROM save_game )");
        ps.setInt(1,size);
        rs = ps.executeQuery();
        return rs;

    }

    public static void updateScore(Score score) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement("UPDATE score SET score=? WHERE id=1");
            PreparedStatement ps2 = connection.prepareStatement("UPDATE score SET score=? WHERE id=2");
            PreparedStatement ps3 = connection.prepareStatement("UPDATE score SET score=? WHERE id=3");
            List<PreparedStatement> preparedStatementList = new ArrayList<>();
            ps.setInt(1, score.getTOP1());
            ps2.setInt(1, score.getTOP2());
            ps3.setInt(1, score.getTOP3());
            ps.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
        }

    }

    public static List<Integer> getScore() throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT score FROM score");
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> scoreList = new ArrayList<>();
            while (rs.next()){
                scoreList.add(rs.getInt("score"));
            }
            return scoreList;
        }
    }

}
