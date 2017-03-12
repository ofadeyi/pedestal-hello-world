(ns hello
  (:require [clojure.string :as st]
            [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.content-negotiation :as conneg]))

(def unmentionables #{"YHWH" "Voldemort" "Mxyzptlk" "Rumplestiltskin" "曹操"})

(def supported-types ["text/html" "application/edn" "application/json" "text/plain"])

(def content-neg-intc (conneg/negotiate-content supported-types))

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

(defn accepted-type [context]
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content [body content-type]
  (case content-type
    "text/html"        body
    "text/plain"       body
    "application/edn"  (pr-str body)
    "application/json" (json/write-str body)))

(defn coerce-to [response content-type]
  (-> response
    (update :body transform-content content-type)
    (assoc-in [:headers "Content-Type"] content-type)))

;; Handlers
(defn respond-hello [request]
  (let [qname (get-in request [:query-params :name])
        rresponse (greeting-for qname)]
    (if rresponse
      (ok rresponse)
      (not-found))))

;; Interceptors
(def echo
  {:name ::echo
   :enter #(assoc % :response (ok (:request %)))})

(def coerce-body
  {:name ::coerce-body
   :leave
         (fn [context]
           (cond-> context
             (nil? (get-in context [:response :body :headers "Content-Type"]))
             (update-in [:response] coerce-to (accepted-type context))))})

;; Routes
(def routes
  (route/expand-routes
   #{["/greet" :get [coerce-body content-neg-intc respond-hello] :route-name :greet]
     ["/echo" :get echo]}))

(defn create-server []
  (http/create-server
   {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})) 

(defn start []
 (http/start (create-server)))
