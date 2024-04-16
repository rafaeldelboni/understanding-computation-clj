(ns cc.delboni.simple.small-step)

(defprotocol Expression
  (-reducible? [_]
    "Predicate that returns true or false depending on the class of its argument."))

(defprotocol Reducible
  (-reduce [_]
    "Expression uses left-to-right evaluation to reduce its arguments."))

(defrecord Numeric [value]
  Object
  (toString [_]
    (str value))

  Expression
  (-reducible? [_] false))

(defrecord Add [left right]
  Object
  (toString [_]
    (str left " + " right))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [_]
    (cond
      (-reducible? left) (->Add (-reduce left) right)
      (-reducible? right) (->Add left (-reduce right))
      :else (->Numeric (+ (:value left) (:value right))))))

(defrecord Multiply [left right]
  Object
  (toString [_]
    (str left " * " right))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [_]
    (cond
      (-reducible? left) (->Multiply (-reduce left) right)
      (-reducible? right) (->Multiply left (-reduce right))
      :else (->Numeric (* (:value left) (:value right))))))
