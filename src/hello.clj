(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn ok [body]
  {:status 200 :body body})

(defn greeting-for [query-name]
  (if (empty? query-name)
    "Hello, world!\n"
    (str "Hello, " query-name "!\n")))

(defn respond-hello [request]
  (let [qname (get-in request [:query-params :name])
        rresponse (greeting-for qname)]
     (ok rresponse)))

(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]}))

(defn create-server []
  (http/create-server
   {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})) 

(defn start []
 (http/start (create-server)))
