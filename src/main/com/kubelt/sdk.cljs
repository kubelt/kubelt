(ns com.kubelt.sdk
  "Entry point for the Kubelt SDK."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.sdk.v1 :as sdk.v1]
   [com.kubelt.lib.rpc :as lib.rpc]
   [com.kubelt.sdk.v1.oort :as sdk.v1.oort]))

;; Entrypoint
;; -----------------------------------------------------------------------------
;; This is the entry point for the SDK.

;; The SDK is intended to work in multiple contexts, including:
;; - ClojureScript applications
;; - Node.js applications
;; - Web applications
;;
;; Each of these values defined below are exposed by the generated
;; library to enable access to the corresponding version of the
;; API. The (init) function accepts configuration options and
;;
;; To use the SDK from ClojureScript:
;;
;;   (:require [com.kubelt.sdk.v1] :as sdk)
;;   (def kbt (sdk/init {...})
;;   (kbt/do-something)
;;   (sdk/halt! kbt)
;;
;; To use the SDK from a web application:
;;
;;   TODO
;;
;; To use the library from Node.js:
;;
;;   const kbt = require("kubelt");
;;   const sdk = kubelt.v1.init({...});
;;   kbt.v1.workspace.available(sdk);
;;   kbt.v1.halt(sdk);
;;

(defn web-v1
  []
  ;; TODO
  (println "web-v1"))

(def node-v1
  #js {:init sdk.v1/init-js
       :halt sdk.v1/halt-js!
       :options sdk.v1/options-js

       ;; storage
       :store sdk.v1/store-js&
       :restore sdk.v1/restore-js&

       ;; oort
       :oort #js {:authenticate sdk.v1.oort/authenticate-js!
                  :claims sdk.v1.oort/claims-js
                  :callRpc sdk.v1.oort/call-rpc-js
                  :callRpcWithApi lib.rpc/call-rpc-with-api-js
                  :rpcApi sdk.v1.oort/rpc-api-js
                  :isLoggedIn sdk.v1.oort/logged-in-js?
                  :setWallet sdk.v1.oort/set-wallet-js}

       ;; development (removed from production builds)
       ;; TODO
       ;;:develop #js {:rpc #js {:call sdk.v1.develop.rpc/call-js!}}
       })
