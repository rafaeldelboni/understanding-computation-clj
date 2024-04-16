(ns cc.delboni.simple.small-step-test
  (:require [cc.delboni.simple.small-step :refer [->Add ->Multiply ->Numeric
                                                  -reduce -reducible?]]
            [clojure.test :refer [deftest is testing]]))

(deftest str-test
  (testing "toString override should work."
    (is (= "5"
           (str (->Numeric 5))))

    (is (= "1 + 2"
           (str (->Add (->Numeric 1) (->Numeric 2)))))

    (is (= "3 * 4"
           (str (->Multiply (->Numeric 3) (->Numeric 4)))))

    (is (= "1 * 2 + 3 * 4"
           (str (->Add
                 (->Multiply (->Numeric 1) (->Numeric 2))
                 (->Multiply (->Numeric 3) (->Numeric 4))))))))

(deftest reducible?-test
  (testing "reducible? definition should work."
    (is (= false
           (-reducible? (->Numeric 5))))

    (is (= true
           (-reducible? (->Add (->Numeric 1) (->Numeric 2)))))

    (is (= true
           (-reducible? (->Multiply (->Numeric 3) (->Numeric 4)))))

    (is (= true
           (-reducible? (->Add
                        (->Multiply (->Numeric 1) (->Numeric 2))
                        (->Multiply (->Numeric 3) (->Numeric 4))))))))

(deftest reduce-test
  (testing "reducible? definition should work."
    (is (thrown? IllegalArgumentException
                 (-reduce (->Numeric 5))))

    (is (= "3"
           (str (-reduce (->Add (->Numeric 1) (->Numeric 2))))))

    (is (= "12"
           (str (-reduce (->Multiply (->Numeric 3) (->Numeric 4))))))

    (is (= "2 + 3 * 4"
           (-> (->Add
                (->Multiply (->Numeric 1) (->Numeric 2))
                (->Multiply (->Numeric 3) (->Numeric 4)))
               -reduce
               str)))

    (is (= "2 + 12"
           (-> (->Add
                (->Multiply (->Numeric 1) (->Numeric 2))
                (->Multiply (->Numeric 3) (->Numeric 4)))
               -reduce
               -reduce
               str)))

    (is (= "14"
           (-> (->Add
                (->Multiply (->Numeric 1) (->Numeric 2))
                (->Multiply (->Numeric 3) (->Numeric 4)))
               -reduce
               -reduce
               -reduce
               str)))))
