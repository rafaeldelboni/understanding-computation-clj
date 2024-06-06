(ns cc.delboni.simple-computers.nfa-test
  (:require [cc.delboni.simple-computers.nfa :refer [->NFARulebook
                                                     next-state]]
            [cc.delboni.simple-computers.fa :refer [->FARule]]
            [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]))

(def rulebook
  (->NFARulebook
   [(->FARule 1 \a 1) (->FARule 1 \b 1) (->FARule 1 \b 2)
    (->FARule 2 \a 3) (->FARule 2 \b 3)
    (->FARule 3 \a 4) (->FARule 3 \b 4)]))

(deftest next-state-test
  (testing "should evaluate next-state"
    (is (match? #{1 2}
                (next-state rulebook #{1} \b)))

    (is (match? #{1 3}
                (next-state rulebook #{1 2} \a)))

    (is (match? #{1 2 4}
                (next-state rulebook #{1 3} \b)))))
