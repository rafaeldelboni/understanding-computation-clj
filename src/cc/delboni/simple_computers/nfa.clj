(ns cc.delboni.simple-computers.nfa
  (:require [cc.delboni.simple-computers.fa :as fa]
            [clojure.set :as set]))

(defprotocol NFARulebookProtocol
  (rules-for [self state character])
  (follow-rules-for [self state character])
  (next-states [self states character])
  (follow-free-moves [self states]))

(defprotocol NFAProtocol
  (current-states [self])
  (accepting? [self])
  (read-char [self character])
  (read-str [self string]))

(defprotocol NFADesignProtocol
  (to-nfa [self])
  (accepts? [self string]))

(defrecord NFARulebook [rules]
  NFARulebookProtocol
  (rules-for [_ state character]
    (filter #(fa/applies-to? % state character) rules))

  (follow-rules-for [this state character]
    (->> character
         (rules-for this state)
         (map fa/follow)))

  (next-states [this states character]
    (->> states
         (map (fn [state] (follow-rules-for this state character)))
         flatten
         set))

  (follow-free-moves [this states]
    (let [more-states (next-states this states nil)]
      (if (set/subset? more-states states)
        states
        (follow-free-moves this (into states more-states))))))

(defrecord NFA [initial-current-states accept-states rulebook]
  NFAProtocol
  (current-states [_]
    (set (follow-free-moves rulebook initial-current-states)))

  (accepting? [self]
    (-> (set/intersection (current-states self) (set accept-states))
        seq
        boolean))

  (read-char [self character]
    (->NFA (next-states (:rulebook self) (current-states self) character)
           (:accept-states self)
           (:rulebook self)))

  (read-str [self string]
    (reduce
     (fn [acc cur] (read-char acc cur)) self (vec string))))

(defrecord NFADesign [start-state accept-states rulebook]
  NFADesignProtocol
  (to-nfa [self]
    (->NFA (hash-set (:start-state self)) (:accept-states self) (:rulebook self)))

  (accepts? [self string]
    (-> (to-nfa self)
        (read-str string)
        accepting?)))

