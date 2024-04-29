(ns cc.delboni.simple.denotational-test
  (:require [cc.delboni.simple.denotational :refer [->Add ->Bool ->Bool ->clj
                                                    ->LessThan ->Numeric ->Multiply
                                                    ->Variable invoke]]
            [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]))

(deftest values-test
  (testing "should evaluate values"
    (is (match? 23
                (-> (->Numeric 23)
                    ->clj
                    eval
                    invoke)))

    (is (match? false
                (-> (->Bool false)
                    ->clj
                    eval
                    invoke)))

    (is (match? 23
                (-> (->Variable :x)
                    ->clj
                    eval
                    (invoke {:x 23}))))))

(deftest expressions-test
  (testing "should evaluate expressions"
    (is (match? 6
                (-> (->Add (->Numeric 3) (->Numeric 3))
                    ->clj
                    eval
                    invoke)))

    (is (match? 9
                (-> (->Multiply (->Numeric 3) (->Numeric 3))
                    ->clj
                    eval
                    invoke)))

    (is (match? true
                (-> (->LessThan (->Numeric 1) (->Numeric 3))
                    ->clj
                    eval
                    invoke)))

    (is (match? false
                (-> (->LessThan (->Numeric 4) (->Numeric 3))
                    ->clj
                    eval
                    invoke)))

    (is (match? true
                (-> (->LessThan (->Add (->Variable :x) (->Numeric 2))
                                (->Variable :y))
                    ->clj
                    eval
                    (invoke {:x 2 :y 5}))))

    (is (match? 4
                (-> (->Add (->Variable :x) (->Numeric 1))
                    ->clj
                    eval
                    (invoke {:x 3}))))

    (is (match? false
                (-> (->LessThan (->Add (->Variable :x) (->Numeric 1))
                                (->Numeric 3))
                    ->clj
                    eval
                    (invoke {:x 3}))))))
