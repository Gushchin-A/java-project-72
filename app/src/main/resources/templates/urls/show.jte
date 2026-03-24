@param hexlet.code.model.Url url
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
                <td>${url.getCreatedAt().toString()}</td>
            </tr>
        </tbody>
    </table>
`)