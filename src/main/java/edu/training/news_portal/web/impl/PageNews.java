package edu.training.news_portal.web.impl;

import java.io.IOException;
import java.util.List;

import edu.training.news_portal.bean.Comment;
import edu.training.news_portal.bean.News;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.CommentService;
import edu.training.news_portal.service.NewsService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.util.CommentUtil;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.util.NewsUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PageNews implements Command {

	private final NewsService newsService = ServiceProvider.getInstance().getNewsService();
	private final CommentService commentService = ServiceProvider.getInstance().getCommentService();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User currentUser = (User) request.getSession().getAttribute(RequestAttr.AUTH_USER);

		try {
			int newsId = NewsUtil.parseNewsId(request);

			News news = newsService.getNews(newsId);
			if (news == null || !NewsUtil.isPublished(news)) {
				NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);
				return;
			}

			List<Comment> comments = commentService.getCommentsByNews(newsId, currentUser);

			request.setAttribute(RequestAttr.NEWS_ITEM, news);
			request.setAttribute(RequestAttr.COMMENTS_LIST, comments);

			CommentUtil.setEditingCommentId(request);

			NavigationUtil.forward(request, response, "WEB-INF/jsp/news.jsp");

		} catch (IllegalArgumentException | ServiceException e) {
			e.printStackTrace();
			NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);
		}
	}
}