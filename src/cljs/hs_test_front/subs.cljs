(ns hs-test-front.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::patients
 (fn [db]
   (get-in db [:patients :data])))
