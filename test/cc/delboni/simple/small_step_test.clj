(ns cc.delboni.simple.small-step-test
  (:require [cc.delboni.simple.small-step :refer [->Add ->LessThan ->Multiply
                                                  ->Numeric ->Variable -reduce
                                                  -reducible? machine->run]]
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
                 (->Multiply (->Numeric 3) (->Numeric 4))))))

    (is (= "1 < 2"
           (str (->LessThan (->Numeric 1) (->Numeric 2)))))))

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
               str)))

    (is (= "true"
           (-> (->LessThan (->Numeric 1) (->Numeric 2))
               -reduce
               str)))

    (is (= "false"
           (-> (->LessThan (->Numeric 2) (->Numeric 1))
               -reduce
               str)))

    (is (= ""
           (-> (->Variable :x)
               -reduce
               str)))

    (is (= "5"
           (-> (->Variable :x)
               (-reduce {:x (->Numeric 5)})
               str)))))

(deftest machine-test
  (testing "machine->run should loop through all expression and reduce without environment"
    (is (= #cc.delboni.simple.small_step.Numeric{:value 14}
           (machine->run (->Add
                          (->Multiply (->Numeric 1) (->Numeric 2))
                          (->Multiply (->Numeric 3) (->Numeric 4)))
                         {})))

    (is (= #cc.delboni.simple.small_step.Bool{:value false}
           (machine->run (->LessThan
                          (->Numeric 5)
                          (->Add (->Numeric 2) (->Numeric 2)))
                         {}))))

  (testing "machine->run should loop through all expression and reduce with environment"
    (is (= #cc.delboni.simple.small_step.Numeric{:value 7}
           (machine->run (->Add
                          (->Variable :x)
                          (->Variable :y))
                         {:x (->Numeric 3)
                          :y (->Numeric 4)})))))
