(ns com.kubelt.lib.p2p
  "Wrapper around the external p2p naming system."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [clojure.string :as cstr])
  (:require
   [com.kubelt.lib.json :as lib.json]
   [com.kubelt.proto.http :as http]))

;; TODO .cljc

(defn authenticate!
  "Authenticate a user against a core. The account is a map that contains the public
  key from the keypair that represents the user's account."
  [sys core]
  {:pre [(string? core)]}
  (let [client (get sys :client/http)
        scheme (get-in sys [:client/p2p :http/scheme])
        host (get-in sys [:client/p2p :http/host])
        port (get-in sys [:client/p2p :http/port])

        wallet (get sys :crypto/wallet)
        address (get wallet :wallet/address)
        body {:address address}
        body-str (lib.json/edn->json-str body)

        path (cstr/join "" ["/@" core "/auth"])

        request {:com.kubelt/type :kubelt.type/http-request
                 :http/method :post
                 :http/body body-str
                 :uri/scheme scheme
                 :uri/domain host
                 :uri/port port
                 :uri/path path}]
    ;; Make an HTTP request to p2p system, passing along the user's
    ;; wallet address. Expect a nonce in return, which should be signed
    ;; and returned to prove ownership of provided key and complete
    ;; registration.
    ;;
    ;; Returns a promise.
    (http/request! client request)))

(defn verify!
  "Send a signed nonce to verify ownership of a keypair as part of the
  authentication flow."
  [sys core nonce signature]
  {:pre [(every? string? [core nonce])]}
  (let [client (get sys :client/http)
        scheme (get-in sys [:client/p2p :http/scheme])
        host   (get-in sys [:client/p2p :http/host])
        port   (get-in sys [:client/p2p :http/port])

        body     {:nonce nonce :signature signature}
        body-str (lib.json/edn->json-str body)

        path (cstr/join "" ["/@" core "/auth/verify"])

        request {:com.kubelt/type :kubelt.type/http-request
                 :http/method     :post
                 :http/body       body-str
                 :uri/scheme      scheme
                 :uri/domain      host
                 :uri/port        port
                 :uri/path        path}]
    (http/request! client request)))

(defn nfts!
  "Fetch a Core's associated NFTs"
  [sys core jwt]
  {:pre [(every? string? [core jwt])]}
  (let [client (get sys :client/http)
        scheme (get-in sys [:client/p2p :http/scheme])
        host   (get-in sys [:client/p2p :http/host])
        port   (get in sys [:client/p2p :https/port])

        body {}
        body-str (lib.json/edn->json-str body)

        path (cstr/join "" ["/@" core "/nft"])

        ;; Make an HTTP request to p2p system, passing along the user's
        ;; wallet address. Expect a list of NFTs in return

        ;; Returns a promise.
        request {:com.kubelt/type :kubelt.type/https-request
                 :http/method   :get
                 :http/body     body-str
                 :uri/scheme    scheme
                 :uri/domain    host
                 :uri/port      port
                 :uri/path      path}]
    (http/request! client request)))
