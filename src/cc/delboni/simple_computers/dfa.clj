(ns cc.delboni.simple-computers.dfa 
  (:require [cc.delboni.simple-computers.fa :as fa]))

(defprotocol DFARulebookProtocol
  (rule-for [self state character])
  (next-state [self state character]))

(defprotocol DFAProtocol
  (accepting? [self])
  (read-char [self character])
  (read-str [self string]))

(defprotocol DFADesignProtocol
  (to-dfa [self])
  (accepts? [self string]))

(defrecord DFARulebook [rules]
  DFARulebookProtocol
  (rule-for [_ state character]
    (some (fn [rule] (when (fa/applies-to? rule state character) rule)) rules))

  (next-state [this state character]
    (-> (rule-for this state character)
        fa/follow)))

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
