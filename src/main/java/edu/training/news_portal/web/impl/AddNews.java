package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.News;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.NewsService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.MessageText;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.util.AuthUtil;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.util.NewsUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AddNews implements Command {

	private final NewsService newsService = ServiceProvider.getInstance().getNewsService();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (!AuthUtil.isAuthenticated(session)) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_AUTH, MessageText.PLEASE_SIGN_IN);
			return;
		}

		User currentUser = AuthUtil.getAuthenticatedUser(session);

		try {

			News news = NewsUtil.buildNewsFromRequest(request, currentUser);

			newsService.addNews(news);
			NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);

		} catch (IllegalArgumentException e) {
			request.setAttribute(RequestAttr.ERROR, e.getMessage());
			NavigationUtil.forward(request, response, "WEB-INF/jsp/create_news.jsp");
		} catch (ServiceException e) {
			e.printStackTrace();
			request.setAttribute(RequestAttr.ERROR, MessageText.ERROR_ADD_NEWS);
			NavigationUtil.forward(request, response, "WEB-INF/jsp/create_news.jsp");
		}
	}
}