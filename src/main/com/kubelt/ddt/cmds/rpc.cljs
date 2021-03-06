(ns com.kubelt.ddt.cmds.rpc
  "CLI setup for 'rpc' sub-command."
  {:author "Proof Zero Inc."}
  (:require
   [com.kubelt.ddt.cmds.rpc.call :as rpc.call]
   [com.kubelt.ddt.cmds.rpc.ls :as rpc.ls]
   [com.kubelt.ddt.cmds.rpc.oort :as rpc.oort]))

(defonce command
  {:command "rpc <command>"
   :desc "Work with RPC APIs"
   :builder (fn [^js yargs]
              (-> yargs
                  (.command (clj->js rpc.call/command))
                  (.command (clj->js rpc.ls/command))
                  (.command (clj->js rpc.oort/command))
                  (.demandCommand)))})
