(ns cc.delboni.simple.denotational-test
  (:require [cc.delboni.simple.denotational :refer [->Bool ->Numeric ->Variable
                                                    ->clj]]
            [clojure.test :refer [deftest is testing]]
            [matcher-combinators.test :refer [match?]]))

(defn ^:private invoke
  ([f] (invoke f {}))
  ([f e] (f e)))

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
