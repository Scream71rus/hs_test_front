(ns hs-test-front.validation
  (:require
    [cljs.spec.alpha :as s]))

(defn validate [data scheme validate-map]
  (->> (s/explain-data scheme data)
       :cljs.spec.alpha/problems
       (map #(get-in % [:path 0]))
       (select-keys validate-map)))

(s/def ::first_name (s/and string? #(< 0 (count %))))
(s/def ::last_name (s/and string? #(< 0 (count %))))
(s/def ::gender (s/and string? (partial re-matches #"^(MALE|FEMALE)$")))
(s/def ::birthday (s/and string? (partial re-matches #"^(20|19)?\d{2}-\d{2}-\d{2}$")))
(s/def ::medical_insurance (s/and string? #(< 0 (count %))))
(s/def ::middle_name string?)
(s/def ::address string?)

(s/def ::create-validation-schema (s/keys :req-un [::first_name ::last_name ::gender
                                                   ::birthday ::medical_insurance]
                                          :opt-un [::address ::middle_name]))
(def create-validate-map {:first_name        "first-name is not valid"
                          :last_name         "last-name is not valid"
                          :gender            "gender is not valid"
                          :birthday          "birthday is not valid"
                          :medical_insurance "medical-insurance is not valid"})

(s/def ::update-validation-schema (s/keys :req-un [::first_name ::last_name ::gender
                                                   ::birthday ::medical_insurance]
                                          :opt-un [::address ::middle_name]))
(def update-validate-map {:first_name        "first-name is not valid"
                          :last_name         "last-name is not valid"
                          :gender            "gender is not valid"
                          :birthday          "birthday is not valid"
                          :medical_insurance "medical-insurance is not valid"})

(defn editing [data]
  (validate data ::update-validation-schema update-validate-map))

(defn creating [data]
  (validate data ::create-validation-schema create-validate-map))
