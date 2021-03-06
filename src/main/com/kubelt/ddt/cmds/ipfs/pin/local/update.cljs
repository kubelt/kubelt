(ns com.kubelt.ddt.cmds.ipfs.pin.local.update
  "Invoke the 'ipfs pin local update' method."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.ipfs.client :as ipfs.client]
   [com.kubelt.ipfs.v0.pin :as v0.pin]))

(defonce command
  {:command "update <from-path> <to-path>"
   :desc "Update a recursive pin"

   :builder (fn [^Yargs yargs]
              yargs)

   :handler (fn [args]
              (println "not yet implemented"))})
