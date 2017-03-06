(ns hell
  (:require [io.pedastal.http :as http]
            [io.pedastal.http.route :as route]))


(defn respond-hello [request]
  {:status 200 :body "Hello World!"})
