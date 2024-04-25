(ns cc.delboni.simple.denotational)

(defprotocol Expression
  (->clj [self]
    "Denotational semantics for Simple by translating it into Clojure."))

; Values
(defrecord Numeric [value]
  Expression
  (->clj [_]
    `(fn [_#] ~value)))

(defrecord Bool [value]
  Expression
  (->clj [_]
    `(fn [_#] ~value)))

(defrecord Variable [var-name]
  Expression
  (->clj [_]
    `(fn [environment#]
       (get environment# ~var-name))))
