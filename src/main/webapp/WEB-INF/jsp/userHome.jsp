<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<!DOCTYPE html>
<html lang="ru">
<head>
<meta charset="UTF-8" />
<title>Новостной Портал - Личный кабинет</title>
<meta name="viewport" content="width=device-width, initial-scale=1" />
<!-- Bootstrap CDN -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet" />
<style>
.news-card img {
	height: 180px;
	object-fit: cover;
}

.news-card {
	margin-bottom: 20px;
}

.category-section {
	margin-top: 40px;
}

.language-switcher button {
	margin-left: 5px;
}

footer {
	background-color: #f8f9fa;
	padding: 20px 0;
	margin-top: 40px;
	text-align: center;
}

.header-top {
	background-color: #f1f1f1;
	padding: 10px 0;
}
</style>
</head>
<body>

	<!-- Header -->
	<header class="header-top">
		<div
			class="container d-flex justify-content-between align-items-center">
			<!-- Logo -->
			<div>
				<a href="Controller?command=page_index"
					class="text-dark text-decoration-none fs-4 fw-bold">NewsPortal</a>
			</div>

			<!-- Language + News count selector -->
			<div class="d-flex align-items-center">
				<div class="language-switcher me-3">
					<span>Язык:</span> <a href="//Controller/SwitchLanguage?lang=ru"><button
							class="btn btn-sm btn-outline-secondary">RU</button></a> <a
						href="//Controller/SwitchLanguage?lang=en"><button
							class="btn btn-sm btn-outline-secondary">EN</button></a> <a
						href="//Controller/SwitchLanguage?lang=by"><button
							class="btn btn-sm btn-outline-secondary">BY</button></a>
				</div>

				<!-- News count selector -->
				<div>
					<form id="pageSizeForm" method="get" action="Controller">
						<input type="hidden" name="command" value="page_user_home" /> <label
							for="pageSize" class="me-2">Количество новостей:</label> <select
							id="pageSize" name="pageSize"
							class="form-select form-select-sm d-inline-block w-auto"
							onchange="document.getElementById('pageSizeForm').submit()">
							<c:forEach var="size" begin="2" end="10" step="2">
								<option value="${size}"
									${size == currentPageSize ? 'selected' : ''}>${size}</option>
							</c:forEach>
						</select>
					</form>
				</div>
			</div>

			<!-- User Info -->
			<div>
				<span class="me-3">Здравствуйте, <strong><c:out
							value="${sessionScope.auth.name}" default="Пользователь" /></strong>!
				</span> <a href="Controller?command=log_out"
					class="btn btn-sm btn-outline-danger">Выйти</a>
				<c:if
					test="${not empty sessionScope.auth and sessionScope.auth.role == 'ADMINISTRATOR'}">
					<a href="Controller?command=page_create_news"
						class="btn btn-sm btn-success ms-3">Добавить новость</a>
				</c:if>
			</div>
		</div>
	</header>

	<!-- Main Content -->
	<main class="container mt-4">
		<h2 class="mb-4">Все новости</h2>

		<div class="row">
			<c:forEach var="news" items="${requestScope.topNews}">
				<div class="col-md-4 news-card">
					<div class="card h-100">
						<div class="card-body d-flex flex-column">
							<span class="category-badge">${news.group}</span>
							<h5 class="card-title" style="font-size: 135%;">${news.title}</h5>
							<p class="card-text">${news.brief}</p>

							<!-- 🟢 Статус публикации (только для администратора) -->
							<c:if test="${requestScope.isAdmin}">
								<div class="mb-2">
									<c:choose>
										<c:when test="${news.publishingDateTime le requestScope.now}">
											<span class="badge bg-success">опубликовано</span>
										</c:when>
										<c:otherwise>
											<span class="badge bg-warning text-dark"> ожидает
												публикации: ${news.publishingDateTime} </span>
										</c:otherwise>
									</c:choose>
								</div>
							</c:if>

							<div class="mt-auto">
								<a
									href="Controller?${RequestParam.COMMAND}=page_news&${RequestParam.NEWS_ID}=${news.id}"
									class="btn btn-sm btn-outline-secondary">Читать далее</a>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
		<!-- Pagination -->
		<div class="mt-4 d-flex justify-content-center">
			<nav>
				<ul class="pagination">
					<c:forEach var="i" begin="1" end="${totalPages}">
						<li class="page-item ${i == currentPage ? 'active' : ''}"><a
							class="page-link"
							href="Controller?command=page_user_home&page=${i}">${i}</a></li>
					</c:forEach>
				</ul>
			</nav>
		</div>
	</main>


	<!-- Footer -->
	<footer>
		<div class="container">
			<p class="mb-0">
				© 2025 NewsPortal. Все права защищены. | <a
					href="Controller?command=page_privacy">Политика
					конфиденциальности</a>
			</p>
		</div>
	</footer>

	<!-- Bootstrap JS (опционально) -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>