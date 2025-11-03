package edu.training.news_portal.web.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import edu.training.news_portal.bean.News;
import edu.training.news_portal.bean.Role;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.NewsService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.util.AuthUtil;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.util.NewsUtil;
import edu.training.news_portal.web.util.PaginationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class PageUserHome implements Command {

	private static final String USER_HOME_JSP_PATH = "WEB-INF/jsp/userHome.jsp";
	private final NewsService newsService = ServiceProvider.getInstance().getNewsService();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if (!AuthUtil.isAuthenticated(session)) {
			NavigationUtil.redirect(response, RequestPath.PAGE_AUTH);
			return;
		}

		User currentUser = (User) session.getAttribute(RequestAttr.AUTH_USER);
		boolean isAdmin = currentUser != null && Role.ADMINISTRATOR == currentUser.getRole();

		int currentPage = PaginationUtil.parsePage(request);
		int pageSize = PaginationUtil.resolvePageSize(request, session);

		try {
			loadNewsData(request, isAdmin, currentPage, pageSize);
			NavigationUtil.forward(request, response, USER_HOME_JSP_PATH);
		} catch (ServiceException e) {
			NavigationUtil.redirect(response, RequestPath.PAGE_ERROR);
		}
	}

	private void loadNewsData(HttpServletRequest request, boolean isAdmin, int currentPage, int pageSize)
			throws ServiceException {

		List<News> pagedNews;
		int totalNewsCount;

		if (isAdmin) {
			pagedNews = newsService.getNewsPage(currentPage, pageSize);
			totalNewsCount = newsService.countAllNews();
		} else {
			pagedNews = newsService.getNewsPage(currentPage, pageSize).stream().filter(NewsUtil::isPublished)
					.collect(Collectors.toList());
			totalNewsCount = newsService.countPublishedNews();
		}

		int totalPages = (int) Math.ceil((double) totalNewsCount / pageSize);

		request.setAttribute(RequestAttr.TOP_NEWS, pagedNews);
		request.setAttribute(RequestAttr.CURRENT_PAGE, currentPage);
		request.setAttribute(RequestAttr.TOTAL_PAGES, totalPages);
		request.setAttribute(RequestAttr.CURRENT_PAGE_SIZE, pageSize);
		request.setAttribute(RequestAttr.IS_ADMIN, isAdmin);
		request.setAttribute(RequestAttr.NOW, LocalDateTime.now());
	}
}