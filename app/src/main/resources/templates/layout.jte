@param String pageTitle
@param gg.jte.Content content

<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
	rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
	crossorigin="anonymous">
</head>
<body>
<nav class="navbar navbar-light bg-white mb-3">
    <div class="container">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
    </div>
</nav>
<main class="container">
	<div id="flash-container"></div>
	${content}
</main>
</body>
</html>