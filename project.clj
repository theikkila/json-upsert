(defproject json-upsert "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
  				 [org.clojure/java.jdbc "0.4.2"]
  				 [mysql/mysql-connector-java "5.1.6"]
  				 [org.clojure/tools.cli "0.3.3"]
  				 [hikari-cp "1.3.1"]
  				 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot json-upsert.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
