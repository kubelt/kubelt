(ns com.kubelt.lib.wallet.node
  "The Node.js implementation of a crypto wallet wrapper."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"}
  (:refer-clojure :exclude [import])
  (:require
   ["fs" :refer [promises] :rename {promises fs-promises} :as fs]
   ["path" :as path])
  (:require
   ["@ethersproject/wallet" :refer [Wallet]])
  (:require
   [cljs.core.async :as async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]])
  (:require
   [com.kubelt.lib.error :as lib.error]
   [com.kubelt.lib.path :as lib.path]
   [com.kubelt.lib.promise :as lib.promise]
   [com.kubelt.lib.wallet.shared :as lib.wallet]))

(defn- fs-exists?&
  "Return the file location if exists"
  [file]
  (-> (.access fs-promises file  (.. fs -constants -F_OK))
      (lib.promise/then (fn [_] file))))

(defn- wallet-dir
  "Return the wallet directory path as a string for an application."
  [app-name]
  (let [config-path (lib.path/data app-name)
        wallet-path (.join path config-path "wallets")]
    wallet-path))

(defn- ensure-wallet-dir
  "Return the wallet directory path for the application as a string,
  creating it if it doesn't already exist."
  [app-name]
  (let [wallet-dirp (wallet-dir app-name)]
    (when-not (.existsSync fs wallet-dirp)
      (let [mode "0700"
            recursive? true
            options #js {:mode mode
                         :recursive recursive?}]
        (.mkdirSync fs wallet-dirp options)))
    wallet-dirp))

(defn- name->path
  "Return the path to a wallet given the owning application name and
  wallet name."
  [app-name wallet-name]
  (let [wallet-path (wallet-dir app-name)
        wallet-path (.join path wallet-path wallet-name)]
    wallet-path))

;; Unused predicate
(defn- valid-perms?
  "Return true if the named wallet has the correct permissions, false
  otherwise."
  [app-name wallet-name]
  (let [read-ok (.. fs -constants -R_OK)
        write-ok (.. fs -constants -W_OK)
        perms (bit-or read-ok write-ok)
        wallet-path (name->path app-name wallet-name)]
    (try
      (.accessSync fs wallet-path read-ok)
      (catch js/Error e
        false))))

;; Public
;; -----------------------------------------------------------------------------
;; TODO allow wallet re-creation from mnemonic
;; TODO allow wallet listing
;; TODO allow wallet deletion

(defn has-wallet?
  "Return true if the named wallet already exists."
  [app-name wallet-name]
  (let [wallet-path (name->path app-name wallet-name)]
    (.existsSync fs wallet-path)))

(defn can-decrypt?
  "Return true if the wallet can be successfully decrypted with the
  supplied password, and false otherwise."
  [app-name wallet-name password]
  (if (has-wallet? app-name wallet-name)
    (let [wallet-path (name->path app-name wallet-name)
          wallet-str (.readFileSync fs wallet-path)]
      (try
        (.fromEncryptedJsonSync Wallet wallet-str password)
        true
        (catch js/Error e
          ;; Error: invalid password
          false)))
    false))

(defn init
  "Create and store an encrypted wallet. The encrypted wallet file is
  stored in an XDG compliant location based on the application name. It
  is also named using the supplied wallet name and encrypted with the
  supplied password. A map describing the created wallet is returned if
  successful. An error map is returned otherwise."
  [app-name wallet-name password]
  (let [;; Create the wallet directory if it doesn't already exist.
        wallet-dirp (ensure-wallet-dir app-name)]
    ;; It's an error to initialize an existing wallet.
    (when (has-wallet? app-name wallet-name)
      (let [message (str "wallet " wallet-name " already exists")]
        (lib.error/error message)))
    ;; Wallet doesn't yet exist, so create it!
    (let [wallet-path (.join path wallet-dirp wallet-name)
          eth-wallet (.createRandom Wallet)
          mnemonic (.-mnemonic eth-wallet)]
      (go
        (let [wallet-js (<p! (.encrypt eth-wallet password))]
          (.writeFileSync fs wallet-path wallet-js)))
      (let [{:keys [phrase path locale]} (js->clj mnemonic :keywordize-keys true)]
        {:wallet/path wallet-path
         :wallet/name wallet-name
         :wallet.mnemonic/phrase phrase
         :wallet.mnemonic/path path
         :wallet.mnemonic/locale locale}))))

(defn load
  "Return a promise that resolves to a wallet map, or that rejects with an
  error map if a problem occurs."
  [app-name wallet-name password]
  (let [wallet-dirp (ensure-wallet-dir app-name)]
    (lib.promise/promise
     (fn [resolve reject]
       (when-not (has-wallet? app-name wallet-name)
         (let [message (str "wallet " wallet-name " doesn't exist")]
           (reject (lib.error/error message))))
       ;; Load the wallet JSON
       (let [wallet-path (name->path app-name wallet-name)]
         (.readFile fs wallet-path "utf8"
          (fn [err wallet-str]
            (if err
              (reject (lib.error/error err))
              (let [eth-wallet (.fromEncryptedJsonSync Wallet wallet-str password)
                    address (.-address eth-wallet)
                    sign-fn (lib.wallet/make-sign-fn eth-wallet)]
                (resolve
                 {:com.kubelt/type :kubelt.type/wallet
                  :wallet/address address
                  :wallet/sign-fn sign-fn}))))))))))

(defn ls
  "Return a list of wallet names."
  [app-name]
  (let [wallet-dirp (ensure-wallet-dir app-name)
        wallet-files (.readdirSync fs wallet-dirp)]
    (js->clj wallet-files)))

(defn delete!
  "Delete a wallet."
  [app-name wallet-name]
  (let [wallet-path (name->path app-name wallet-name)]
    (.unlinkSync fs wallet-path)))

(defn create
  ""
  []
  :fixme
  ;; (<! (lib.wallet/random-wallet))
  #_(let [sign-fn (lib.wallet/make-sign-fn eth-wallet)]
    {:wallet/address :fixme
     :wallet/sign-fn sign-fn}))

(defn import
  "Import a wallet and store it encrypted. Returns a promise that resolves
  to the path to the imported wallet, or if an error occurs rejects with
  a standard error map."
  [app-name wallet-name mnemonic password]
  (lib.promise/promise
   (fn [resolve reject]
     (when (has-wallet? app-name wallet-name)
       (let [msg (str "wallet " wallet-name " already exists")
             error (lib.error/error msg)]
         (reject error)))
     ;; The wallet with the given name doesn't yet exist, we can import
     ;; from the mnemonic and create a wallet with that name.
     (letfn [;; Returns a promise that resolves to the wallet
             ;; JSON. Throws if the mnemonic is invalid.
             (from-mnemonic [mnemonic]
               (try
                 (let [w (.fromMnemonic Wallet mnemonic)]
                   ;; Returns a promise.
                   (.encrypt w password))
                 (catch js/Error e
                   (let [error (lib.error/from-obj e)]
                     (reject error)))))
             ;; Returns the path of the wallet file to write.
             (wallet-path []
               (let [wallet-dir (ensure-wallet-dir app-name)
                     wallet-file (.join path wallet-dir wallet-name)]
                 (lib.promise/resolved wallet-file)))]
       (let [path& (wallet-path)
             wallet& (from-mnemonic mnemonic)]
         (-> (lib.promise/all [path& wallet&])
             (.then (fn [[wallet-dirp wallet-js]]
                      (try
                        (.writeFileSync fs wallet-dirp wallet-js)
                        (let [result {:wallet/name wallet-name
                                      :wallet/path wallet-path}]
                          (resolve result))
                        (catch js/Error e
                          (let [error (lib.error/from-obj e)]
                            (reject error))))))
             (.catch (fn [e]
                       (let [error (lib.error/from-obj e)]
                         (reject error))))))))))
