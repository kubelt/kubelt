(ns com.kubelt.ddt.ipfs.pin.remote.add
  "Invoke the 'ipfs pin remote add' method."
  {:copyright "©2022 Kubelt, Inc." :license "UNLICENSED"}
  (:require
   [com.kubelt.ipfs.client :as ipfs.client]
   [com.kubelt.ipfs.v0.pin.remote :as v0.pin.remote]))

(defonce command
  {:command "add <ipfs-path>"
   :desc "Pin objects to remote storage"

   :builder (fn [^Yargs yargs]
              yargs)

   :handler (fn [args]
              (println "not yet implemented"))})
