(ns com.kubelt.ddt.cmds.wallet.ls
  "Invoke the wallet (ls) method."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [clojure.string :as cstr])
  (:require
   [com.kubelt.ddt.color :as ddt.color]
   [com.kubelt.ddt.options :as ddt.options]
   [com.kubelt.ddt.util :as ddt.util]
   [com.kubelt.lib.promise :as lib.promise]
   [com.kubelt.lib.wallet :as lib.wallet]))

(defonce command
  {:command "ls"
   :desc "List wallets"
   :requiresArg false

   :builder (fn [^Yargs yargs]
              yargs)

   :handler (fn [args]
              (let [args-map (ddt.options/to-map args)
                    app-name (get args-map :app-name)]
                (-> (lib.wallet/ls& app-name)
                    (lib.promise/then
                     (fn [wallet-names]
                       (doseq [wallet-name wallet-names]
                         (let [arrow (ddt.color/hilite "->")
                               wallet-line (cstr/join " " [arrow wallet-name])]
                           (println wallet-line)))))
                    (lib.promise/catch
                     (fn [e] (ddt.util/exit-if e))))))})
