(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(def unmentionables #{"YHWH" "Voldemort" "Mxyzptlk" "Rumplestiltskin" "曹操"})

(defn ok [body]
  {:status 200 :body body})

(defn bad-request []
  {:status 400 :body "Bad Request\n"})

(defn not-found []
  {:status 404 :body "Not Found\n"})

(defn greeting-for [ query-name]
  (cond
    (unmentionables query-name) nil
    (empty? query-name)         "Hello, world!\n"
    :else                       (str "Hello, " query-name "!\n")))

(defn respond-hello [request]
  (let [qname (get-in request [:query-params :name])
        rresponse (greeting-for qname)]
    (if rresponse
      (ok rresponse)
      (not-found))))

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
