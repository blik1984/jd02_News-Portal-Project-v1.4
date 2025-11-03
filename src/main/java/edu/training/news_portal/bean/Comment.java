package edu.training.news_portal.bean;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {

	private int id;
	private int newsId;
	private int userId;
	private String userName;
	private String text;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int statusId;
	private String status;
	private boolean editable;

	public Comment() {
	}

	private Comment(CommentBuilder builder) {
		this.id = builder.id;
		this.newsId = builder.newsId;
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.text = builder.text;
		this.createdAt = builder.createdAt;
		this.updatedAt = builder.updatedAt;
		this.statusId = builder.statusId;
		this.status = builder.status;
		this.editable = builder.editable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public static class CommentBuilder implements Builder<Comment> {
		private int id;
		private int newsId;
		private int userId;
		private String userName;
		private String text;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private int statusId;
		private String status;
		private boolean editable;

		public CommentBuilder() {
		}

		public CommentBuilder id(int id) {
			this.id = id;
			return this;
		}

		public CommentBuilder newsId(int newsId) {
			this.newsId = newsId;
			return this;
		}

		public CommentBuilder userId(int userId) {
			this.userId = userId;
			return this;
		}

		public CommentBuilder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public CommentBuilder text(String text) {
			this.text = text;
			return this;
		}

		public CommentBuilder createdAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public CommentBuilder updatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
			return this;
		}

		public CommentBuilder statusId(int statusId) {
			this.statusId = statusId;
			return this;
		}

		public CommentBuilder status(String status) {
			this.status = status;
			return this;
		}

		public CommentBuilder editable(boolean editable) {
			this.editable = editable;
			return this;
		}

		@Override
		public Comment build() {
			return new Comment(this);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdAt, editable, id, newsId, status, statusId, text, updatedAt, userId, userName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return id == other.id && newsId == other.newsId && userId == other.userId && statusId == other.statusId
				&& editable == other.editable && Objects.equals(userName, other.userName)
				&& Objects.equals(text, other.text) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(updatedAt, other.updatedAt) && Objects.equals(status, other.status);
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", newsId=" + newsId + ", userId=" + userId + ", userName=" + userName + ", text="
				+ text + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", statusId=" + statusId
				+ ", status=" + status + ", editable=" + editable + "]";
	}
}