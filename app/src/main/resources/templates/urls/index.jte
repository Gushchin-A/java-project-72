@param java.util.List<hexlet.code.model.Url> urls
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
                <th>Код ответа</th>
            </tr>
        </thead>
        <tbody>
            @for(hexlet.code.model.Url url : urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getCreatedAt().toString()}</td>
                    <td></td>
                </tr>
            @endfor
        </tbody>
    </table>
`)