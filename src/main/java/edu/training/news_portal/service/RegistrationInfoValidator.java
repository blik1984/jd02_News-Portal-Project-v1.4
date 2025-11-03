package edu.training.news_portal.service;

import java.util.regex.Pattern;

import edu.training.news_portal.bean.RegistrationInfo;

public class RegistrationInfoValidator {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");

	public static void validate(RegistrationInfo info) throws ServiceException {
		if (info == null) {
			throw new ServiceException("Registration info cannot be null");
		}

		String email = info.getEmail();
		String name = info.getName();
		String password = info.getPassword();

		if (email == null || email.isBlank()) {
			throw new ServiceException("Email cannot be empty");
		}
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new ServiceException("Invalid email format");
		}

		if (name == null || name.isBlank()) {
			throw new ServiceException("Name cannot be empty");
		}
		if (name.length() < 2 || name.length() > 50) {
			throw new ServiceException("Name must be between 2 and 50 characters");
		}

		if (password == null || password.isBlank()) {
			throw new ServiceException("Password cannot be empty");
		}
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new ServiceException("Password must be at least 8 characters and contain both letters and numbers");
		}
	}
}