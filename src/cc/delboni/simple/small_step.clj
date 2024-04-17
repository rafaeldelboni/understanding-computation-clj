(ns cc.delboni.simple.small-step)

(defprotocol Expression
  (-reducible? [_]
    "Predicate that returns true or false depending on the class of its argument."))

(defprotocol Reducible
  (-reduce [_] [_ environment]
    "Expression uses left-to-right evaluation to reduce its arguments."))

; Types
(defrecord Numeric [value]
  Object
  (toString [_]
    (str value))

  Expression
  (-reducible? [_] false))

(defrecord Bool [value]
  Object
  (toString [_]
    (str value))

  Expression
  (-reducible? [_] false))

; Operations
(defrecord Add [left right]
  Object
  (toString [_]
    (str left " + " right))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [this]
    (-reduce this {}))

  (-reduce [_ environment]
    (cond
      (-reducible? left) (->Add (-reduce left environment) right)
      (-reducible? right) (->Add left (-reduce right environment))
      :else (->Numeric (+ (:value left) (:value right))))))

(defrecord Multiply [left right]
  Object
  (toString [_]
    (str left " * " right))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [this]
    (-reduce this {}))

  (-reduce [_ environment]
    (cond
      (-reducible? left) (->Multiply (-reduce left environment) right)
      (-reducible? right) (->Multiply left (-reduce right environment))
      :else (->Numeric (* (:value left) (:value right))))))

(defrecord LessThan [left right]
  Object
  (toString [_]
    (str left " < " right))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [this]
    (-reduce this {}))

  (-reduce [_ environment]
    (cond
      (-reducible? left) (->LessThan (-reduce left environment) right)
      (-reducible? right) (->LessThan left (-reduce right environment))
      :else (->Bool (< (:value left) (:value right))))))

(defrecord Variable [var-name]
  Object
  (toString [_]
    (str var-name))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [this]
    (-reduce this {}))

  (-reduce [_ environment]
    (get environment var-name)))

(defn machine->run [expressions environment]
  (loop [expression expressions]
    (prn (str expression))
    (if-not (-reducible? expression)
      expression
      (recur (-reduce expression environment)))))
