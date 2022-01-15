(ns com.kubelt.cli.path.data
  "Invoke the path > data method to return the system configuration path."
  {:copyright "©2022 Kubelt, Inc." :license "UNLICENSED"}
  (:require
   [com.kubelt.sdk.impl.path :as path]))

(defonce command
  {:command "data"
   :desc "Print the data path"
   :requiresArg false

   :builder (fn [^Yargs yargs]
              yargs)

   :handler (fn [args]
              (let [{app-name :$0}
                    (js->clj args :keywordize-keys true)]
                (println (path/data app-name))))})
