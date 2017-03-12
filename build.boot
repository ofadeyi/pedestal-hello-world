;; IntelliJ "integration"
(require '[boot.core :refer :all]
         '[boot.task.built-in :refer :all])

(task-options!
 pom {:project     'hello-world
      :version     "0.0.1"
      :description "Pedestal - Hello World Tutorial"})

(set-env!
 :resource-paths #{"src"}
 :dependencies   '[[org.clojure/clojure "1.8.0" :scope "provided"]
                   [boot/core "2.7.1" :scope "provided"]
                   [onetom/boot-lein-generate "0.1.3" :scope "test"]


                   [io.pedestal/pedestal.service "0.5.1"]
                   [io.pedestal/pedestal.route   "0.5.1"]
                   [io.pedestal/pedestal.jetty   "0.5.1"]
                   [org.clojure/data.json        "0.2.6"]
                   [org.slf4j/slf4j-simple       "1.7.21"]])

;; Used to create a project.clj
(require '[boot.lein])
(boot.lein/generate)
