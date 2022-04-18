(ns com.kubelt.ddt.cmds.sdk
  "CLI setup for 'sdk' sub-command."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.ddt.cmds.sdk.core :as sdk.core]
   [com.kubelt.ddt.cmds.sdk.init :as sdk.init]
   [com.kubelt.ddt.cmds.sdk.options :as sdk.options]
   [com.kubelt.ddt.cmds.sdk.resource :as sdk.resource]
   [com.kubelt.ddt.cmds.sdk.workspace :as sdk.workspace]))

(defonce command
  {:command "sdk <command>"
   :desc "Invoke SDK methods"
   :builder (fn [^js yargs]
              (-> yargs
                  (.command (clj->js sdk.init/command))
                  (.command (clj->js sdk.options/command))
                  (.command (clj->js sdk.core/command))
                  (.command (clj->js sdk.resource/command))
                  (.command (clj->js sdk.workspace/command))
                  (.demandCommand)))})
