(ns com.kubelt.spec.rpc.client
  "An RPC client map."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:refer-clojure :exclude [methods])
  (:require
   [com.kubelt.spec.rpc.init :as spec.rpc.init]))

;; http-client
;; -----------------------------------------------------------------------------
;; TODO this should be something that conforms to com.kubelt.proto.http/HttpClient

(def http-client
  :any)

;; prefix
;; -----------------------------------------------------------------------------
;; Every schema used by the client has an associated prefix that is used
;; to namespace it from other schemas in the same client.

(def prefix
  :keyword)

;; schemas
;; -----------------------------------------------------------------------------

;; TODO flesh these specs out to describe the RPC client map that
;; results from the (rpc/init) call.
(def version :string)
(def metadata :map)
(def servers [:vector :any])
(def methods :map)

(def schema
  [:map
   [:rpc/version version]
   [:rpc/metadata metadata]
   [:rpc/servers servers]
   [:rpc/methods methods]])

(def schemas
  [:map-of prefix schema])

;; client
;; -----------------------------------------------------------------------------
;; An RPC client.

(def client
  [:map
   [:com.kubelt/type [:enum :kubelt.type/rpc.client]]
   [:init/options spec.rpc.init/options]
   [:http/client http-client]
   [:rpc/schemas schemas]])
