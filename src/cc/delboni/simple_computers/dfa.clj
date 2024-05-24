(ns cc.delboni.simple-computers.dfa)

(defprotocol Rule
  (applies-to? [self state character])
  (follow [self]))

(defprotocol Rulebook
  (rule-for [self state character])
  (next-state [self state character]))

(defprotocol DFAProtocol
  (accepting? [self])
  (read-char [self character])
  (read-str [self string]))

(defprotocol DFADesignProtocol
  (to-dfa [self])
  (accepts? [self string]))

(defrecord FARule [state character next-state]
  Object
  (toString [_]
    (format "(FARule %s --%s--> %s)" state character next-state))

  Rule
  (applies-to? [self state character]
    (and (= (:state self) state)
         (= (:character self) character)))

  (follow [_] next-state))

(defrecord DFARulebook [rules]
  Rulebook
  (rule-for [_ state character]
    (some (fn [rule] (when (applies-to? rule state character) rule)) rules))

  (next-state [this state character]
    (-> (rule-for this state character)
        follow)))

(defrecord DFA [current-state accept-states rulebook]
  DFAProtocol
  (accepting? [_]
    (boolean (some #(= % current-state) accept-states)))

  (read-char [self character]
    (->DFA (next-state (:rulebook self) current-state character)
           (:accept-states self)
           (:rulebook self)))

  (read-str [self string]
    (reduce
     (fn [acc cur] (read-char acc cur)) self (vec string))))

(defrecord DFADesign [start-state accept-state rulebook]
  DFADesignProtocol
  (to-dfa [self]
    (->DFA (:start-state self) (:accept-state self) (:rulebook self)))

  (accepts? [self string]
    (-> (to-dfa self)
        (read-str string)
        accepting?)))
