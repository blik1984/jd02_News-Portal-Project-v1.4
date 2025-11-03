package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.RegistrationInfo;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.service.UserSecurity;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.constant.MessageText;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.util.UserUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DoRegistration implements Command {

	private final UserSecurity userSecurity = ServiceProvider.getInstance().getUserSecurity();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RegistrationInfo regInfo = UserUtil.extractRegistrationInfo(request);

		try {
			userSecurity.registration(regInfo);
			NavigationUtil.redirect(response, RequestPath.PAGE_AUTH);
		} catch (ServiceException e) {
			String message = (e.getMessage() != null) ? e.getMessage() : MessageText.ERROR_REGISTRATION;
			request.setAttribute(RequestAttr.ERROR_MESSAGE, message);
			NavigationUtil.forward(request, response, "WEB-INF/jsp/registration.jsp");
		}
	}
}