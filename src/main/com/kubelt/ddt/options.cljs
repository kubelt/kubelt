(ns com.kubelt.ddt.options
  "Common options various sub-commands."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   ["path" :as path])
  (:require
   [clojure.set :as cset]
   [clojure.string :as cstr]))

;; Defaults
;; -----------------------------------------------------------------------------

(def scheme-default
  "http")

(def scheme-choices
  #js ["http" "https"])

(def host-default
  "127.0.0.1")

(def oort-port-default
  8787)

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
       :describe "oort service host"
       :requiresArg true
       :demandOption "service host is required"
       :string true
       :nargs 1
       :default host-default})

(def port-name
  "port")

(def port-config
  #js {:alias "p"
       :describe "oort service port"
       :requiresArg true
       :demandOption "service port is required"
       :number true
       :nargs 1
       :default oort-port-default})

(def wallet-name
  "wallet")

(def wallet-config
  #js {:alias "w"
       :describe "a wallet name"
       :requiresArg true
       :demandOption "wallet must be specified"
       :string true
       :nargs 1})

(def ipfs-read-scheme
  "ipfs-read-scheme")

(def ipfs-read-scheme-config
  #js {:describe "use http or https to talk to IPFS read instance"
       :requiresArg true
       :demandOption "scheme is required"
       :choices scheme-choices
       :nargs 1
       :default scheme-default})

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

(def ipfs-write-scheme
  "ipfs-write-scheme")

(def ipfs-write-scheme-config
  #js {:describe "use http or https to talk to IPFS write instance"
       :requiresArg true
       :demandOption "scheme is required"
       :choices scheme-choices
       :nargs 1
       :default scheme-default})

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
  ;; Add --ipfs-read-scheme
  (.option yargs ipfs-read-scheme ipfs-read-scheme-config)
  ;; Add --ipfs-read-host option
  (.option yargs ipfs-read-host ipfs-read-host-config)
  ;; Add --ipfs-read-port option
  (.option yargs ipfs-read-port ipfs-read-port-config)
  ;; Add --ipfs-write-scheme
  (.option yargs ipfs-write-scheme ipfs-write-scheme-config)
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
  (let [;; The parsed arguments are returned as a #js object. Convert to
        ;; a CLJS map with keywords as keys.
        m (js->clj args :keywordize-keys true)
        ;; The string array of arguments is provided with the
        ;; key "_". Rename it to something a bit friendlier.
        m (cset/rename-keys m {:_ :args})
        ;; Get the name of the application that was invoked; store in
        ;; the arguments map as something that is namespace qualified,
        ;; i.e. com.kubelt.$name.
        base-name (.basename path (get m :$0))
        app-name (cstr/join "." ["com" "kubelt" base-name])
        ;; Store the oort service coordinates.
        tls (get m :tls)
        oort-scheme (if tls :https :http)
        oort-host (get m :host)
        oort-port (get m :port)
        ;; Store coordinates of IPFS read host.
        ipfs-read-scheme (keyword (get m :ipfs-read-scheme))
        ipfs-read-host (get m :ipfs-read-host)
        ipfs-read-port (get m :ipfs-read-port)
        ;; Store coordinates of IPFS write host.
        ipfs-write-scheme (keyword (get m :ipfs-write-scheme))
        ipfs-write-host (get m :ipfs-write-host)
        ipfs-write-port (get m :ipfs-write-port)
        ;; Turn a sequence of core names and JWT strings into a map:
        ;; ["aaa" "bbb" "ccc" "ddd"]
        ;; => {"aaa" "bbb", "ccc" "ddd"}
        jwts (get m :jwt)
        credentials (apply hash-map jwts)]
    (-> m
        (assoc :app-name app-name)
        (assoc :oort-scheme oort-scheme)
        (assoc :oort-host oort-host)
        (assoc :oort-port oort-port)
        (assoc :ipfs-read-scheme ipfs-read-scheme)
        (assoc :ipfs-read-host ipfs-read-host)
        (assoc :ipfs-read-port ipfs-read-port)
        (assoc :ipfs-write-scheme ipfs-write-scheme)
        (assoc :ipfs-write-host ipfs-write-host)
        (assoc :ipfs-write-port ipfs-write-port)
        (assoc :credentials credentials)
        (update :log-level keyword))))

(defn init-options
  "Transform an argument map into a map of options that can be used to
  initialize the SDK. The argument map should contain arguments provided
  on the command line, and have been transformed from a JavaScript
  object to a Clojure map using (to-map)."
  [m]
  {:pre [(map? m)]}
  {:credential/jwt (get m :credentials)
   :log/level (get m :log-level)
   :app/name (get m :app-name)
   :ipfs.read/scheme (get m :ipfs-read-scheme)
   :ipfs.read/host (get m :ipfs-read-host)
   :ipfs.read/port (get m :ipfs-read-port)
   :ipfs.write/scheme (get m :ipfs-write-scheme)
   :ipfs.write/host (get m :ipfs-write-host)
   :ipfs.write/port (get m :ipfs-write-port)
   :oort/scheme (get m :oort-scheme)
   :oort/host (get m :oort-host)
   :oort/port (get m :oort-port)})
