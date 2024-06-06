(ns cc.delboni.simple-computers.nfa
  (:require [cc.delboni.simple-computers.fa :as fa]))

(defprotocol NFARulebookProtocol
  (rules-for [self state character])
  (follow-rules-for [self state character])
  (next-state [self states character]))

(defrecord NFARulebook [rules]
  NFARulebookProtocol
  (rules-for [_ state character]
    (filter #(fa/applies-to? % state character) rules))

  (follow-rules-for [this state character]
    (->> character
         (rules-for this state)
         (map fa/follow)))

  (next-state [this states character]
    (->> states
         (map (fn [state] (follow-rules-for this state character)))
         flatten
         set)))
