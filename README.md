# simulant-bootstrap

Simulant bootstrap project showcasing how to simulate traffic to a simple API

Consist of 2 projects;

  - site, simple CRUD site under test
  - sim, the simulant tester

Described in detail in [this blog post](http://martintrojer.github.io/clojure/2013/09/29/testing-an-api-with-simulant/).

## Usage

See `site/README.md` and `sim/README.md`.

## Beware, This Example Has Major Problems

If you use this example as inspiration, do not replicate the following
problems:

* Global connection state management in db.clj.
* Choosing driver data randomly at sim time, not test time.
* Recording sim-specific results on agents & actions, which breaks the
  separation between the test an sim layers.

## License

Copyright © 2013 Martin Trojer

Distributed under the Eclipse Public License, the same as Clojure.
