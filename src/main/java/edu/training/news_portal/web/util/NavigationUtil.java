package edu.training.news_portal.web.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import edu.training.news_portal.web.RequestPath;
import edu.training.news_portal.web.constant.RequestParam;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class NavigationUtil {

	private NavigationUtil() {
	}

	public static void redirect(HttpServletResponse response, RequestPath command) throws IOException {
		response.sendRedirect(buildControllerUrl(command));
	}

	public static void redirectWithMessage(HttpServletResponse response, RequestPath command, String message)
			throws IOException {
		String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
		response.sendRedirect(buildControllerUrl(command) + "&" + RequestParam.MESSAGE + "=" + encoded);
	}

	public static void redirectWithParam(HttpServletResponse response, RequestPath command, String paramName,
			Object value) throws IOException {
		response.sendRedirect(buildControllerUrl(command) + "&" + paramName + "=" + value);
	}

	public static void forward(HttpServletRequest request, HttpServletResponse response, String jspPath)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
		dispatcher.forward(request, response);
	}

	private static String buildControllerUrl(RequestPath command) {
		return "Controller?" + RequestParam.COMMAND + "=" + command.name();
	}
}