@import hexlet.code.util.TextTruncate
@import hexlet.code.util.DateFormatter
@param hexlet.code.model.Url url
@param java.util.List<hexlet.code.model.UrlCheck> checks
@param String flash

@template.layout(
pageTitle = "Сайт " + url.getName(),
flash = flash,
content = @`
    <h1 class="mb-4">Сайт: ${url.getName()}</h1>

    <table class="table table-bordered" data-test="url">
        <tbody>
            <tr>
                <td>ID</td>
                <td>${url.getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${url.getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${DateFormatter.format(url.getCreatedAt())}</td>
            </tr>
        </tbody>
    </table>

    <form method="post" action="/urls/${url.getId()}/checks">
        <input type="submit" value="Запустить проверку">
    </form>

        <h2>Проверки</h2>

        <table class="table table-bordered" data-test="checks">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Код ответа</th>
                    <th>h1</th>
                    <th>title</th>
                    <th>description</th>
                    <th>Дата создания</th>
                </tr>
            </thead>
            <tbody>
                @for(hexlet.code.model.UrlCheck check : checks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${TextTruncate.truncate(check.getH1())}</td>
                        <td>${TextTruncate.truncate(check.getTitle())}</td>
                        <td>${TextTruncate.truncate(check.getDescription())}</td>
                        <td>${DateFormatter.format(check.getCreatedAt())}</td>
                    </tr>
                @endfor
            </tbody>
        </table>
`)