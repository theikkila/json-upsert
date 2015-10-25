(ns json-upsert.core
  (:require [clojure.data.json :as json]
            [hikari-cp.core :refer :all]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))


(def upsert-query
  "INSERT INTO test (a, b, score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE score = values(score);")

(def cli-options
  ;; An option with a required argument
  [["-p" "--port PORT" "Database port number"
    :default 3306
    :id :port-number
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-i" "--input INPUT.json" "Input filename"
    :default "import.json"
    :id :input-file]
   ["-q" "--query QUERY" "SQL query to be executed for every JSON array in params. Substitute variables with ?"
    :default upsert-query
    :id :query]
   ["-u" "--user USER" "Database username"
    :default "root"
    :id :username]
   ["-w" "--pass PASSWORD" "Database password"
    :default "password"
    :id :password]
   ["-d" "--database NAME" "Database name"
    :default "test"
    :id :database-name]
   ["-t" "--host HOSTNAME" "Database hostname"
    :default "localhost"
    :id :server-name]
   ["-h" "--help"]])


(defn usage [options-summary]
  (->> ["This is JSON-importer."
        ""
        "Usage: json-upsert [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  import    Import JSON file"
        ""
        "Please refer to the manual page for more information."]
       (clojure.string/join \newline)))


(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(def datasource-options {:auto-commit        true
                         :maximum-pool-size  17
                         :adapter            "mysql"})


(defn read-scores [file]
  (json/read-str (slurp file) :key-fn keyword))


(defn upsert-row [options values]
  (let [query (:query options)
        datasource (:datasource options)]
  (jdbc/with-db-connection [conn {:datasource datasource}]
    (jdbc/execute! conn (apply conj [query] values) :multi? false :transaction? false))))



(defn import-json [options]
  (println "Starting importing" (:input-file options))
  (let [filename (:input-file options)
        query (:query options)
        db-options (-> options
                    (dissoc :input-file)
                    (dissoc :query))
        datasource (make-datasource (merge datasource-options db-options))
        scores (read-scores filename)]
    (println "Running" (count scores) "queries")
    (doall (pmap (partial upsert-row {:datasource datasource :query query}) scores))
    (shutdown-agents)
    (close-datasource datasource)))



(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    (case (first arguments)
      "import" (import-json options)
      (exit 1 (usage summary)))))
