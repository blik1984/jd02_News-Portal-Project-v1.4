package edu.training.news_portal.dao.impl;

import edu.training.news_portal.bean.News;
import edu.training.news_portal.bean.NewsGroups;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.dao.DaoException;
import edu.training.news_portal.dao.DbColumn;
import edu.training.news_portal.dao.NewsDao;
import edu.training.news_portal.dao.pool.ConnectionPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBNewsDao implements NewsDao {

	private final ConnectionPool pool = ConnectionPool.getInstance();

	private static final String SQL_SELECT_TOP_NEWS = "SELECT n." + DbColumn.News.ID + ", n."
			+ DbColumn.News.NEWS_GROUP_ID + ", n." + DbColumn.News.TITLE + ", " + "n." + DbColumn.News.BRIEF + ", n."
			+ DbColumn.News.CONTENT_PATH + ", " + "n." + DbColumn.News.CREATE_DATE + ", n." + DbColumn.News.UPDATED_DATE
			+ ", " + "n." + DbColumn.News.PUBLISH_DATE + ", n." + DbColumn.News.USER_ID + " "
			+ "FROM news n ORDER BY n." + DbColumn.News.ID + " ASC LIMIT ?";

	private static final String SQL_ADD_NEWS = "INSERT INTO news (" + DbColumn.News.NEWS_GROUP_ID + ", "
			+ DbColumn.News.TITLE + ", " + DbColumn.News.BRIEF + ", " + DbColumn.News.CONTENT_PATH + ", "
			+ DbColumn.News.CREATE_DATE + ", " + DbColumn.News.UPDATED_DATE + ", " + DbColumn.News.PUBLISH_DATE + ", "
			+ DbColumn.News.USER_ID + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SQL_GET_NEWS_PAGED = "SELECT n." + DbColumn.News.ID + ", n."
			+ DbColumn.News.NEWS_GROUP_ID + ", n." + DbColumn.News.TITLE + ", " + "n." + DbColumn.News.BRIEF + ", n."
			+ DbColumn.News.CONTENT_PATH + ", " + "n." + DbColumn.News.CREATE_DATE + ", n." + DbColumn.News.UPDATED_DATE
			+ ", " + "n." + DbColumn.News.PUBLISH_DATE + ", n." + DbColumn.News.USER_ID + " "
			+ "FROM news n ORDER BY n." + DbColumn.News.ID + " DESC LIMIT ? OFFSET ?";

	private static final String SQL_COUNT_NEWS = "SELECT COUNT(*) FROM news";

	private static final String SQL_GET_NEWS_BY_ID = "SELECT n." + DbColumn.News.ID + ", n." + DbColumn.News.TITLE
			+ ", n." + DbColumn.News.BRIEF + ", " + "n." + DbColumn.News.CONTENT_PATH + ", n."
			+ DbColumn.News.CREATE_DATE + ", " + "n." + DbColumn.News.UPDATED_DATE + ", n." + DbColumn.News.PUBLISH_DATE
			+ ", " + "n." + DbColumn.News.NEWS_GROUP_ID + ", n." + DbColumn.News.USER_ID + ", " + "ng.value AS "
			+ DbColumn.NewsGroup.VALUE + ", " + "u." + DbColumn.User.EMAIL + ", u." + DbColumn.User.PASSWORD + ", "
			+ "u." + DbColumn.User.ROLE_ID + ", u." + DbColumn.User.STATUS_ID + ", " + "u."
			+ DbColumn.User.REGISTRATION_DATE + ", u." + DbColumn.User.UPDATED_DATE + " AS user_updated_date "
			+ "FROM news n " + "JOIN news_group ng ON n." + DbColumn.News.NEWS_GROUP_ID + " = ng.id "
			+ "LEFT JOIN users u ON n." + DbColumn.News.USER_ID + " = u." + DbColumn.User.ID + " " + "WHERE n."
			+ DbColumn.News.ID + " = ?";
	private static final String SQL_COUNT_PUBLISHED_NEWS = "SELECT COUNT(*) FROM news WHERE "
			+ DbColumn.News.PUBLISH_DATE + " <= ?";

	@Override
	public List<News> topNews(int count) throws DaoException {
		List<News> newsList = new ArrayList<>();

		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_SELECT_TOP_NEWS)) {

			ps.setInt(1, count);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					newsList.add(mapNews(rs, false));
				}
			}

		} catch (SQLException e) {
			throw new DaoException("Error retrieving top news", e);
		}

		return newsList;
	}

	@Override
	public boolean addNews(News news) throws DaoException {
		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_ADD_NEWS)) {

			ps.setInt(1, news.getGroup().getId());
			ps.setString(2, news.getTitle());
			ps.setString(3, news.getBrief());
			ps.setString(4, saveTextNewsToFile(news.getContent()));
			ps.setObject(5, news.getCreateDateTime());
			ps.setObject(6, news.getUpdateDateTime());
			ps.setObject(7, news.getPublishingDateTime());

			if (news.getPublisher() != null) {
				ps.setInt(8, news.getPublisher().getId());
			} else {
				ps.setNull(8, Types.INTEGER);
			}

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new DaoException("Error adding news", e);
		}
	}

	@Override
	public List<News> getNewsPaged(int limit, int offset) throws DaoException {
		List<News> newsList = new ArrayList<>();

		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_GET_NEWS_PAGED)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					newsList.add(mapNews(rs, false));
				}
			}

		} catch (SQLException e) {
			throw new DaoException("Error retrieving paginated news", e);
		}

		return newsList;
	}

	@Override
	public int countNews() throws DaoException {
		try (Connection con = pool.takeConnection();
				PreparedStatement ps = con.prepareStatement(SQL_COUNT_NEWS);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;

		} catch (SQLException e) {
			throw new DaoException("Error counting news", e);
		}
	}

	@Override
	public News getNewsById(int newsId) throws DaoException {
		try (Connection con = pool.takeConnection(); PreparedStatement ps = con.prepareStatement(SQL_GET_NEWS_BY_ID)) {

			ps.setInt(1, newsId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapNews(rs, true);
				}
			}

			return null;

		} catch (SQLException e) {
			throw new DaoException("Error fetching news by id", e);
		}
	}

	@Override
	public News findNews() throws DaoException {
		// Not implemented yet
		return null;
	}

	private News mapNews(ResultSet rs, boolean includePublisher) throws SQLException, DaoException {
		News news = new News();
		news.setId(rs.getInt(DbColumn.News.ID));
		news.setTitle(rs.getString(DbColumn.News.TITLE));
		news.setBrief(rs.getString(DbColumn.News.BRIEF));
		news.setFileContentPath(rs.getString(DbColumn.News.CONTENT_PATH));
		news.setCreateDateTime(rs.getObject(DbColumn.News.CREATE_DATE, LocalDateTime.class));
		news.setUpdateDateTime(rs.getObject(DbColumn.News.UPDATED_DATE, LocalDateTime.class));
		news.setPublishingDateTime(rs.getObject(DbColumn.News.PUBLISH_DATE, LocalDateTime.class));

		int groupId = rs.getInt(DbColumn.News.NEWS_GROUP_ID);
		news.setGroup(NewsGroups.fromId(groupId));

		if (includePublisher) {
			int userId = rs.getInt(DbColumn.News.USER_ID);
			if (!rs.wasNull()) {
				User user = new User();
				user.setId(userId);
				user.setEmail(rs.getString(DbColumn.User.EMAIL));
				news.setPublisher(user);
			}

			String contentPath = rs.getString(DbColumn.News.CONTENT_PATH);
			if (contentPath != null) {
				news.setContent(getTextNewsFromFile(contentPath));
			}
		}

		return news;
	}

	@Override
	public int countPublishedNews(LocalDateTime currentTime) throws DaoException {
		try (Connection con = pool.takeConnection();
				PreparedStatement ps = con.prepareStatement(SQL_COUNT_PUBLISHED_NEWS)) {

			ps.setObject(1, currentTime);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}

			return 0;

		} catch (SQLException e) {
			throw new DaoException("Error counting published news", e);
		}
	}

	private String saveTextNewsToFile(String newsContent) throws DaoException {
		try {
			URL location = DBNewsDao.class.getProtectionDomain().getCodeSource().getLocation();
			Path basePath = Paths.get(location.toURI()).getParent().getParent();
			Path contentDir = basePath.resolve(Paths.get("resources", "news", "content"));

			if (!Files.exists(contentDir)) {
				Files.createDirectories(contentDir);
			}

			String fileName = "news_" + System.currentTimeMillis() + ".txt";
			Path filePath = contentDir.resolve(fileName);

			try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
				writer.print(newsContent);
			}

			return filePath.toString();

		} catch (Exception e) {
			throw new DaoException("Error saving news content to file", e);
		}
	}

	private String getTextNewsFromFile(String path) throws DaoException {
		try {
			return Files.readString(Path.of(path));
		} catch (IOException e) {
			throw new DaoException("Error reading news file: " + path, e);
		}
	}
}
