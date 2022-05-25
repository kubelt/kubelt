(ns com.kubelt.ddt.cmds.rpc.core.config
  "RPC core options"
  {:copyright "ⓒ2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.ddt.cmds.rpc.core.config.get :as core.config.get]
   [com.kubelt.ddt.cmds.rpc.core.config.set :as core.config.set]))

(defonce command
  {:command "config"
   :desc "Work with RPC core config APIs"
   :builder (fn [^js yargs]
              (-> yargs
                  (.command (clj->js core.config.get/command))
                  (.command (clj->js core.config.set/command))
                  (.demandCommand)))})
