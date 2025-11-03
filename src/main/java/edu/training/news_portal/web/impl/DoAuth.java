package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.service.UserSecurity;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.MessageText;
import edu.training.news_portal.web.constant.SessionAttr;
import edu.training.news_portal.web.constant.RequestParam;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.util.RequestUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DoAuth implements Command {

	private final UserSecurity security = ServiceProvider.getInstance().getUserSecurity();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String email = RequestUtil.getRequiredParam(request, RequestParam.EMAIL);
		String password = RequestUtil.getRequiredParam(request, RequestParam.PASSWORD);

		try {
			User user = security.signIn(email, password);

			if (user == null) {
				NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_AUTH, MessageText.INVALID_CREDENTIALS);
				return;
			}

			HttpSession session = request.getSession(true);
			session.setAttribute(SessionAttr.AUTH_USER, user);

			NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);

		} catch (ServiceException e) {
			e.printStackTrace();
			NavigationUtil.redirect(response, RequestPath.PAGE_ERROR);
		}
	}
}