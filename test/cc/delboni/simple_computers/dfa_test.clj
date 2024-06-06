(ns cc.delboni.simple-computers.dfa-test
  (:require [cc.delboni.simple-computers.dfa :refer [->DFA ->DFADesign
                                                     ->DFARulebook
                                                     next-state
                                                     accepting? accepts?
                                                     read-char
                                                     read-str]]
            [cc.delboni.simple-computers.fa :refer [->FARule]]
            [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]))

(def rulebook
  (->DFARulebook
   [(->FARule 1 \a 2) (->FARule 1 \b 1)
    (->FARule 2 \a 2) (->FARule 2 \b 3)
    (->FARule 3 \a 3) (->FARule 3 \b 3)]))

(deftest next-state-test
  (testing "should evaluate next-state"
    (is (match? 2
                (next-state rulebook 1 \a)))

    (is (match? 1
                (next-state rulebook 1 \b)))

    (is (match? 3
                (next-state rulebook 2 \b)))))

(deftest accepting-test
  (testing "should accept or not an state"
    (is (match? true
                (-> (->DFA 1 [1 3] rulebook)
                    accepting?)))
    (is (match? false
                (-> (->DFA 1 [3] rulebook)
                    accepting?)))

    (is (match? false
                (-> (->DFA 1 [3] rulebook)
                    (read-char \b)
                    accepting?)))

    (is (match? false
                (-> (->DFA 1 [3] rulebook)
                    (read-char \b)
                    (read-char \a)
                    (read-char \a)
                    (read-char \a)
                    accepting?)))

    (is (match? true
                (-> (->DFA 1 [3] rulebook)
                    (read-char \b)
                    (read-char \a)
                    (read-char \a)
                    (read-char \a)
                    (read-char \b)
                    accepting?)))

    (is (match? true
                (-> (->DFA 1 [3] rulebook)
                    (read-str "baaab")
                    accepting?)))))

(deftest dfa-design-test
  (testing "should create new dfa from start and read-str to put into a new state"
    (let [dfa-design (->DFADesign 1 [3] rulebook)]
      (is (match? false
                  (accepts? dfa-design "a")))

      (is (match? false
                  (accepts? dfa-design "ba")))

      (is (match? false
                  (accepts? dfa-design "baa")))

      (is (match? true
                  (accepts? dfa-design "bab")))

      (is (match? true
                  (accepts? dfa-design "baba"))))))

