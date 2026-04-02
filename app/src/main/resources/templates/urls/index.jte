@import hexlet.code.util.DateFormatter
@param java.util.List<hexlet.code.model.Url> urls
@param java.util.Map<java.lang.Long, hexlet.code.model.UrlCheck> lastChecks
@param String flash

@template.layout(
pageTitle = "Сайты",
flash = flash,
content = @`
    <h1 class="mb-4">Сайты</h1>

    <table class="table table-bordered table-hover" data-test="urls">
        <thead>
            <tr>
                <th>ID</th>
                <th>Имя</th>
                <th>Дата создания</th>
                <th>Последняя проверка</th>
                <th>Код ответа</th>
            </tr>
        </thead>
        <tbody>
            @for(hexlet.code.model.Url url : urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${DateFormatter.format(url.getCreatedAt())}</td>
                    <td>${lastChecks.get(url.getId()) != null ? DateFormatter.format(lastChecks.get(url.getId()).getCreatedAt()) : ""}</td>
                    <td>${lastChecks.get(url.getId()) != null ? String.valueOf(lastChecks.get(url.getId()).getStatusCode()) : ""}</td>
                </tr>
            @endfor
        </tbody>
    </table>
`)