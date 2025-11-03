package edu.training.news_portal.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import edu.training.news_portal.bean.News;
import edu.training.news_portal.dao.DaoException;
import edu.training.news_portal.dao.DaoProvider;
import edu.training.news_portal.dao.NewsDao;
import edu.training.news_portal.service.NewsService;
import edu.training.news_portal.service.ServiceException;

public class NewsServiceImpl implements NewsService {

	private final NewsDao newsDao = DaoProvider.getInstance().getNewsDao();

	private final int MAX_AVAILABLE_TOP_NEWS = 10;

	@Override
	public List<News> takeTopNews(int count) throws ServiceException {

		if (count < 1) {
			throw new ServiceException("Error");
		}

		if (count > MAX_AVAILABLE_TOP_NEWS) {
			count = 10;
		}

		try {
			return newsDao.topNews(count);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<News> getNewsPage(int page, int pageSize) throws ServiceException {
		int offset = (page - 1) * pageSize;
		try {
			return newsDao.getNewsPaged(pageSize, offset);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public int countAllNews() throws ServiceException {
		try {
			return newsDao.countNews();
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean addNews(News news) throws ServiceException {

		try {
			LocalDateTime now = LocalDateTime.now();
			news.setCreateDateTime(now);
			news.setUpdateDateTime(now);
			return newsDao.addNews(news);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public News getNews(int newsId) throws ServiceException {
		try {
			News news = newsDao.getNewsById(newsId);
			if (news == null) {
				throw new ServiceException("News not found with id: " + newsId);
			}
			return news;
		} catch (DaoException e) {
			throw new ServiceException("Error retrieving news", e);
		}
	}

	@Override
	public int countPublishedNews() throws ServiceException {
		try {
			return newsDao.countPublishedNews(LocalDateTime.now());
		} catch (DaoException e) {
			throw new ServiceException("Error counting published news", e);
		}
	}
}
