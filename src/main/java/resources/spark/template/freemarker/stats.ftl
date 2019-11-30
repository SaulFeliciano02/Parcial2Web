<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>The ERP Blog</title>

    <!-- Bootstrap core CSS -->
    <!--<link href="resources/publico/startbootstrap-blog-home-gh-pages/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">-->

    <!-- Custom styles for this template -->
    <!--<link href="resources/publico/startbootstrap-blog-home-gh-pages/css/blog-home.css" rel="stylesheet">-->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="js/qrcode.min.js"></script>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>


</head>

<body>

<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#">The ERP Url Shorter</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="/">Home
                        <span class="sr-only">(current)</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">About</a>
                </li>
                <#if loggedUser?exists>
                    <li class="nav-item">
                        <a class="nav-link" href="#">${loggedUser.username}</a>
                    </li>
                </#if>
                <#if loggedUser?exists && loggedUser.administrador == true>
                    <li class="nav-item">
                        <a class="nav-link" href="/administradores">Autores</a>
                    </li>
                </#if>
                <#if loggedUser?exists>
                    <li class="nav-item">
                        <a class="nav-link" href="/disconnect">Desconectar</a>
                    </li>
                <#else>
                    <li class="nav-item">
                        <a class="nav-link" href="/login">Login</a>
                    </li>
                </#if>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <br><br>
            <h1 class="my-4">Stats
            </h1>

            <h2 class="my-4">Charts</h2>
            <div class="row">
                <div class="col-md-6">
                    <div id="columnchart_values"></div>
                </div>
                <div class="col-md-6">
                    <div id="columnchartdays_values"></div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div id="columncharthours_values"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    // Load the Visualization API and the corechart package.
    google.charts.load('current', {'packages':['corechart']});

    // Set a callback to run when the Google Visualization API is loaded.
    google.charts.setOnLoadCallback(drawChart);
    function drawChart() {
        var data = google.visualization.arrayToDataTable([
            ['Webpage', 'Visitations', { role: 'style' } ],
            ['Chrome', ${chromeVisits}, 'color: yellow'],
            ['Firefox', ${firefoxVisits}, 'color: orange'],
            ['Opera', ${operaVisits}, 'color: red'],
            ['Edge', ${edgeVisits}, 'color: blue'],
            ['Safari', ${safariVisits}, 'color: grey']
        ]);
        var options = {
            title: "Visits by webpage",
            width: 600,
            height: 400,
            bar: {groupWidth: "95%"},
            legend: { position: "none" },
        };
        var chart = new google.visualization.ColumnChart(document.getElementById("columnchart_values"));
        chart.draw(data, options);
    }

    google.charts.setOnLoadCallback(drawChartDayOfWeek);
    function drawChartDayOfWeek() {
        var data = google.visualization.arrayToDataTable([
            ['Webpage', 'Visitations', { role: 'style' } ],
            ['Lunes', ${mondayVisits}, 'color: blue'],
            ['Martes', ${tuesdayVisits}, 'color: blue'],
            ['Miercoles', ${wednesdayVisits}, 'color: blue'],
            ['Jueves', ${thursdayVisits}, 'color: blue'],
            ['Viernes', ${fridayVisits}, 'color: blue'],
            ['Sabado', ${saturdayVisits}, 'color: blue'],
            ['Domingo', ${sundayVisits}, 'color: blue']
        ]);
        var options = {
            title: "Visits by day of the week",
            width: 600,
            height: 400,
            bar: {groupWidth: "95%"},
            legend: { position: "none" },
        };
        var chart = new google.visualization.ColumnChart(document.getElementById("columnchartdays_values"));
        chart.draw(data, options);
    }

    google.charts.setOnLoadCallback(drawChartHour);
    function drawChartHour() {
        var data = google.visualization.arrayToDataTable([
            ['Webpage', 'Visitations', { role: 'style' } ],
            ['00:00', ${zero}, 'color: blue'],
            ['01:00', ${one}, 'color: blue'],
            ['02:00', ${two}, 'color: blue'],
            ['03:00', ${three}, 'color: blue'],
            ['04:00', ${four}, 'color: blue'],
            ['05:00', ${five}, 'color: blue'],
            ['06:00', ${six}, 'color: blue'],
            ['07:00', ${seven}, 'color: blue'],
            ['08:00', ${eight}, 'color: blue'],
            ['09:00', ${nine}, 'color: blue'],
            ['10:00', ${ten}, 'color: blue'],
            ['11:00', ${eleven}, 'color: blue'],
            ['12:00', ${twelve}, 'color: blue'],
            ['13:00', ${thirteen}, 'color: blue'],
            ['14:00', ${fourteen}, 'color: blue'],
            ['15:00', ${fifteen}, 'color: blue'],
            ['16:00', ${sixteen}, 'color: blue'],
            ['17:00', ${seventeen}, 'color: blue'],
            ['18:00', ${eighteen}, 'color: blue'],
            ['19:00', ${nineteen}, 'color: blue'],
            ['20:00', ${twenty}, 'color: blue'],
            ['21:00', ${twenty_one}, 'color: blue'],
            ['22:00', ${twenty_two}, 'color: blue'],
            ['23:00', ${twenty_three}, 'color: blue']
        ]);
        var options = {
            title: "Visits by hour",
            width: 1200,
            height: 400,
            bar: {groupWidth: "95%"},
            legend: { position: "none" },
        };
        var chart = new google.visualization.ColumnChart(document.getElementById("columncharthours_values"));
        chart.draw(data, options);
    }
</script>

</body>