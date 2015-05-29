(require :reload 'runner)
(in-ns 'runner)

(def uri "datomic:free://localhost:4334/simulation-example")
(db/set-test-uri uri)

(load-schema db/sim-conn "simulant/schema.edn")
(load-schema db/sim-conn "site-sim.edn")

;; generate a model
@(d/transact
  db/sim-conn
  [{:db/id model-id
    :db/ident :example-model
    :model/type :model.type/siteUsage
    :model/userCount 100
    :model/meanPayloadSize 2
    :model/meanSecsBetweenHits 10}])

(def site-user-model
  (d/entity (d/db db/sim-conn) :example-model))

;; generate some test data
(def site-usage-test
  (sim/create-test
   db/sim-conn
   site-user-model
   {:db/id (d/tempid :test)
    :test/duration (* 10 10000)}))

;; horizontally scale the test
(def site-usage-sim
  (sim/create-sim
   db/sim-conn
   site-usage-test
   {:db/id (d/tempid :sim)
    :sim/processCount 10}))

;; action log for this sim
(def action-log
  (sim/create-action-log db/sim-conn site-usage-sim))

;; speed up time!
(def sim-clock
  (sim/create-fixed-clock
   db/sim-conn
   site-usage-sim
   {:clock/multiplier 960}))

;; run
(def pruns
  (->> #(sim/run-sim-process uri (:db/id site-usage-sim))
       (repeatedly (:sim/processCount site-usage-sim))
       (into [])))

;; take a short break
(time
   (mapv (fn [prun] @(:runner prun)) pruns))


(def simdb (d/db db/sim-conn))

(def site-ids
  (set (d/q '[:find [?id ...]
              :in $ ?test
              :where
              [?test :test/agents ?agent]
              [?agent :agent/siteIds ?id]]
            simdb (:db/id site-usage-test))))

(def live-ids
  (-> (client/get "http://localhost:3000/liveids")
      :body
      read-string
      set))

(= site-ids live-ids)

(defn get-action-site-ids [action-type]
  (set (d/q '[:find [?id ...]
              :in $ ?action-type ?test
              :where
              [?test :test/agents ?agent]
              [?agent :agent/actions ?action]
              [?action :action/type ?action-type]
              [?action :action/siteId ?id]]
            simdb action-type (:db/id site-usage-test))))

(def idmap
  {:put (get-action-site-ids :action.type/put)
   :get (get-action-site-ids :action.type/get)
   :rm  (get-action-site-ids :action.type/delete)})

;; did deleted things go away
(= (difference (:put idmap) (:rm idmap))
   live-ids)











