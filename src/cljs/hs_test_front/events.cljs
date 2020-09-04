(ns hs-test-front.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :refer [json-request-format json-response-format]]
    [hs-test-front.db :as db]))

(def url "http://localhost:3000")

(rf/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(rf/reg-event-fx
  ::get-patients
  (fn [{:keys [db]}]
    {:db         (assoc-in db [:patients :loading?] true)
     :http-xhrio {:method          :get
                  :uri             (str url "/patient")
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [::patients-loaded]}}))

(rf/reg-event-db
  ::patients-loaded
  (fn [db [_ res]]
    (-> db
        (assoc-in [:patients :data] res)
        (assoc-in [:patients :loading?] false))))

(rf/reg-event-fx
  ::edit-patient
  (fn [{:keys [db]} [_ patient]]
    {:db         (assoc-in db [:patients :editing?] true)
     :http-xhrio {:method          :put
                  :params          patient
                  :uri             (str url "/patient/" (:id patient))
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [::get-patients]}}))

(rf/reg-event-fx
  ::create-patient
  (fn [{:keys [db]} [_ patient]]
    {:db         (assoc-in db [:patients :creating?] true)
     :http-xhrio {:method          :post
                  :params          patient
                  :uri             (str url "/patient")
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [::get-patients]}}))

(rf/reg-event-fx
  ::delete-patient
  (fn [{:keys [db]} [_ patient-id]]
    {:http-xhrio {:method          :delete
                  :uri             (str url "/patient/" patient-id)
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [::get-patients]}}))
