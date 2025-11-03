package edu.training.news_portal.service;

import edu.training.news_portal.bean.RegistrationInfo;
import edu.training.news_portal.bean.User;

public interface UserSecurity {
	
	User signIn(String login, String Password) throws ServiceException;
	
	boolean registration(RegistrationInfo info) throws ServiceException;
	
}
