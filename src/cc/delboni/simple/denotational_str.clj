(ns cc.delboni.simple.denotational-str)

(defn reval [code-str]
  (eval (read-string code-str)))

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
    (str "(fn [_] " value ")")))

(defrecord Bool [value]
  Expression
  (->clj [_]
    (str "(fn [_] " value ")")))

(defrecord Variable [var-name]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (get environment " var-name "))")))

; Expressions
(defrecord Add [left right]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (+ (" (->clj left) " environment)
               (" (->clj right) " environment)))")))

(defrecord Multiply [left right]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (* (" (->clj left) " environment)
               (" (->clj right) " environment)))")))

(defrecord LessThan [left right]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (< (" (->clj left) " environment)
               (" (->clj right) " environment)))")))

; Statements
(defrecord DoNothing []
  Expression
  (->clj [_]
    (str "(fn [environment]
            environment)")))

(defrecord Assign [var-name expression]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (assoc environment " var-name " (" (->clj expression) " environment)))")))

(defrecord If [condition consequence alternative]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (if (" (->clj condition) " environment)
              (" (->clj consequence) " environment)
              (" (->clj alternative) " environment)))")))

(defrecord Sequence [one two]
  Expression
  (->clj [_]
    (str "(fn [environment]
              (" (->clj two) " (" (->clj one) " environment)))")))

(defrecord While [condition body]
  Expression
  (->clj [_]
    (str "(fn [environment]
            (loop [env environment]
              (if-not (" (->clj condition) " env)
                env
                (recur (" (->clj body) " env)))))")))
