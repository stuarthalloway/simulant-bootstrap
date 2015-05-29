(ns db
  (:require [datomic.api :as d]))

;; Holder of the datomic connection
;; ... used by runner and api-user-sim

(defn reset-conn
  "Reset connection to a scratch database. Use memory database if no
   URL passed in."
  ([]
     (reset-conn (str "datomic:mem://" (d/squuid))))
  ([uri]
     (d/delete-database uri)
     (d/create-database uri)
     (d/connect uri)))

(def sim-uri (str "datomic:mem://" (d/squuid)))
(def sim-conn (reset-conn sim-uri))

;; disgusting demo hack
(defn set-test-uri
  [uri]
  (d/create-database uri)
  (def sim-uri uri)
  (def sim-conn (d/connect uri)))


