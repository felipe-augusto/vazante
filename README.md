# vazante

## Primeiros passos

Primeiramente, clone o repositório
https://github.com/felipe-augusto/vazante.git






sbt run (hot reload??) ou sbt run

https://developers.google.com/maps/documentation/javascript/examples/directions-simple?hl=pt-br

https://www.experts-exchange.com/questions/26617895/Plotting-multiple-directions-using-Google-maps-API.html

Lembrar de restringir a chave de acesso!

A barebones Scala app (using the Play framework), which can easily be deployed to Heroku.  

This application support the [Getting Started with Scala/Play on Heroku](https://devcenter.heroku.com/articles/getting-started-with-scala) article - check it out.

## Running Locally

Make sure you have Play and sbt installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

```sh
$ git clone https://github.com/heroku/scala-getting-started.git
$ cd scala-getting-started
$ sbt compile stage
$ heroku local
```

Your app should now be running on [localhost:5000](http://localhost:5000/).

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

or

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Documentation

For more information about using Play and Scala on Heroku, see these Dev Center articles:

- [Play and Scala on Heroku](https://devcenter.heroku.com/categories/language-support#scala-and-play)

