package edu.training.news_portal.dao.impl;

import edu.training.news_portal.bean.Comment;
import edu.training.news_portal.dao.CommentDao;
import edu.training.news_portal.dao.DaoException;
import edu.training.news_portal.dao.DbColumn;
import edu.training.news_portal.dao.pool.ConnectionPool;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBCommentDao implements CommentDao {

	private final ConnectionPool pool = ConnectionPool.getInstance();

	private static final String SQL_GET_COMMENTS_BY_NEWS_ID = "SELECT c." + DbColumn.Comment.ID + ", c."
			+ DbColumn.Comment.CONTENT + ", c." + DbColumn.Comment.CREATED_DATE + ", c." + DbColumn.Comment.UPDATED_DATE
			+ ", c." + DbColumn.Comment.USER_ID + ", c." + DbColumn.Comment.STATUS_ID + ", ud."
			+ DbColumn.UserDetails.NAME + " AS " + DbColumn.UserDetails.NAME + ", cs." + DbColumn.Comment.STATUS + " "
			+ "FROM Commentaries c " + "JOIN user_details ud ON c." + DbColumn.Comment.USER_ID + " = ud."
			+ DbColumn.UserDetails.USER_ID + " " + "JOIN commentaries_status cs ON c." + DbColumn.Comment.STATUS_ID
			+ " = cs.id " + "WHERE c." + DbColumn.Comment.NEWS_ID + " = ? " + "AND cs." + DbColumn.Comment.STATUS
			+ " = 'activ' " + "ORDER BY c." + DbColumn.Comment.CREATED_DATE + " DESC";

	private static final String SQL_ADD_COMMENT = "INSERT INTO Commentaries (" + DbColumn.Comment.CONTENT + ", "
			+ DbColumn.Comment.CREATED_DATE + ", " + DbColumn.Comment.USER_ID + ", " + DbColumn.Comment.NEWS_ID + ", "
			+ DbColumn.Comment.STATUS_ID + ") VALUES (?, ?, ?, ?, ?)";

	private static final String SQL_UPDATE_COMMENT = "UPDATE Commentaries SET " + DbColumn.Comment.CONTENT + " = ?, "
			+ DbColumn.Comment.UPDATED_DATE + " = ? WHERE " + DbColumn.Comment.ID + " = ?";

	private static final String SQL_GET_COMMENT_BY_ID = "SELECT * FROM Commentaries WHERE " + DbColumn.Comment.ID
			+ " = ?";

	private static final String SQL_HIDE_COMMENT = "UPDATE Commentaries " + "SET " + DbColumn.Comment.STATUS_ID
			+ " = 2 " + // 2 = inactive
			"WHERE " + DbColumn.Comment.ID + " = ?";

	@Override
	public List<Comment> getCommentsByNewsId(int newsId) throws DaoException {
		List<Comment> comments = new ArrayList<>();

		try (Connection con = pool.takeConnection();
				PreparedStatement ps = con.prepareStatement(SQL_GET_COMMENTS_BY_NEWS_ID)) {

			ps.setInt(1, newsId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Comment comment = new Comment();
					comment.setId(rs.getInt(DbColumn.Comment.ID));
					comment.setNewsId(newsId);
					comment.setUserId(rs.getInt(DbColumn.Comment.USER_ID));
					comment.setUserName(rs.getString(DbColumn.UserDetails.NAME));
					comment.setText(rs.getString(DbColumn.Comment.CONTENT));
					comment.setCreatedAt(rs.getTimestamp(DbColumn.Comment.CREATED_DATE).toLocalDateTime());
					Timestamp updated = rs.getTimestamp(DbColumn.Comment.UPDATED_DATE);
					if (updated != null) {
						comment.setUpdatedAt(updated.toLocalDateTime());
					}
					comment.setStatusId(rs.getInt(DbColumn.Comment.STATUS_ID));
					comment.setStatus(rs.getString(DbColumn.Comment.STATUS));
					comments.add(comment);
				}
			}
		} catch (SQLException e) {
			throw new DaoException("Failed to load comments", e);
		}
		return comments;
	}

	@Override
	public boolean addComment(Comment comment) throws DaoException {
		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_ADD_COMMENT)) {
			ps.setString(1, comment.getText());
			ps.setTimestamp(2, Timestamp.valueOf(comment.getCreatedAt()));
			ps.setInt(3, comment.getUserId());
			ps.setInt(4, comment.getNewsId());
			ps.setInt(5, comment.getStatusId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new DaoException("Failed to add comment", e);
		}
	}

	@Override
	public boolean updateComment(Comment comment) throws DaoException {
		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_UPDATE_COMMENT)) {
			ps.setString(1, comment.getText());
			ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			ps.setInt(3, comment.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new DaoException("Failed to update comment", e);
		}
	}

	@Override
	public boolean hideComment(int commentId) throws DaoException {
		try (Connection connection = pool.takeConnection();
				PreparedStatement ps = connection.prepareStatement(SQL_HIDE_COMMENT)) {

			ps.setInt(1, commentId);
			int updatedRows = ps.executeUpdate();
			return updatedRows > 0;

		} catch (SQLException e) {
			throw new DaoException("Failed to hide comment with id=" + commentId, e);
		}
	}

	@Override
	public Comment getCommentById(int commentId) throws DaoException {
		try (Connection con = pool.takeConnection();
				PreparedStatement ps = con.prepareStatement(SQL_GET_COMMENT_BY_ID)) {
			ps.setInt(1, commentId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Comment comment = new Comment();
					comment.setId(rs.getInt(DbColumn.Comment.ID));
					comment.setText(rs.getString(DbColumn.Comment.CONTENT));
					comment.setUserId(rs.getInt(DbColumn.Comment.USER_ID));
					comment.setNewsId(rs.getInt(DbColumn.Comment.NEWS_ID));
					comment.setStatusId(rs.getInt(DbColumn.Comment.STATUS_ID));
					comment.setCreatedAt(rs.getTimestamp(DbColumn.Comment.CREATED_DATE).toLocalDateTime());
					Timestamp updated = rs.getTimestamp(DbColumn.Comment.UPDATED_DATE);
					if (updated != null) {
						comment.setUpdatedAt(updated.toLocalDateTime());
					}
					return comment;
				}
				return null;
			}
		} catch (SQLException e) {
			throw new DaoException("Failed to get comment", e);
		}
	}
}
