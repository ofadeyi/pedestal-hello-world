(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))


(defn respond-hello [request]
  (let [qname (get-in request [:query-params :name])
        rresponse (if (empty? qname)
                    "Hello, world!\n"
                    (str "Hello, " qname "!\n"))]
    {:status 200
     :body rresponse}))


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
