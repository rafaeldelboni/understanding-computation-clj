(ns cc.delboni.simple.denotational-test
  (:require [cc.delboni.simple.denotational :refer [->Add ->Assign ->Bool
                                                    ->Bool ->clj ->DoNothing
                                                    ->LessThan ->Multiply
                                                    ->Numeric ->Variable
                                                    ->If ->Sequence ->While invoke]]
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

(deftest statements-test
  (testing "should evaluate statements and return updated environment"
    (is (match? {:x 12}
                (-> (->DoNothing)
                    ->clj
                    eval
                    (invoke {:x 12}))))

    (is (match? {:x false}
                (-> (->Assign :x (->Bool false))
                    ->clj
                    eval
                    (invoke {}))))

    (is (match? {:x 2
                 :y 5}
                (-> (->Assign :y (->Add (->Variable :x) (->Numeric 3)))
                    ->clj
                    eval
                    (invoke {:x 2}))))

    (is (match? {:x 2
                 :y 5}
                (-> (->If (->LessThan (->Numeric 1) (->Numeric 3))
                          (->Assign :y (->Add (->Variable :x) (->Numeric 3)))
                          (->Assign :y (->Numeric 1)))
                    ->clj
                    eval
                    (invoke {:x 2}))))

    (is (match? {:x 2
                 :y 1}
                (-> (->If (->LessThan (->Numeric 4) (->Numeric 3))
                          (->Assign :y (->Add (->Variable :x) (->Numeric 3)))
                          (->Assign :y (->Numeric 1)))
                    ->clj
                    eval
                    (invoke {:x 2}))))

    (is (match? {:x 2
                 :y 5}
                (-> (->Sequence
                     (->Assign :x (->Add (->Numeric 1) (->Numeric 1)))
                     (->Assign :y (->Add (->Variable :x) (->Numeric 3))))
                    ->clj
                    eval
                    (invoke {:x 0}))))

    (is (match? {:x 9}
                (-> (->While
                     (->LessThan (->Variable :x) (->Numeric 5))
                     (->Assign :x (->Multiply (->Variable :x) (->Numeric 3))))
                    ->clj
                    eval
                    (invoke {:x 1}))))))
