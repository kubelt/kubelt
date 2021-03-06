(ns com.kubelt.lib.config.default
  "Default configuration values."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [integrant.core :as ig])
  (:require
   [com.kubelt.lib.wallet :as lib.wallet]))

;; Default logging level.
(def log-level :warn)

;; Default configuration
;; -----------------------------------------------------------------------------

(def app
  {:app/name ""})

(def log
  {:log/level log-level})

;; By default we look for a local IPFS node.
(def ipfs
  {:ipfs.read/scheme :http
   :ipfs.read/host "127.0.0.1"
   :ipfs.read/port 5001
   :ipfs.write/scheme :http
   :ipfs.write/host "127.0.0.1"
   :ipfs.write/port 5001})

(def oort
  {:oort/scheme :http
   :oort/host "127.0.0.1"
   :oort/port 8787})

(def logging
  {:log/level log-level})

(def credentials
  {:credential/jwt {}})

;; sdk
;; -----------------------------------------------------------------------------
;; These are the defaults for the option map passed to the SDK init function.

(def sdk
  (merge app
         ipfs
         log
         oort
         credentials
         logging))

;; system
;; -----------------------------------------------------------------------------
;; These defaults should be overridden from the SDK init options map.

(def system
  (merge sdk
         {;; An application name, usually reverse-TLD namespaced,
          ;; e.g. com.example.foo-app.
          :app/name ""
          ;; Our common HTTP client.
          :client/http {}
          ;; Our connection to IPFS.
          :client/ipfs {:ipfs/read {:http/scheme (ig/ref :ipfs.read/scheme)
                                    :http/host (ig/ref :ipfs.read/host)
                                    :http/port (ig/ref :ipfs.read/port)}
                        :ipfs/write {:http/scheme (ig/ref :ipfs.write/scheme)
                                     :http/host (ig/ref :ipfs.write/host)
                                     :http/port (ig/ref :ipfs.write/port)}
                        :client/http (ig/ref :client/http)}
          ;; Our connection to the Kubelt Oort backend.
          :client/oort {:http/scheme (ig/ref :oort/scheme)
                        :http/host (ig/ref :oort/host)
                        :http/port (ig/ref :oort/port)}
          ;; A wrapper around platform configuration storage.
          :config/storage {:app/name (ig/ref :app/name)}
          ;; A crypto "vault" that stores session credentials.
          :crypto/session {:jwt/tokens (ig/ref :credential/jwt)}
          ;; A wrapper around the platform wallet functionality.
          :crypto/wallet {}}))
