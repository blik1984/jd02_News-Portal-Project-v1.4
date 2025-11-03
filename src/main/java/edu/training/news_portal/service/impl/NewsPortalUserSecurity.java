package edu.training.news_portal.service.impl;

import edu.training.news_portal.bean.RegistrationInfo;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.dao.DaoException;
import edu.training.news_portal.dao.DaoProvider;
import edu.training.news_portal.dao.UserDao;
import edu.training.news_portal.service.RegistrationInfoValidator;
import edu.training.news_portal.service.ServiceException;
import edu.training.news_portal.service.UserSecurity;

public class NewsPortalUserSecurity implements UserSecurity {

	private final UserDao userDao = DaoProvider.getInstance().getUserDao();

	@Override
	public User signIn(String login, String password) throws ServiceException {

		try {
			return userDao.checkCredentials(login, password)
					.orElseThrow(() -> new ServiceException("User not found or invalid credentials"));
		} catch (DaoException e) {
			throw new ServiceException("Failed to authenticate user", e);
		}
	}

	@Override
	public boolean registration(RegistrationInfo info) throws ServiceException {

		RegistrationInfoValidator.validate(info);

		try {
			if (userDao.emailExists(info.getEmail())) {
				throw new ServiceException("A user with this email already exists");
			}

			return userDao.registration(info);

		} catch (DaoException e) {
			throw new ServiceException("Error occurred during user registration", e);
		}
	}

}
