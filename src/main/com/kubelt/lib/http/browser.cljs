(ns com.kubelt.lib.http.browser
  "Support for HTTP requests from a browser execution context."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [clojure.string :as cstr])
  (:require
   [goog.net.XhrIo :as xhrio]
   [goog.url :as gurl])
  (:require
   [malli.core :as malli])
  (:require
   [com.kubelt.lib.error :as lib.error]
   [com.kubelt.lib.http.shared :as http.shared]
   [com.kubelt.lib.json :as lib.json]
   [com.kubelt.lib.promise :refer [promise]]
   [com.kubelt.proto.http :as proto.http]
   [com.kubelt.spec.http :as spec.http]))

;; Internal
;; -----------------------------------------------------------------------------

;; Request map example
;; {:kubelt/type :kubelt.type/uri
;;  :http/method method
;;  :http/version version
;;  :http/headers headers
;;  :http/trailers trailers
;;  :http/status status
;;  :http/body body
;;  :uri/scheme scheme
;;  :uri/port port
;;  :uri/path path
;;  :uri/fragment fragment
;;  :uri/query query
;;  :uri/domain domain
;;  :uri/user user}

(defn- validate-content-type
  "Only lower-case when content-type exists. Calling `cstr/lower-case`
  on `nil` throws an error."
  [content-type]
  (when (and content-type
             (not (cstr/blank? content-type)))
    (cstr/lower-case content-type)))

(defn- make-response-fn
  "Returns a response handler function for an HTTP request send using
  XhrIo."
  [resolve]
  (fn [^js event]
    (let [res (-> event .-target .getResponseText)
          status (-> event .-target .getStatus)
          content-type (-> event
                           .-target
                           (.getResponseHeader "content-type")
                           validate-content-type)
          response {:http/status status :http/headers ""}]
      ;; TODO Return a structured map containing the response data. The
      ;; structure needs to be schematized as a spec, e.g.
      ;; {:body {:text ...}}

      ;; FIXME support non-json and pass headers properly
      (case content-type
          "application/json; charset=utf-8"
          (resolve (assoc response :http/body (lib.json/from-json res true)))

          "text/plain;charset=utf-8"
          (resolve (assoc response :http/body res))

          (resolve (assoc response :http/body res))))))

(defn- strip-forbidden-headers
  "We are generally not allowed to set certain headers from
  browser. Returns the given map of headers with any disallowed headers
  removed."
  [headers]
  {:pre [(map? headers)]}
  (let [forbidden #{"user-agent"}]
    (into {} (remove (fn [[header _]]
                       (let [header (-> header cstr/trim cstr/lower-case)]
                         (contains? forbidden header)))
                     headers))))

;; Public
;; -----------------------------------------------------------------------------

(defrecord HttpClient []
  proto.http/HttpClient
  (request!
    [this request]
    (promise
     (fn [resolve reject]
       (if-not (malli/validate spec.http/request request)
         (reject (lib.error/explain spec.http/request request))
         ;; The request map is valid, so fire off the request.
         (let [url-or-error (http.shared/request->url request)]
           (if (lib.error/error? url-or-error)
             ;; Constructed URL wasn't valid, return an error map.
             url-or-error
             ;; Perform the HTTP request.
             (let [method (http.shared/request->method request)
                   body (http.shared/request->body request)
                   headers (-> request
                               http.shared/request->headers
                               ;; Some headers may not be sent from a
                               ;; browser context, so we'll remove them
                               ;; here.
                               strip-forbidden-headers)
                   headers-obj (clj->js headers)
                   ;; 0 means no timeout.
                   timeout-ms 0
                   on-response (make-response-fn resolve)]
               (xhrio/send url-or-error
                           on-response
                           method body
                           headers-obj
                           timeout-ms)))))))))
