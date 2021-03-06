(ns com.kubelt.proto.bag-io
  "A protocol for reading and writing BAGs."
  {:copyright "©2022 Proof Zero Inc." :license "Apache 2.0"})


(defprotocol BagReader
  "Read BAGs (Bundle of Acyclic Graph)."
  (read-bag [this cid] "Read a BAG"))

(defprotocol BagWriter
  "Write a BAG."
  (write-bag [this bag] "Write a BAG"))
