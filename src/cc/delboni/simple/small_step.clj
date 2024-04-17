(ns cc.delboni.simple.small-step)

(defprotocol Expression
  (-reducible? [_]
    "Predicate that returns true or false depending on the class of its argument."))

(defprotocol Reducible
  (-reduce [_] [_ environment]
    "Expression uses left-to-right evaluation to reduce its arguments."))

; Expression
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

(defrecord Variable [var-name]
  Object
  (toString [_]
    (name var-name))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [this]
    (-reduce this {}))

  (-reduce [_ environment]
    (get environment var-name)))

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

; Statements
(defrecord DoNothing []
  Object
  (toString [_]
    (str "do-nothing"))

  Expression
  (-reducible? [_] false))

(defrecord Assign [var-name expression]
  Object
  (toString [_]
    (str (name var-name) " = " expression))

  Expression
  (-reducible? [_] true)

  Reducible
  (-reduce [this]
    (-reduce this {}))

  (-reduce [_ environment]
    (if (-reducible? expression)
      [(->Assign var-name (-reduce expression environment)) environment]
      [(->DoNothing) (assoc environment var-name expression)])))

(defn machine->run [statements environment]
  (loop [current-statement statements
         current-environment environment]
    (prn (str current-statement ", " current-environment))
    (if-not (-reducible? current-statement)
      [current-statement current-environment]
      (let [[reduced-statement updated-enviroment] (-reduce current-statement
                                                            current-environment)]
        (recur reduced-statement updated-enviroment)))))
