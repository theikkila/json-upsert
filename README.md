# json-upsert

Executes SQL-queries with parameters from json-array

## Usage

    $ java -jar json-upsert-0.1.0-standalone.jar [args]

## Options

```
Usage: json-upsert [options] action

Options:
  -p, --port PORT         3306                                                                                            Database port number
  -i, --input INPUT.json  import.json                                                                                     Input filename
  -q, --query QUERY       INSERT INTO test (a, b, score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE score = values(score);  SQL query to be executed for every JSON array in params. Substitute variables with ?
  -u, --user USER         root                                                                                            Database username
  -w, --pass PASSWORD     password                                                                                        Database password
  -d, --database NAME     test                                                                                            Database name
  -t, --host HOSTNAME     localhost                                                                                       Database hostname

Actions:
  import    Import JSON file

```

## License

Copyright © 2015 Teemu Heikkilä

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
