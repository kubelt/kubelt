(ns com.kubelt.ddt.cmds.rpc.oort.config.set
  "RPC oort config set options"
  {:copyright "ⓒ2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [cljs.reader :as r])
  (:require
   [com.kubelt.ddt.auth :as ddt.auth]
   [com.kubelt.ddt.cmds.rpc.call :as rpc.call ]
   [com.kubelt.ddt.options :as ddt.options]
   [com.kubelt.ddt.prompt :as ddt.prompt]
   [com.kubelt.ddt.util :as ddt.util]
   [com.kubelt.lib.json :as lib.json]
   [com.kubelt.lib.promise :as lib.promise]
   [com.kubelt.lib.rpc :as lib.rpc ]
   [com.kubelt.sdk.v1.oort :as sdk.oort]))

(defonce command
  {:command "set <path> [config-value]"
   :desc "Make an RPC call to set config value (by default in json) specifyng a path"
   :requiresArg true
   :builder (fn [^Yargs yargs]
              (ddt.options/options yargs)
              (.option yargs rpc.call/ethers-rpc-name rpc.call/ethers-rpc-config)
              (.option yargs rpc.call/edn-name rpc.call/edn-value))
   :handler (fn [args]
              (aset args "method" ":kb:get:config")
              (let [args (rpc.call/rpc-args args)
                    path (ddt.util/rpc-name->path (get args :path ""))
                    edn? (get args (keyword rpc.call/edn-name))
                    config-value  (let [data (get args :config-value (if edn? "nil"  "null"))]
                                    (if edn?
                                      (r/read-string data)
                                      (lib.json/json-str->edn data)))]
                (ddt.prompt/ask-password!
                 (fn [err result]
                   (ddt.util/exit-if err)
                   (ddt.auth/authenticate
                    args
                    (.-password result)
                    (fn [sys]
                      (-> (sdk.oort/rpc-api sys (-> sys :crypto/wallet :wallet/address))
                          (lib.promise/then
                           (fn [api]
                             (-> (lib.rpc/rpc-call& sys api args)
                                 (lib.promise/then #(-> % :http/body :result))
                                 (lib.promise/then
                                  (fn [response]
                                    (println "CURRENT CONFIG-> " response)
                                    (println "TRYING NEW CONFIG ... " (assoc-in response path config-value))
                                    (let [args (assoc args
                                                      :method  (ddt.util/rpc-name->path ":kb:set:config")
                                                      :params {:config (assoc-in response path config-value)})]
                                      (-> (lib.rpc/rpc-call& sys api args)
                                          (lib.promise/then #(-> % :http/body :result))
                                          (lib.promise/then #(println "SET->" %))
                                          (lib.promise/catch #(println "SET ERROR-> " %))))))
                                 (lib.promise/catch #(println "ERROR-> " %)))))
                          (lib.promise/catch
                           (fn [e]
                             (println (ex-message e))
                             (prn (ex-data e)))))))))))})
