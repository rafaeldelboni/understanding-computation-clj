(ns cc.delboni.simple-computers.dfa-test
  (:require [cc.delboni.simple-computers.dfa :refer [->DFARulebook ->FARule
                                                     next-state]]
            [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]))

(deftest values-test
  (testing "should evaluate values"
    (let [rulebook (->DFARulebook
                    [(->FARule 1 'a' 2) (->FARule 1 'b' 1)
                     (->FARule 2 'a' 2) (->FARule 2 'b' 3)
                     (->FARule 3 'a' 3) (->FARule 3 'b' 3)])]
      (is (match? 2
                  (next-state rulebook 1 'a')))

      (is (match? 1
                  (next-state rulebook 1 'b')))

      (is (match? 3
                  (next-state rulebook 2 'b'))))))
