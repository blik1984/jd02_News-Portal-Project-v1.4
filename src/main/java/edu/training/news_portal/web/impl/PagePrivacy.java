package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.util.NavigationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PagePrivacy implements Command {

	private static final String PRIVACY_JSP_PATH = "WEB-INF/jsp/privacy.jsp";

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NavigationUtil.forward(request, response, PRIVACY_JSP_PATH);
	}
}