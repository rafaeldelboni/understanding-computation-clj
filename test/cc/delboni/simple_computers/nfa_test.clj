(ns cc.delboni.simple-computers.nfa-test
  (:require [cc.delboni.simple-computers.fa :refer [->FARule]]
            [cc.delboni.simple-computers.nfa :refer [->NFA ->NFARulebook
                                                     ->NFADesign
                                                     accepting? accepts?
                                                     next-states read-char
                                                     read-str]]
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
                (next-states rulebook #{1} \b)))

    (is (match? #{1 3}
                (next-states rulebook #{1 2} \a)))

    (is (match? #{1 2 4}
                (next-states rulebook #{1 3} \b)))))

(deftest accepting-test
  (testing "should accept or not an list of possible states vs possible acceptances"
    (is (match? false
                (-> (->NFA #{1} #{4} rulebook)
                    accepting?)))

    (is (match? true
                (-> (->NFA #{1 2 4} #{4} rulebook)
                    accepting?)))

    (is (match? false
                (-> (->NFA #{1} #{4} rulebook)
                    (read-char \b)
                    accepting?)))

    (is (match? false
                (-> (->NFA #{1} #{4} rulebook)
                    (read-char \b)
                    (read-char \a)
                    accepting?)))

    (is (match? true
                (-> (->NFA #{1} #{4} rulebook)
                    (read-char \b)
                    (read-char \a)
                    (read-char \b)
                    accepting?)))

    (is (match? true
                (-> (->NFA #{1} #{3} rulebook)
                    (read-str "bbbbb")
                    accepting?)))))

(deftest nfa-design-test
  (testing "should create new nfa from start and read-str to put into a new state"
    (let [nfa-design (->NFADesign 1 #{4} rulebook)]
      (is (match? true
                  (accepts? nfa-design "bab")))

      (is (match? true
                  (accepts? nfa-design "bbbbb")))

      (is (match? false
                  (accepts? nfa-design "bbabb"))))))
