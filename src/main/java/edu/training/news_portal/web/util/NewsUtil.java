package edu.training.news_portal.web.util;

import java.time.LocalDateTime;

import edu.training.news_portal.bean.News;
import edu.training.news_portal.bean.NewsGroups;
import edu.training.news_portal.bean.User;
import edu.training.news_portal.web.constant.MessageText;
import edu.training.news_portal.web.constant.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

public final class NewsUtil {

	private NewsUtil() {
	}

	public static NewsGroups parseGroup(String groupParam) {
		try {
			return NewsGroups.valueOf(groupParam);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(MessageText.INVALID_NEWS_GROUP);
		}
	}

	public static News buildNewsFromRequest(HttpServletRequest request, User publisher) {
		String title = RequestUtil.getRequiredParam(request, RequestParam.TITLE);
		String brief = RequestUtil.getRequiredParam(request, RequestParam.BRIEF);
		String content = RequestUtil.getRequiredParam(request, RequestParam.CONTENT);
		String groupStr = RequestUtil.getRequiredParam(request, RequestParam.GROUP);
		LocalDateTime publishDate = RequestUtil.parseDateTimeParam(request, RequestParam.PUBLISH_DATE_TIME);

		NewsGroups group = parseGroup(groupStr);

		News news = new News();
		news.setTitle(title);
		news.setBrief(brief);
		news.setContent(content);
		news.setGroup(group);
		news.setPublishingDateTime(publishDate);
		news.setPublisher(publisher);

		return news;
	}

	public static int parseNewsId(HttpServletRequest request) {
		String idParam = request.getParameter(RequestParam.NEWS_ID);
		if (idParam == null) {
			throw new IllegalArgumentException("News ID is missing");
		}
		try {
			return Integer.parseInt(idParam);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid News ID format");
		}
	}

	public static boolean isPublished(News news) {
		if (news == null || news.getPublishingDateTime() == null) {
			return false;
		}
		return !LocalDateTime.now().isBefore(news.getPublishingDateTime());
	}
}