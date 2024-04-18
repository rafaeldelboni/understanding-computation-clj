(ns cc.delboni.simple.small-step-test
  (:require [cc.delboni.simple.small-step :refer [->Add ->Assign ->Bool ->DoNothing
                                                  ->If ->LessThan ->Multiply
                                                  ->Numeric ->Variable
                                                  ->Sequence -reduce
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

    (is (= "true"
           (str (->Bool true))))

    (is (= "1 < 2"
           (str (->LessThan (->Numeric 1) (->Numeric 2)))))

    (is (= "do-nothing"
           (-> (->DoNothing)
               str)))

    (is (= "x = x + 1"
           (-> (->Assign :x (->Add (->Variable :x) (->Numeric 1)))
               str)))

    (is (= "if (x) { y = 1 } else { y = 2 }"
           (-> (->If (->Variable :x)
                     (->Assign :y (->Numeric 1))
                     (->Assign :y (->Numeric 2)))
               str)))))

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
                         (->Multiply (->Numeric 3) (->Numeric 4))))))

    (is (= false
           (-reducible? (->Bool true))))

    (is (= false
           (-reducible? (->DoNothing))))

    (is (= true
           (-reducible? (->Assign :x (->Numeric 5)))))))

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
               str)))

    (is (= {:x #cc.delboni.simple.small_step.Bool{:value true}
            :y #cc.delboni.simple.small_step.Numeric{:value 1}}
           (-> (->If (->Variable :x)
                     (->Assign :y (->Numeric 1))
                     (->Assign :y (->Numeric 2)))
               (-reduce {:x (->Bool true)})
               (as-> se (apply -reduce se))
               (as-> se (apply -reduce se))
               last)))))

(deftest machine-test
  (testing "machine->run should loop through all expression and reduce without environment"
    (is (= 14
           (-> (machine->run
                (->Assign :result (->Add
                                   (->Multiply (->Numeric 1) (->Numeric 2))
                                   (->Multiply (->Numeric 3) (->Numeric 4))))
                {})
               last
               :result
               :value)))

    (is (= false
           (-> (machine->run
                (->Assign :result (->LessThan
                                   (->Numeric 5)
                                   (->Add (->Numeric 2) (->Numeric 2))))
                {})
               last
               :result
               :value))))

  (testing "machine->run should loop through all expression and reduce with environment"
    (is (= [#cc.delboni.simple.small_step.DoNothing{}
            {:x #cc.delboni.simple.small_step.Numeric{:value 3}
             :y #cc.delboni.simple.small_step.Numeric{:value 4}
             :result #cc.delboni.simple.small_step.Numeric{:value 7}}]
           (machine->run (->Assign :result (->Add
                                            (->Variable :x)
                                            (->Variable :y)))
                         {:x (->Numeric 3)
                          :y (->Numeric 4)}))))

  (testing "Assign new value to variable"
    (is (= [#cc.delboni.simple.small_step.DoNothing{}
            {:x #cc.delboni.simple.small_step.Numeric{:value 3}}]
           (machine->run (->Assign :x (->Add (->Variable :x) (->Numeric 1)))
                         {:x (->Numeric 2)}))))

  (testing "If condition check"
    (is (= [#cc.delboni.simple.small_step.DoNothing{}
            {:x #cc.delboni.simple.small_step.Bool{:value true}
             :y #cc.delboni.simple.small_step.Numeric{:value 1}}]
           (machine->run (->If (->Variable :x)
                               (->Assign :y (->Numeric 1))
                               (->Assign :y (->Numeric 2)))
                         {:x (->Bool true)})))

    (is (= [#cc.delboni.simple.small_step.DoNothing{}
            {:x #cc.delboni.simple.small_step.Bool{:value false}
             :y #cc.delboni.simple.small_step.Numeric{:value 2}}]
           (machine->run (->If (->Variable :x)
                               (->Assign :y (->Numeric 1))
                               (->Assign :y (->Numeric 2)))
                         {:x (->Bool false)})))

    (is (= [#cc.delboni.simple.small_step.DoNothing{}
            {:x #cc.delboni.simple.small_step.Bool{:value false}}]
           (machine->run (->If (->Variable :x)
                               (->Assign :y (->Numeric 1))
                               (->DoNothing))
                         {:x (->Bool false)}))))

  (testing "Checks Sequence statement"
    (is (= [#cc.delboni.simple.small_step.DoNothing{}
            {:x #cc.delboni.simple.small_step.Numeric{:value 2}
             :y #cc.delboni.simple.small_step.Numeric{:value 5}}]
           (machine->run (->Sequence
                          (->Assign :x (->Add (->Numeric 1) (->Numeric 1)))
                          (->Assign :y (->Add (->Variable :x) (->Numeric 3))))
                         {})))))
