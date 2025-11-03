package edu.training.news_portal.web.filters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import edu.training.news_portal.bean.Role;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.web.AllowedRoles;
import edu.training.news_portal.web.RequestPath;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class SecurityFilter implements Filter {

	private static final Map<Role, Set<RequestPath>> roleAccessMap = new EnumMap<>(Role.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);
		String commandParam = httpRequest.getParameter("command");

		if (commandParam == null) {
			forwardWithError(request, response, "Unknown command.", "page_main");
			return;
		}

		try {
			RequestPath command = RequestPath.valueOf(commandParam.toUpperCase());

			Role userRole = resolveUserRole(session);

			if (!isCommandAllowedForRole(command, userRole)) {
				forwardWithError(request, response, "Access denied. Insufficient privileges.", "page_auth");
				return;
			}

			chain.doFilter(request, response);

		} catch (IllegalArgumentException e) {
			forwardWithError(request, response, "Unknown command.", "page_main");
		}
	}

	private Role resolveUserRole(HttpSession session) {
		if (session == null) {
			return Role.GUEST;
		}
		User user = (User) session.getAttribute("auth");
		return (user != null && user.getRole() != null) ? user.getRole() : Role.GUEST;
	}

	private void forwardWithError(ServletRequest request, ServletResponse response, String message, String command)
			throws ServletException, IOException {
		request.setAttribute("errorMessage", message);
		request.getRequestDispatcher("Controller?command=" + command).forward(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) {
		for (Role role : Role.values()) {
			roleAccessMap.put(role, EnumSet.noneOf(RequestPath.class));
		}

		for (RequestPath path : RequestPath.values()) {
			AllowedRoles annotation = getAllowedRoles(path);
			if (annotation != null) {
				for (Role role : annotation.value()) {
					roleAccessMap.get(role).add(path);
				}
			}
		}
	}

	private boolean isCommandAllowedForRole(RequestPath command, Role role) {
		AllowedRoles annotation = getAllowedRoles(command);
		if (annotation == null) {
			return false;
		}

		return Arrays.asList(annotation.value()).contains(role);
	}

	private AllowedRoles getAllowedRoles(RequestPath path) {

		try {
			Field field = RequestPath.class.getField(path.name());
			return field.getAnnotation(AllowedRoles.class);

		} catch (NoSuchFieldException e) {
			return null;
		}
	}

}
