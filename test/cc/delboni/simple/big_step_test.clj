(ns cc.delboni.simple.big-step-test
  (:require [cc.delboni.simple.big-step :refer [->If ->Sequence ->While
                                                ->DoNothing ->Assign
                                                ->Add ->Bool ->LessThan
                                                ->Multiply ->Numeric
                                                ->Variable evaluate]]
            [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]))

(deftest values-test
  (testing "should evaluate values"
    (is (match? {:value 23}
                (evaluate (->Numeric 23) {})))

    (is (match? {:value false}
                (evaluate (->Bool false) {})))

    (is (match? {:value 23}
                (-> (->Variable :x)
                    (evaluate {:x (->Numeric 23)}))))))

(deftest expressions-test
  (testing "should evaluate expressions"
    (is (match? {:value 6}
                (-> (->Add (->Numeric 3) (->Numeric 3))
                    (evaluate {}))))

    (is (match? {:value 9}
                (-> (->Multiply (->Numeric 3) (->Numeric 3))
                    (evaluate {}))))

    (is (match? {:value true}
                (-> (->LessThan (->Numeric 1) (->Numeric 3))
                    (evaluate {}))))

    (is (match? {:value false}
                (-> (->LessThan (->Numeric 4) (->Numeric 3))
                    (evaluate {}))))

    (is (match? {:value true}
                (-> (->LessThan (->Add (->Variable :x) (->Numeric 2))
                                (->Variable :y))
                    (evaluate {:x (->Numeric 2)
                               :y (->Numeric 5)}))))))

(deftest statements-test
  (testing "should evaluate statements and return updated environment"
    (is (match? {:x {:value 12}}
                (evaluate (->DoNothing)
                          {:x (->Numeric 12)})))

    (is (match? {:x {:value false}}
                (evaluate (->Assign :x (->Bool false))
                          {})))

    (is (match? {:x {:value 2}
                 :y {:value 5}}
                (evaluate (->Assign :y (->Add (->Variable :x) (->Numeric 3)))
                          {:x (->Numeric 2)})))

    (is (match? {:x {:value 2}
                 :y {:value 5}}
                (evaluate (->If (->LessThan (->Numeric 1) (->Numeric 3))
                                (->Assign :y (->Add (->Variable :x) (->Numeric 3)))
                                (->Assign :y (->Numeric 1)))
                          {:x (->Numeric 2)})))

    (is (match? {:x {:value 2}
                 :y {:value 1}}
                (evaluate (->If (->LessThan (->Numeric 4) (->Numeric 3))
                                (->Assign :y (->Add (->Variable :x) (->Numeric 3)))
                                (->Assign :y (->Numeric 1)))
                          {:x (->Numeric 2)})))

    (is (match? {:x {:value 2}
                 :y {:value 5}}
                (evaluate (->Sequence
                           (->Assign :x (->Add (->Numeric 1) (->Numeric 1)))
                           (->Assign :y (->Add (->Variable :x) (->Numeric 3))))
                          {:x (->Numeric 2)})))

    (is (match? {:x {:value 9}}
                (evaluate (->While
                           (->LessThan (->Variable :x) (->Numeric 5))
                           (->Assign :x (->Multiply (->Variable :x) (->Numeric 3))))
                          {:x (->Numeric 1)})))))
