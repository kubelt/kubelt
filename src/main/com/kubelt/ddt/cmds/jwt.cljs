(ns com.kubelt.ddt.cmds.jwt
  "CLI setup for 'jwt' sub-command."
  {:copyright "©2022 Kubelt, Inc." :license "UNLICENSED"}
  (:require
   [com.kubelt.ddt.cmds.jwt.sign :as jwt.sign]))

(defonce command
  {:command "jwt <command>"
   :desc "Work with JWTs"
   :builder (fn [^js yargs]
              (-> yargs
                  (.command (clj->js jwt.sign/command))
                  (.demandCommand)))})
