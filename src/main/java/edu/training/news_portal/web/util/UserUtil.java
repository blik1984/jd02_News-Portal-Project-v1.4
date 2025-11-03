package edu.training.news_portal.web.util;

import edu.training.news_portal.bean.RegistrationInfo;
import jakarta.servlet.http.HttpServletRequest;
import edu.training.news_portal.web.constant.RequestParam;

public final class UserUtil {

	private UserUtil() {
	}

	public static RegistrationInfo extractRegistrationInfo(HttpServletRequest request) {
		String email = request.getParameter(RequestParam.EMAIL);
		String password = request.getParameter(RequestParam.PASSWORD);
		String name = request.getParameter(RequestParam.NAME);

		return new RegistrationInfo.RegBuilder().email(email).password(password).name(name).build();
	}
}