package com.rahulswaminathan.yarnapplicationstatistics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLWrapper {

	Connection conn = null;

	/**
	 * A wrapper for MySQL. This can simplify writing to a MySQL database.
	 * 
	 * @param databaseName
	 *            Name of the database being written to.
	 * @param host
	 *            Host of the MySQL server. Typically localhost.
	 * @param user
	 *            Username for the MySQL server.
	 */
	public SQLWrapper(String databaseName, String host, String user) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			conn = DriverManager.getConnection("jdbc:mysql://" + host + "/"
					+ databaseName + "?" + "user=" + user);
		} catch (SQLException e) {
			printSQLInformation(e);
		} catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Removes a row from the specified table.
     * @param table
     *          Table to remove row from.
     * @param tag
     *          Tag value of the row to be removed
     * @return
     *          True if row removed, false otherwise.
     */
    public boolean removeRow(String table, String tag) {
        Statement statement;
        try {
            statement = conn.createStatement();
            statement.executeUpdate("DELETE from " + table + " WHERE tag='" + tag + "'");
        } catch (SQLException e) {
            //printSQLInformation(e);
            return false;
        }

        return true;
    }

    public boolean createTagValueTable(String table) {
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE " + table + "(tag varchar(255), value varchar(255))");
		} catch (SQLException e) {
			printSQLInformation(e);
			return false;
		}

		return true;
	}
	/**
	 * Updates a value in a given table, assuming that the table has two
	 * columns, the first of which is a string, and the second is an integer.
	 * 
	 * @param table
	 *            Table to be updated
	 * @param tag
	 *            Tag to be updated.
	 * @param newValue
	 *            New value of the given tag.
	 * @return True on success, false otherwise.
	 */
	public boolean updateValue(String table, String tag, int newValue) {
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate("UPDATE " + table + " SET value="
					+ newValue + " WHERE tag='" + tag + "'");
		}
         catch (SQLException e) {
            printSQLInformation(e);
            return false;
        }

		return true;
	}

    /**
     * Updates a value in a given table, assuming that the table has two
     * columns, the first of which is a string, and the second is an string.
     *
     * @param table
     *            Table to be updated
     * @param tag
     *            Tag to be updated.
     * @param newValue
     *            New value of the given tag.
     * @return True on success, false otherwise.
     */
    public boolean updateValue(String table, String tag, String newValue) {
        Statement statement;
        try {
            statement = conn.createStatement();
            statement.executeUpdate("UPDATE " + table + " SET value='"
                    + newValue + "' WHERE tag='" + tag + "'");
        }
        catch (SQLException e) {
            printSQLInformation(e);
            return false;
        }

        return true;
    }

	/**
	 * Inserts a value into a given table, assuming that the table has two
	 * columns, the first of which is a string, and the second is an integer.
	 * 
	 * @param table
	 *            Table to be edited.
	 * @param tag
	 *            First column of a table
	 * @param value
	 *            Second column of a table
	 * @return True on success, false otherwise.
	 */
	public boolean insertIntoTable(String table, String tag, int value) {
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO " + table + " " + "VALUES ('"
					+ tag + "', " + value + ")");
		} catch (SQLException e) {
            printSQLInformation(e);
			return false;
		}

		return true;
	}


    /**
     * Inserts a value into a given table, assuming that the table has two
     * columns, the first of which is a string, and the second is an string.
     *
     * @param table
     *            Table to be edited.
     * @param tag
     *            First column of a table
     * @param value
     *            Second column of a table
     * @return True on success, false otherwise.
     */
    public boolean insertIntoTable(String table, String tag, String value) {
        Statement statement;
        try {
            statement = conn.createStatement();
            statement.executeUpdate("INSERT INTO " + table + " " + "VALUES ('"
                    + tag + "', '" + value + "')");
        } catch (SQLException e) {
            printSQLInformation(e);
            return false;
        }

        return true;
    }

	/**
	 * Prints the given table to the console.
	 * 
	 * @param table
	 *            Table to be printed.
	 */
	public void printTableInformation(String table) {
		Statement statement;
		ResultSet rs;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery("SELECT * from " + table);

			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();

			System.out.println("TABLE: " + table);
			System.out.println("----------------");
			for (int i = 1; i <= numberOfColumns; i++) {
				if (i > 1)
					System.out.print(" | ");
				String columnName = rsmd.getColumnName(i);
				System.out.print(columnName);
			}
			System.out.println("");
			System.out.println("----------------");

			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue);
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			printSQLInformation(e);
		}

	}

	private void printSQLInformation(SQLException ex) {
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	}
}
