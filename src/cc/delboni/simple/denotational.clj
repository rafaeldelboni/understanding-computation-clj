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
; (defrecord DoNothing []
;   Expression
;   (evaluate [_ environment]
;     environment))
;
; (defrecord Assign [var-name expression]
;   Expression
;   (evaluate [_ environment]
;     (assoc environment var-name (evaluate expression environment))))
;
; (defrecord If [condition consequence alternative]
;   Expression
;   (evaluate [_ environment]
;     (if (:value (evaluate condition environment))
;       (evaluate consequence environment)
;       (evaluate alternative environment))))
;
; (defrecord Sequence [one two]
;   Expression
;   (evaluate [_ environment]
;     (evaluate two (evaluate one environment))))
;
; (defrecord While [condition body]
;   Expression
;   (evaluate [_ environment]
;     (loop [env environment]
;       (if-not (:value (evaluate condition env))
;         env
;         (recur (evaluate body env))))))
