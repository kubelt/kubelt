(ns com.kubelt.lib.uri
  "URI-related utilities."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:require
   [clojure.string :as cstr])
  (:require
   [camel-snake-kebab.core :as csk]
   [lambdaisland.uri :as lambda])
  (:require
   [com.kubelt.lib.error :as lib.error]))

;; TODO test test test

;; TODO define com.kubelt.spec.uri schema

(def spec-parse-url
  :string)

(def spec-parse-opts
  [:map
   [:query/convert? {:optional true} :boolean]
   [:query/keywordize? {:optional true} :boolean]
   [:query/hyphenate? {:optional true} :boolean]])

(def parse-defaults
  {:query/convert? true
   :query/keywordize? true
   :query/hyphenate? true})

(defn- query->map
  "Convert a query string into a map."
  [query-str {:keys [query/keywordize? query/hyphenate?]}]
  {:pre [(string? query-str)]}
  (into {}
        (map (fn [s]
               (let [[k v] (cstr/split s #"=")
                     k (cond-> k
                         ;; convert string keys to keywords
                         keywordize?
                         keyword
                         ;; convert keys to kebab case
                         hyphenate?
                         csk/->kebab-case)]
                 [k v]))
             (cstr/split query-str #"&"))))

(defn- parse-query
  "Given a query string, process it according to the configured
  options. With an empty options map, the same query string is
  returned."
  [query-str {:keys [query/convert?] :as options}]
  {:pre [(string? query-str)]}
  (if convert?
    (query->map query-str options)
    query-str))

(defn- extract
  ([v k]
   (extract v k identity))
  ([v k f]
   (let [[k k2] (if (vector? k) k [k k])]
     (some->> v k f (hash-map k2)))))

;; parse
;; -----------------------------------------------------------------------------

(defn parse
  "Given a URL string, parse the URL into component parts and return as a
  map. Available options include:
  - :query/convert?, convert query string into a map (default: true)
  - :query/keywordize?, convert query map keys to keywords (default: true)
  - :query/hyphenate?, convert query map keys to kebab case (default: true)"
  ([s]
   (parse s parse-defaults))

  ([s options]
   (lib.error/conform*
    [spec-parse-url s]
    [spec-parse-opts options]
    (let [options (merge parse-defaults options)
          uri (lambda/uri s)]
      (merge {:com.kubelt/type :kubelt.type/uri}
             (extract uri [:scheme :uri/scheme] keyword)
             (extract uri [:host :uri/domain])
             (extract uri [:port :uri/port])
             (extract uri [:path :uri/path])
             (extract uri [:fragment :uri/fragment])
             (extract uri [:query :uri/query] #(parse-query % options))
             (extract uri [:user :uri/user])
             (extract uri [:password :uri/password]))))))

;; unparse
;; -----------------------------------------------------------------------------
;; TODO convert a URI map back into a URI string.

(defn unparse
  ""
  [m]
  #_(lib.error/conform*
     [])
  :fixme
  )

;; expand
;; -----------------------------------------------------------------------------
;; TODO convert a URL template string and associated parameter values
;; into a URI string.

(defn expand
  "Given a URL template and a map of parameter values, return the expanded
  URI. If no variables are supplied, the template is returned unchanged."
  [tpl variables]
  {:pre [(string? tpl) ((some-fn [nil? map?]) variables)]}
  ;; TODO actually expand this template
  tpl)
