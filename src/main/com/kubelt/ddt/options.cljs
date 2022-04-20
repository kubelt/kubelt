(ns com.kubelt.ddt.options
  "Common options various sub-commands."
  {:copyright "©2022 Kubelt, Inc." :license "Apache 2.0"}
  (:require
   ["path" :as path])
  (:require
   [clojure.string :as cstr]))

;; Defaults
;; -----------------------------------------------------------------------------

(def host-default
  "127.0.0.1")

(def port-default
  9061)

(def ipfs-port-default
  5001)

;; Options
;; -----------------------------------------------------------------------------

(def log-level
  "log-level")

(def log-config
  #js {:describe "set the logging level"
       :default "warn"
       :choices #js ["log"
                     "trace"
                     "debug"
                     "info"
                     "warn"
                     "error"
                     "fatal"
                     "report"
                     "spy"]})

(def tls-name
  "tls")

(def tls-config
  #js {:describe "make request with(out) TLS"
       :boolean true})

(def host-name
  "host")

(def host-config
  #js {:alias "h"
       :describe "p2p service host"
       :requiresArg true
       :demandOption "service host is required"
       :string true
       :nargs 1
       :default host-default})

(def port-name
  "port")

(def port-config
  #js {:alias "p"
       :describe "p2p service port"
       :requiresArg true
       :demandOption "service port is required"
       :number true
       :nargs 1
       :default port-default})

(def wallet-name
  "wallet")

(def wallet-config
  #js {:alias "w"
       :describe "a wallet name"
       :requiresArg true
       :demandOption "wallet must be specified"
       :string true
       :nargs 1})

(def ipfs-read-host
  "ipfs-read-host")

(def ipfs-read-host-config
  #js {:describe "host address of IPFS read instance"
       :requiresArg true
       :demandOption "address is required"
       :string true
       :nargs 1
       :default host-default})

(def ipfs-read-port
  "ipfs-read-port")

(def ipfs-read-port-config
  #js {:describe "port of IPFS read instance"
       :requiresArg true
       :demandOption "port is required"
       :number true
       :nargs 1
       :default ipfs-port-default})

(def ipfs-write-host
  "ipfs-write-host")

(def ipfs-write-host-config
  #js {:describe "address of IPFS write instance"
       :requiresArg true
       :demandOption "address is required"
       :string true
       :nargs 1
       :default host-default})

(def ipfs-write-port
  "ipfs-write-port")

(def ipfs-write-port-config
  #js {:describe "port of IPFS write instance"
       :requiresArg true
       :demandOption "port is required"
       :number true
       :nargs 1
       :default ipfs-port-default})

(def jwt-name
  "jwt")

(def jwt-config
  #js {:describe "A core name and associated JWT"
       :requiresArg true
       :array true
       :nargs 2})

;; All of these options are required. NB: the options with supplied
;; defaults won't cause an error if not supplied by user.
(def required-options
  #js [host-name
       port-name
       wallet-name])

;; Public
;; -----------------------------------------------------------------------------

(defn options
  [yargs]
  ;; Add --log-level option
  (.option yargs log-level log-config)
  ;; Add --(no-)tls option
  (.option yargs tls-name tls-config)
  ;; Add --host option
  (.option yargs host-name host-config)
  ;; Add --port option
  (.option yargs port-name port-config)
  ;; Add --ipfs-read-host option
  (.option yargs ipfs-read-host ipfs-read-host-config)
  ;; Add --ipfs-read-port option
  (.option yargs ipfs-read-port ipfs-read-port-config)
  ;; Add --ipfs-write-host option
  (.option yargs ipfs-write-host ipfs-write-host-config)
  ;; Add --ipfs-write-port option
  (.option yargs ipfs-write-port ipfs-write-port-config)
  ;; Add --wallet option
  (.option yargs wallet-name wallet-config)
  ;; Add --jwt option
  (.option yargs jwt-name jwt-config)
  ;; Indicate which options must be provided
  (.demandOption yargs required-options)
  ;; Pretend like this is functional
  yargs)


(defn to-map
  "Convert arguments object to a Clojure map, ensuring that common options
  are transformed appropriately."
  [args]
  (let [m (js->clj args :keywordize-keys true)
        ;; Get the name of the application that was invoked; store in
        ;; the arguments map as something that is namespace qualified,
        ;; i.e. com.kubelt.$name.
        base-name (.basename path (get m :$0))
        app-name (cstr/join "." ["com" "kubelt" base-name])
        ;; Store the p2p service coordinates as a multiaddress.
        host (get m :host)
        port (get m :port)
        tls (get m :tls)
        p2p-maddr (cstr/join "/" ["" "ip4" host "tcp" port])
        p2p-scheme (if tls :https :http)
        ;; Store coordinates of IPFS read host as a multiaddress.
        ipfs-read-host (get m :ipfs-read-host)
        ipfs-read-port (get m :ipfs-read-port)
        ipfs-read (cstr/join "/" ["" "ip4" ipfs-read-host "tcp" ipfs-read-port])
        ;; Store coordinates of IPFS write host as a multiaddress.
        ipfs-write-host (get m :ipfs-write-host)
        ipfs-write-port (get m :ipfs-write-port)
        ipfs-write (cstr/join "/" ["" "ip4" ipfs-write-host "tcp" ipfs-write-port])
        ;; Turn a sequence of core names and JWT strings into a map:
        ;; ["aaa" "bbb" "ccc" "ddd"]
        ;; => {"aaa" "bbb", "ccc" "ddd"}
        jwts (get m :jwt)
        credentials (apply hash-map jwts)]
    (-> m
        (assoc :app-name app-name)
        (assoc :p2p-maddr p2p-maddr)
        (assoc :p2p-scheme p2p-scheme)
        (assoc :ipfs-read ipfs-read)
        (assoc :ipfs-write ipfs-write)
        (assoc :credentials credentials)
        (update :log-level keyword))))

(defn init-options
  [m]
  {:pre [(map? m)]}
  (let []
    {:credential/jwt (get m :credentials)
     :log/level (get m :log-level)
     :ipfs/read (get m :ipfs-read)
     :ipfs/write (get m :ipfs-write)
     :p2p/read (get m :p2p-maddr)
     :p2p.read/scheme (get m :p2p-scheme)
     :p2p/write (get m :p2p-maddr)
     :p2p.write/scheme (get m :p2p-scheme)}))
