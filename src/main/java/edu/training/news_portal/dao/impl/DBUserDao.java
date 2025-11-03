package edu.training.news_portal.dao.impl;

import edu.training.news_portal.bean.RegistrationInfo;
import edu.training.news_portal.bean.Role;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.dao.DaoException;
import edu.training.news_portal.dao.DbColumn;
import edu.training.news_portal.dao.UserDao;
import edu.training.news_portal.dao.pool.ConnectionPool;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class DBUserDao implements UserDao {

	private final ConnectionPool pool = ConnectionPool.getInstance();

	private static final String SQL_CHECK_CREDENTIALS = "SELECT u." + DbColumn.User.ID + ", ud."
			+ DbColumn.UserDetails.NAME + ", " + "r." + DbColumn.Role.NAME + " AS " + DbColumn.Role.ALIAS + " "
			+ "FROM users u " + "JOIN user_details ud ON u." + DbColumn.User.ID + " = ud."
			+ DbColumn.UserDetails.USER_ID + " " + "JOIN roles r ON u." + DbColumn.User.ROLE_ID + " = r."
			+ DbColumn.Role.ID + " " + "WHERE u." + DbColumn.User.EMAIL + " = ? AND u." + DbColumn.User.PASSWORD
			+ " = ?";

	private static final String SQL_INSERT_USER = "INSERT INTO users (" + DbColumn.User.EMAIL + ", "
			+ DbColumn.User.PASSWORD + ", " + DbColumn.User.ROLE_ID + ", " + DbColumn.User.STATUS_ID + ", "
			+ DbColumn.User.REGISTRATION_DATE + ") VALUES (?, ?, ?, ?, ?)";

	private static final String SQL_INSERT_USER_DETAILS = "INSERT INTO user_details (" + DbColumn.UserDetails.USER_ID
			+ ", " + DbColumn.UserDetails.NAME + ") VALUES (?, ?)";

	private static final String SQL_EMAIL_EXISTS = "SELECT COUNT(*) FROM users WHERE " + DbColumn.User.EMAIL + " = ?";

	@Override
	public Optional<User> checkCredentials(String email, String password) throws DaoException {
		try (Connection con = pool.takeConnection();
				PreparedStatement ps = con.prepareStatement(SQL_CHECK_CREDENTIALS)) {

			ps.setString(1, email);
			ps.setString(2, password);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt(DbColumn.User.ID);
					String name = rs.getString(DbColumn.UserDetails.NAME);
					Role role = Role.valueOf(rs.getString(DbColumn.Role.ALIAS).toUpperCase());
					return Optional.of(new User(userId, name, email, role));
				}
			}
			return Optional.empty();
		} catch (SQLException e) {
			throw new DaoException("Error checking credentials", e);
		}
	}

	@Override
	public boolean registration(RegistrationInfo info) throws DaoException {
		try (Connection con = pool.takeConnection()) {
			con.setAutoCommit(false);

			long userId = insertUser(con, info);
			insertUserDetails(con, userId, info.getName());

			con.commit();
			return true;

		} catch (SQLException e) {
			throw new DaoException("Database error during registration", e);
		}
	}

	@Override
	public boolean emailExists(String email) throws DaoException {
		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_EMAIL_EXISTS)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new DaoException("Error checking email existence", e);
		}
	}

	private long insertUser(Connection con, RegistrationInfo info) throws DaoException {
		try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, info.getEmail());
			ps.setString(2, info.getPassword());
			ps.setInt(3, 2); // Default role ID
			ps.setInt(4, 1); // Default status
			ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

			int affected = ps.executeUpdate();
			if (affected == 0) {
				throw new DaoException("Creating user failed: no rows affected");
			}

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getLong(1);
				} else {
					throw new DaoException("Creating user failed: no ID obtained");
				}
			}

		} catch (SQLException e) {
			rollbackQuietly(con);
			throw new DaoException("Error inserting user", e);
		}
	}

	private void insertUserDetails(Connection con, long userId, String name) throws DaoException {
		try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_USER_DETAILS)) {
			ps.setLong(1, userId);
			ps.setString(2, name);
			ps.executeUpdate();

		} catch (SQLException e) {
			rollbackQuietly(con);
			throw new DaoException("Error inserting user details", e);
		}
	}

	private void rollbackQuietly(Connection con) {
		try {
			con.rollback();
		} catch (SQLException ex) {
			// Просто логируем при необходимости
			System.err.println("Rollback failed: " + ex.getMessage());
		}
	}
}
