(ns com.kubelt.lib.jwt.util
  ""
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"})

;;;;;;;;;; helpers ;;;;;;;;;;;;;;;;;;;

;; #?(:node
;;    (defn encode [target]
;;      "Encode a string with base64 URL Safe"
;;      (.encodeString base64 target goog.crypt.base64.BASE_64_URL_SAFE)))

;; #?(:node
;;    (defn decode [target]
;;      "Decode a string with base64 URL Safe"
;;      (.decodeString base64 target)))

;; (defn create-header [alg exp]
;;   "Create a JWT header specifying the algorithm used and expiry time"
;;   {:alg alg :exp exp})

;; #?(:node
;;    (defn get-public-key [token]
;;      "Extract public key from the JWT payload"
;;      (let [payload-part (decode (get (cstr/split token #"\.") 1))
;;            payload-json (cstr/replace payload-part "\"" "")
;;            payload-map (js->clj (json/parse (decode (js->clj payload-json))) :keywordize-keys true)]
;;        (decode (get payload-map :pubkey)))))

;; #?(:node
;;    (defn prepare-key [key-material]
;;      "import raw private key string"
;;      ;; return key object
;;      key-material))

;; #?(:node
;;    (defn prepare-payload [claims]
;;      "import raw private key string"
;;      (encode (json/serialize (clj->js claims)))))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; #?(:node
;;    (defn- sign-jwt [alg secret-key header-enc payload-enc]
;;      "Sign encoded payload and header using secret key, return digest"
;;      ;; sign payload+header
;;      (let [signature-target (cstr/join "" [header-enc payload-enc])
;;            signature-digest (-> (.createSign crypto "RSA-SHA256")
;;                                 (.update (cstr/join "." [header-enc payload-enc]))
;;                                 (.sign secret-key "base64"))]
;;        signature-digest)))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; #?(:node
;;    (defn create-jwt [secret-key header payload]
;;      "Create a JWT token from key, header and payload"
;;      (let [alg (get header :alg)
;;            header-enc (encode (json/serialize (clj->js header)))
;;            payload-enc (encode (json/serialize (clj->js payload)))
;;            signature-enc  (encode (sign-jwt alg secret-key header-enc payload-enc))]
;;        (cstr/join "." [header-enc payload-enc signature-enc]))))

;; ;; TODO this can probably extract the pubkey internally
;; #?(:node
;;    (defn validate-jwt [token]
;;      "Validate a token against a given public key"
;;      (let [;; extract public key
;;            pubkey (get-public-key token)
;;            token-pieces (cstr/split token #"\.")
;;            psig (get token-pieces 2)
;;            verified (-> (.createVerify crypto "RSA-SHA256")
;;                         (.update (cstr/join "." [(get token-pieces 0) (get token-pieces 1)]))
;;                         ;;(.verify pubkey, psig, "base64"))]
;;                         (.verify pubkey (decode psig) "base64"))]
;;        verified)))
