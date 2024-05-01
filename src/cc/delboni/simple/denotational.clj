(ns cc.delboni.simple.denotational)

(defn invoke
  ([f] (invoke f {}))
  ([f e] (f e)))

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

; Expressions
(defrecord Add [left right]
  Expression
  (->clj [_]
    `(fn [environment#]
       (+ (-> ~left ->clj eval (invoke environment#))
          (-> ~right ->clj eval (invoke environment#))))))

(defrecord Multiply [left right]
  Expression
  (->clj [_]
    `(fn [environment#]
       (* (-> ~left ->clj eval (invoke environment#))
          (-> ~right ->clj eval (invoke environment#))))))

(defrecord LessThan [left right]
  Expression
  (->clj [_]
    `(fn [environment#]
       (< (-> ~left ->clj eval (invoke environment#))
          (-> ~right ->clj eval (invoke environment#))))))

; Statements
(defrecord DoNothing []
  Expression
  (->clj [_]
    `(fn [environment#]
       environment#)))

(defrecord Assign [var-name expression]
  Expression
  (->clj [_]
    `(fn [environment#]
       (assoc environment#
              ~var-name (-> ~expression ->clj eval (invoke environment#))))))

(defrecord If [condition consequence alternative]
  Expression
  (->clj [_]
    `(fn [environment#]
       (if (-> ~condition ->clj eval (invoke environment#))
         (-> ~consequence ->clj eval (invoke environment#))
         (-> ~alternative ->clj eval (invoke environment#))))))

(defrecord Sequence [one two]
  Expression
  (->clj [_]
    `(fn [environment#]
       (let [evaluated-one# (-> ~one ->clj eval (invoke environment#))]
         (-> ~two ->clj eval (invoke evaluated-one#))))))

(defrecord While [condition body]
  Expression
  (->clj [_]
    `(fn [environment#]
       (loop [env# environment#]
         (if-not (-> ~condition ->clj eval (invoke env#))
           env#
           (recur (-> ~body ->clj eval (invoke env#))))))))
