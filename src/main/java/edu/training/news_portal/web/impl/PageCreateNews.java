package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.NewsGroups;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.util.NavigationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PageCreateNews implements Command {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute(RequestAttr.NEWS_GROUPS, NewsGroups.values());
		NavigationUtil.forward(request, response, "WEB-INF/jsp/create_news.jsp");
	}
}
