(ns com.kubelt.ddt.cmds.sdk.core
  "CLI setup for SDK > core sub-command."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.ddt.cmds.sdk.core.authenticate :as core.authenticate]
   [com.kubelt.ddt.cmds.sdk.core.logged-in :as core.logged-in]))

(defonce command
  {:command "core <command>"
   :desc "Work with Kubelt accounts"
   :builder (fn [^js yargs]
              (-> yargs
                  (.command (clj->js core.authenticate/command))
                  (.command (clj->js core.logged-in/command))
                  (.demandCommand)))})
