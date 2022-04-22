(ns com.kubelt.ddt.cmds.ipfs
  "CLI setup for 'ipfs' sub-command."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.ddt.cmds.ipfs.dag :as ipfs.dag]
   [com.kubelt.ddt.cmds.ipfs.key :as ipfs.key]
   [com.kubelt.ddt.cmds.ipfs.name :as ipfs.name]
   [com.kubelt.ddt.cmds.ipfs.node :as ipfs.node]
   [com.kubelt.ddt.cmds.ipfs.pin :as ipfs.pin]))

(defonce command
  {:command "ipfs <command>"
   :desc "Interact with IPFS"
   :builder (fn [^js yargs]
              (-> yargs
                  (.command (clj->js ipfs.dag/command))
                  (.command (clj->js ipfs.key/command))
                  (.command (clj->js ipfs.name/command))
                  (.command (clj->js ipfs.node/command))
                  (.command (clj->js ipfs.pin/command))
                  (.demandCommand)))})
