package edu.training.news_portal.web.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtil {

	private RequestUtil() {
	}

	public static String getRequiredParam(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing or empty parameter: " + paramName);
		}
		return value.trim();
	}

	public static int parseIntParam(HttpServletRequest request, String paramName) {
		String value = getRequiredParam(request, paramName);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number format for parameter: " + paramName);
		}
	}

	public static LocalDateTime parseDateTimeParam(HttpServletRequest request, String paramName) {
		String value = getRequiredParam(request, paramName);
		try {
			return LocalDateTime.parse(value);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date-time format for parameter: " + paramName);
		}
	}
}