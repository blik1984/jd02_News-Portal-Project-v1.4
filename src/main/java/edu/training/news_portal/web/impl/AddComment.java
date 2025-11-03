package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.Comment;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.CommentService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.MessageText;
import edu.training.news_portal.web.constant.RequestParam;
import edu.training.news_portal.web.util.AuthUtil;
import edu.training.news_portal.web.util.CommentUtil;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.util.RequestUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AddComment implements Command {

	private final CommentService commentService = ServiceProvider.getInstance().getCommentService();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (!AuthUtil.isAuthenticated(session)) {
			NavigationUtil.redirectWithMessage(response, RequestPath.PAGE_AUTH, MessageText.PLEASE_SIGN_IN);
			return;
		}

		User user = AuthUtil.getAuthenticatedUser(session);

		String text = RequestUtil.getRequiredParam(request, RequestParam.COMMENT_TEXT);
		int newsId = RequestUtil.parseIntParam(request, RequestParam.NEWS_ID);

		Comment comment = CommentUtil.buildUserComment(user, newsId, text);

		try {
			commentService.addComment(comment);
			NavigationUtil.redirectWithParam(response, RequestPath.PAGE_NEWS, RequestParam.NEWS_ID,
					comment.getNewsId());
		} catch (ServiceException e) {
			e.printStackTrace();
			NavigationUtil.redirect(response, RequestPath.PAGE_ERROR);
		}
	}
}