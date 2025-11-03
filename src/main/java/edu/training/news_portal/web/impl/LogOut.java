package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.SessionAttr;
import edu.training.news_portal.web.util.AuthUtil;
import edu.training.news_portal.web.util.NavigationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogOut implements Command {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session != null && AuthUtil.isAuthenticated(session)) {
			session.removeAttribute(SessionAttr.AUTH_USER); 
		}
		NavigationUtil.redirect(response, RequestPath.PAGE_MAIN);
	}
}