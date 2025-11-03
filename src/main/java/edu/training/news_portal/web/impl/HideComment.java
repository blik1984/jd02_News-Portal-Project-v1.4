package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.bean.Role;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.service.CommentService;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.ServiceProvider;
import edu.training.news_portal.web.Command;
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
			throw new IllegalStateException("User is not authenticated");
		}

		User user = (User) session.getAttribute("auth");
		if (user == null) {
			throw new IllegalStateException("User is not authenticated");
		}

		if (!(user.getRole() == Role.ADMINISTRATOR)) {
			throw new IllegalStateException("You don’t have permission to perform this action");
		}

		String commentId = request.getParameter("commentId");

		if (commentId == null || commentId.trim().isEmpty()) {
			throw new IllegalArgumentException("Commentaries ID is missing.");
		}

		String newsId = request.getParameter("newsId");
		if (newsId == null || newsId.trim().isEmpty()) {
			throw new IllegalArgumentException("News ID is missing.");
		}

		int commentariesId;
		int Id;

		try {
			commentariesId = Integer.parseInt(commentId);
			Id = Integer.parseInt(newsId);

			service.hideComment(commentariesId);

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid comment or news ID format.", e);
		} catch (ServiceException e) {
			throw new ServletException("Failed to hide comment.", e);
		}

		response.sendRedirect("Controller?command=page_news&newsId=" + Id);
	}

}
