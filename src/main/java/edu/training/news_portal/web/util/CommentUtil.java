package edu.training.news_portal.web.util;

import java.io.IOException;
import java.time.LocalDateTime;

import edu.training.news_portal.bean.Comment;
import edu.training.news_portal.bean.Role;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.CommentService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.RequestAttr;
import edu.training.news_portal.web.constant.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CommentUtil {

	private CommentUtil() {
	}

	public static Comment buildUserComment(User user, int newsId, String text) {
		return new Comment.CommentBuilder().newsId(newsId).userId(user.getId()).userName(user.getName()).text(text)
				.createdAt(LocalDateTime.now()).statusId(1).editable(true).build();
	}

	public static void setEditingCommentId(HttpServletRequest request) {
		String editingCommentIdStr = request.getParameter(RequestParam.EDITING_COMMENT_ID);
		if (editingCommentIdStr != null) {
			try {
				int editingCommentId = Integer.parseInt(editingCommentIdStr);
				request.setAttribute(RequestAttr.EDITING_COMMENT_ID, editingCommentId);
			} catch (NumberFormatException e) {
				// silently ignore invalid format
			}
		}
	}

	public static int parseCommentId(HttpServletRequest request, String param) {
		return Integer.parseInt(request.getParameter(param));
	}

	public static String getCommentText(HttpServletRequest request, String param) {
		return request.getParameter(param).trim();
	}

	public static void updateComment(CommentService service, int commentId, String text, User user,
			HttpServletResponse response) throws IOException, ServiceException {

		Comment existingComment = service.getCommentById(commentId);
		if (existingComment == null) {
			NavigationUtil.redirect(response, RequestPath.PAGE_USER_HOME);
			return;
		}

		boolean isOwner = existingComment.getUserId() == user.getId();
		boolean isAdmin = user.getRole() == Role.ADMINISTRATOR;
		boolean within30Min = existingComment.getCreatedAt().plusMinutes(30).isAfter(LocalDateTime.now());

		if (!isAdmin && (!isOwner || !within30Min)) {
			NavigationUtil.redirectWithParam(response, RequestPath.PAGE_NEWS, RequestParam.NEWS_ID,
					existingComment.getNewsId());
			return;
		}

		existingComment.setText(text);
		existingComment.setUpdatedAt(LocalDateTime.now());

		service.updateComment(existingComment, user);

		NavigationUtil.redirectWithParam(response, RequestPath.PAGE_NEWS, RequestParam.NEWS_ID,
				existingComment.getNewsId());
	}
}