(ns cc.delboni.simple-computers.fa)

(defprotocol Rule
  (applies-to? [self state character])
  (follow [self]))

(defrecord FARule [state character next-state]
  Object
  (toString [_]
    (format "(FARule %s --%s--> %s)" state character next-state))

  Rule
  (applies-to? [self state character]
    (and (= (:state self) state)
         (= (:character self) character)))

  (follow [_] next-state))
