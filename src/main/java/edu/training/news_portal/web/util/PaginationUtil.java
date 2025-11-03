package edu.training.news_portal.web.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import edu.training.news_portal.web.constant.RequestParam;
import edu.training.news_portal.web.constant.SessionAttr;

public final class PaginationUtil {

	private PaginationUtil() {
	}

	private static final int DEFAULT_PAGE_SIZE = 6;

	public static int parsePage(HttpServletRequest request) {
		try {
			int page = Integer.parseInt(request.getParameter(RequestParam.PAGE));
			return Math.max(page, 1);
		} catch (Exception e) {
			return 1;
		}
	}

	public static int resolvePageSize(HttpServletRequest request, HttpSession session) {
		int result = DEFAULT_PAGE_SIZE;
		String param = request.getParameter(RequestParam.PAGE_SIZE);

		if (param != null) {
			try {
				int value = Integer.parseInt(param);
				if (value >= 2 && value <= 10 && value % 2 == 0) {
					session.setAttribute(SessionAttr.PAGE_SIZE, value);
					return value;
				}
			} catch (NumberFormatException ignored) {
			}
		}

		Integer stored = (Integer) session.getAttribute(SessionAttr.PAGE_SIZE);
		return (stored != null) ? stored : result;
	}
}