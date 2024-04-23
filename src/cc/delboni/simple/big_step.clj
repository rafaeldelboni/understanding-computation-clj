(ns cc.delboni.simple.big-step)

(defprotocol Expression
  (evaluate [self environment]
    "Expression evaluation."))

; Values
(defrecord Numeric [value]
  Expression
  (evaluate [self _]
    self))

(defrecord Bool [value]
  Expression
  (evaluate [self _]
    self))

(defrecord Variable [var-name]
  Expression
  (evaluate [_ environment]
    (get environment var-name)))

; Expressions
(defrecord Add [left right]
  Expression
  (evaluate [_ environment]
    (->Numeric (+ (:value (evaluate left environment))
                  (:value (evaluate right environment))))))

(defrecord Multiply [left right]
  Expression
  (evaluate [_ environment]
    (->Numeric (* (:value (evaluate left environment))
                  (:value (evaluate right environment))))))

(defrecord LessThan [left right]
  Expression
  (evaluate [_ environment]
    (->Bool (< (:value (evaluate left environment))
               (:value (evaluate right environment))))))

; Statements
(defrecord DoNothing []
  Expression
  (evaluate [_ environment]
    environment))

(defrecord Assign [var-name expression]
  Expression
  (evaluate [_ environment]
    (assoc environment var-name (evaluate expression environment))))

(defrecord If [condition consequence alternative]
  Expression
  (evaluate [_ environment]
    (if (:value (evaluate condition environment))
      (evaluate consequence environment)
      (evaluate alternative environment))))

(defrecord Sequence [one two]
  Expression
  (evaluate [_ environment]
    (evaluate two (evaluate one environment))))

(defrecord While [condition body]
  Expression
  (evaluate [_ environment]
    (loop [env environment]
      (if-not (:value (evaluate condition env))
        env
        (recur (evaluate body env))))))
