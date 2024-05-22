(ns cc.delboni.simple-computers.dfa)

(defprotocol Rule
  (applies-to? [self state character])
  (follow [self]))

(defprotocol Rulebook
  (rule-for [self state character])
  (next-state [self state character]))

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
    (some (fn [rule] (if (applies-to? rule state character) rule nil)) rules))

  (next-state [this state character]
    (-> (rule-for this state character)
        follow)))
