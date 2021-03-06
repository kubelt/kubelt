(ns com.kubelt.ddt.cmds.ipfs.key.generate
  "Invoke the 'ipfs key generate' method."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.ipfs.client :as ipfs.client]
   [com.kubelt.ipfs.v0.key :as v0.key]))

(defonce command
  {:command "generate <name>"
   :desc "Create a new keypair"

   :builder (fn [^Yargs yargs]
              yargs)

   :handler (fn [args]
              (println "not yet implemented"))})
