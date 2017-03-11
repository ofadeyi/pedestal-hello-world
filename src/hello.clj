(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure.string :as st]))

(def unmentionables #{"YHWH" "Voldemort" "Mxyzptlk" "Rumplestiltskin" "曹操"})

(defn ok [body]
  {:status 200 :body body})

(defn bad-request []
  {:status 400 :body "Bad Request\n"})

(defn not-found []
  {:status 404 :body "Not Found\n"})

(defn check-unmentionables [query-name]
  (if (empty? query-name)
    nil
    (seq (filter #(= (st/lower-case query-name) %)
         (map #(st/lower-case %) unmentionables)))))

(defn greeting-for [query-name]
  (cond
    (check-unmentionables query-name) nil
    (empty? query-name)         "Hello, world!\n"
    :else                       (str "Hello, " query-name "!\n")))

(defn respond-hello [request]
  (let [qname (get-in request [:query-params :name])
        rresponse (greeting-for qname)]
    (if rresponse
      (ok rresponse)
      (not-found))))

(def echo
  {
   :name ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]
     ["/echo" :get echo]}))

(defn create-server []
  (http/create-server
   {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})) 

(defn start []
 (http/start (create-server)))
