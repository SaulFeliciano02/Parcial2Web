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

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>

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
                    <a class="nav-link" href="/menu/1">Home
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

<!-- Page Content -->
<div class="container">

    <div class="row">

        <!-- Blog Entries Column -->
        <div class="col-md-8">
            <br><br>
            <h1 class="my-4">Page Heading
            </h1>

            <h2 class="my-4">Posts</h2>

            <!-- Post Creation Form -->
            <form method="post" action="/createPost" class="form-inline">
                <div class="form-group">
                    <input class="form-control" name="postTitle" placeholder="Title" type="text">
                </div>
                <button id="postPost" type="submit" class="btn btn-primary">Publish</button>
            </form>
            <br><br>

            <!--Tablas de links -->
            <table class="table">
                <thead>
                <tr>
                    <th>Link</th>
                    <th>Shortened Link</th>
                </tr>
                </thead>
                <tbody>
                    <tr>
                        <#if links?size != 0>
                            <#list links as link>
                                <td><a href="/shorty.com/${link.urlBase62?substring(12)}">"${link.urlBase62}"</a></td>
                            </#list>
                        </#if>
                    </tr>
                </tbody>
            </table>

            <!-- Blog Post -->
                    <div class="card mb-4">
                    </div>
            <!-- Pagination -->
            <ul class="pagination justify-content-center mb-4">
                <li id="listOlder">
                    <a id="olderButton" class="page-link" href="#">&larr; Older</a>
                </li>
                <li id="listNewer">
                    <a id="newerButton" class="page-link" href="#">Newer &rarr;</a>
                </li>
            </ul>

        </div>

        <!-- Sidebar Widgets Column -->
        <div class="col-md-4">
            <br><br>
            <!-- Search Widget -->
            <div class="card my-4">
                <h5 class="card-header">Search</h5>
                <div class="card-body">
                    <div class="input-group">
                        <input id="buscaTag" name="buscaTag" type="text" class="form-control" placeholder="Buscar por tag...">
                        <span class="input-group-btn">
                <button id="tagBusqueda" class="btn btn-secondary" type="button">Buscar</button>
              </span>
                    </div>
                </div>
            </div>

            <!-- Categories Widget -->
            <div class="card my-4">
                <h5 class="card-header">Categories</h5>
                <div class="card-body">
                    <div class="row">
                        <div class="col-lg-6">
                            <ul class="list-unstyled mb-0">
                                <li>
                                    <a href="#">Web Design</a>
                                </li>
                                <li>
                                    <a href="#">HTML</a>
                                </li>
                                <li>
                                    <a href="#">Freebies</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-lg-6">
                            <ul class="list-unstyled mb-0">
                                <li>
                                    <a href="#">JavaScript</a>
                                </li>
                                <li>
                                    <a href="#">CSS</a>
                                </li>
                                <li>
                                    <a href="#">Tutorials</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Side Widget -->
            <div class="card my-4">
                <h5 class="card-header">Side Widget</h5>
                <div class="card-body">
                    You can put anything you want inside of these side widgets. They are easy to use, and feature the new Bootstrap 4 card containers!
                </div>
            </div>

        </div>

    </div>
    <!-- /.row -->

</div>
<!-- /.container -->

<!-- Footer -->
<footer class="py-5 bg-dark">
    <div class="container">
        <p class="m-0 text-center text-white">Copyright &copy; Your Website 2019</p>
    </div>
    <!-- /.container -->
</footer>

</body>