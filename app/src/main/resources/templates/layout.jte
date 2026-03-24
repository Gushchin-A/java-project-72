@param String pageTitle
@param String flash
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
<body class="min-vh-100 d-flex flex-column">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
        <div class="navbar-nav">
        <a class="nav-link" href="/urls">Сайты</a>
        </div>
    </div>
</nav>
<main class="container mt-4 flex-grow-1">
	@if(flash != null)
        <div class="alert alert-info" role="alert">
            ${flash}
        </div>
    @endif
	${content}
</main>
<footer class="border-top py-3 mt-auto">
    <div class="container text-center">
        <a href="https://hexlet.io">Hexlet</a>
    </div>
</footer>
</body>
</html>