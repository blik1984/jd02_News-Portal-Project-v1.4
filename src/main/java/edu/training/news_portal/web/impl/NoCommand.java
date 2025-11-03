package edu.training.news_portal.web.impl;

import java.io.IOException;

import edu.training.news_portal.web.Command;
import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.util.NavigationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NoCommand implements Command {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NavigationUtil.redirect(response, RequestPath.PAGE_ERROR);
	}
}