package edu.training.news_portal.dao;

public final class DbColumn {

	private DbColumn() {
	}

	public static final class User {
		public static final String ID = "id";
		public static final String EMAIL = "email";
		public static final String PASSWORD = "password";
		public static final String ROLE_ID = "roles_idroles";
		public static final String STATUS_ID = "user_status_id";
		public static final String REGISTRATION_DATE = "registration_date";
		public static final String UPDATED_DATE = "updated_date";
	}

	public static final class UserDetails {
		public static final String USER_ID = "users_id";
		public static final String NAME = "name";
	}

	public static final class Role {
		public static final String ID = "idroles";
		public static final String NAME = "name";
		public static final String ALIAS = "role"; // используется для alias в SQL
	}

	public static final class News {
		public static final String ID = "id";
		public static final String NEWS_GROUP_ID = "news_group_id";
		public static final String TITLE = "title";
		public static final String BRIEF = "brief";
		public static final String CONTENT_PATH = "content_path";
		public static final String CREATE_DATE = "create_date";
		public static final String UPDATED_DATE = "updated_date";
		public static final String PUBLISH_DATE = "publish_date";
		public static final String USER_ID = "user_id";
	}

	public static final class NewsGroup {
		public static final String ID = "id";
		public static final String VALUE = "value";
	}

	public static final class Comment {
		public static final String ID = "id";
		public static final String CONTENT = "content";
		public static final String CREATED_DATE = "created_date";
		public static final String UPDATED_DATE = "updated_date";
		public static final String USER_ID = "users_id";
		public static final String NEWS_ID = "news_id";
		public static final String STATUS_ID = "commentaries_status_id";
		public static final String STATUS = "value";
	}
}
