package edu.training.news_portal.dao;

import java.util.Optional;

import edu.training.news_portal.bean.RegistrationInfo;
import edu.training.news_portal.bean.User;

public interface UserDao {
	Optional<User> checkCredentials(String login, String password) throws DaoException;
	boolean registration(RegistrationInfo info)throws DaoException;
	boolean emailExists(String email) throws DaoException;

}
