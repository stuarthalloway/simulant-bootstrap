(init)
(start)

;; see https://github.com/mmcgrana/ring/blob/master/SPEC
(site/the-site {})
{:status 404,
 :headers {"Content-Type" "application/edn"},
 :body ":not-found"}

(site/the-site {:uri "/"
                :scheme :http
                :request-method :get})
{:status 200,
 :headers {"Content-Type" "application/edn"},
 :body ":ok"}

(site/the-site {:uri "/liveids"
                :scheme :http
                :request-method :get})
{:status 200,
 :headers {"Content-Type" "application/edn"},
 :body "()"}

(site/the-site {:uri "/data"
                :scheme :http
                :content-type "application/edn"
                :body "{:eggs 3}"
                :request-method :put})

(site/the-site {:uri "/data"
                :scheme :http
                :query-string "id=0"
                :request-method :get})
{:status 200,
 :headers {"Content-Type" "application/edn"},
 :body "{:eggs 3}"}

(site/the-site {:uri "/liveids"
                :scheme :http
                :request-method :get})
{:status 200,
 :headers {"Content-Type" "application/edn"},
 :body "(0)"}
