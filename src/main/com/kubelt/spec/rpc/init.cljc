(ns com.kubelt.spec.rpc.init
  "Schemas related to RPC client (init) function."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [com.kubelt.spec.jwt :as spec.jwt]))

;; http-client
;; -----------------------------------------------------------------------------

;; TODO re-use existing HTTP client definition
(def http-client
  :map)

;; user-agent
;; -----------------------------------------------------------------------------
;; Override the default HTTP user agent string. Useful for development
;; and debugging to find the calls that originated with a specific
;; instance of the client.

(def user-agent
  :string)

;; options
;; -----------------------------------------------------------------------------
;; The options map that may be passed to RPC client (init) function.

(def options
  [:map
   [:http/client {:optional true} http-client]
   [:http/user-agent {:optional true} user-agent]
   ;; TODO flip :rpc/jwt to spec.jwt/jwt once we use/expect decrypted jwt value (in BE too)
   [:rpc/jwt {:optional true} :string]])
