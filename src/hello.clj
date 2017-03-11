(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn ok [body]
  {:status 200 :body body})

(defn bad-request []
  {:status 400})

(defn greeting-for [has-query-name query-name]
  (cond
    (= has-query-name false) "Hello, world!\n"
    (= (.length query-name) 0) nil
    :else (str "Hello, " query-name "!\n")))

(defn respond-hello [request]
  (let [has-qname (contains? (:query-params request) :name)
        qname (get-in request [:query-params :name])
        rresponse (greeting-for has-qname qname)]
    (if rresponse
      (ok rresponse)
      (bad-request))))

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
