package edu.training.news_portal.web.util;

import jakarta.servlet.http.HttpSession;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.web.constant.SessionAttr;

public final class AuthUtil {

	private AuthUtil() {
	}

	public static boolean isAuthenticated(HttpSession session) {
		return session != null && session.getAttribute(SessionAttr.AUTH_USER) != null;
	}

	public static User getAuthenticatedUser(HttpSession session) {
		if (session == null) {
			throw new IllegalStateException("User is not authenticated (no session).");
		}
		User user = (User) session.getAttribute(SessionAttr.AUTH_USER);
		if (user == null) {
			throw new IllegalStateException("User is not authenticated (no auth user).");
		}
		return user;
	}

}