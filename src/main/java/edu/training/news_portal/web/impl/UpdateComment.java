package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.service.CommentService;
import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.constant.RequestParam;
import edu.training.news_portal.web.util.AuthUtil;
import edu.training.news_portal.web.util.CommentUtil;
import edu.training.news_portal.web.util.NavigationUtil;
import edu.training.news_portal.web.RequestPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UpdateComment implements Command {

	private final CommentService commentService = ServiceProvider.getInstance().getCommentService();

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		User currentUser = AuthUtil.getAuthenticatedUser(request.getSession(false));
		if (currentUser == null) {
			NavigationUtil.redirect(response, RequestPath.PAGE_AUTH);
			return;
		}

		try {
			int commentId = CommentUtil.parseCommentId(request, RequestParam.COMMENT_ID);
			String commentText = CommentUtil.getCommentText(request, RequestParam.COMMENT_TEXT);

			if (commentText.isBlank()) {
				NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);
				return;
			}

			CommentUtil.updateComment(commentService, commentId, commentText, currentUser, response);

		} catch (NumberFormatException | ServiceException e) {
			NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);
		}
	}
}