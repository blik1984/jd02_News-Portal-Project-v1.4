package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.Role;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.CommentService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.MessageText;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.constant.RequestParam;
import edu.training.news_portal.web.util.NavigationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class HideComment implements Command {

	private final CommentService service = ServiceProvider.getInstance().getCommentService();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_AUTH, MessageText.PLEASE_SIGN_IN);
			return;
		}

		User user = (User) session.getAttribute(RequestAttr.AUTH_USER);
		if (user == null) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_AUTH, MessageText.PLEASE_SIGN_IN);
			return;
		}

		if (user.getRole() != Role.ADMINISTRATOR) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_USER_HOME, MessageText.NO_PERMISSION);
			return;
		}

		String commentIdStr = request.getParameter(RequestParam.COMMENT_ID);
		String newsIdStr = request.getParameter(RequestParam.NEWS_ID);

		if (commentIdStr == null || commentIdStr.isBlank() || newsIdStr == null || newsIdStr.isBlank()) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_USER_HOME, MessageText.INVALID_REQUEST);
			return;
		}

		try {
			int commentId = Integer.parseInt(commentIdStr);
			int newsId = Integer.parseInt(newsIdStr);

			service.hideComment(commentId);

			NavigationUtil.redirectWithParam(response, RequestPath.PAGE_NEWS, RequestParam.NEWS_ID, newsId);
		} catch (NumberFormatException e) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_USER_HOME, MessageText.INVALID_REQUEST);
		} catch (ServiceException e) {
			throw new ServletException(MessageText.UNKNOWN_ERROR, e);
		}
	}
}
