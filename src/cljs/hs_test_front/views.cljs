(ns hs-test-front.views
  (:require
    [re-frame.core :as rf]
    [hs-test-front.subs :as subs]
    [hs-test-front.events :as events]
    [hs-test-front.validation :as validation]
    [reagent.core :as r]
    [clojure.string :as str]
    [cljs-time.format :refer [formatters formatter parse unparse]]
    ))

(defn format-date [d]
  (unparse (:date formatters) (parse (:date-time-no-ms formatters) d)))

(defn input [pat [key-name title select?] errors]
  [:div
   [:p {:style {:margin      "0px"
                :padding-top "1em"}} title]
   (if select?
     [:select.form-control {:style     {:width "90%"}
                            :on-change #(swap! pat assoc key-name (-> % .-target .-value))}
      [:option {:value ""} ""]
      [:option {:value "MALE" :selected (= (key-name @pat) "MALE")} "MALE"]
      [:option {:value "FEMALE" :selected (= (key-name @pat) "FEMALE")} "FEMALE"]]

     [:input.form-control {:name        (name key-name)
                           :placeholder (key-name @pat)
                           :value       (key-name @pat)
                           :on-change   #(swap! pat assoc key-name (-> % .-target .-value))
                           :style       {:width "90%"}}])

   (if-not (empty? (get @errors key-name))
     [:p {:style {:color "red"}} (get @errors key-name)])])

(defn modal [pat show?]
  (let [errors (r/atom {})
        close #(do (reset! show? false) (reset! errors {}))]
    (fn [pat show?]
      (let [patient @pat]
        [:div.modal.fade.show
         {:tabindex "-1"
          :style    (if @show? {:display "block"})}
         [:div.modal-dialog
          [:div.modal-content
           [:div.modal-header
            [:h5.modal-title (:first_name patient) " " (:last_name patient)]
            [:button.close
             {:aria-label "Close" :data-dismiss "modal" :type "button" :on-click close}
             [:span {:aria-hidden "true"} "×"]]]
           [:div.modal-body
            [:form {:on-submit (fn [event]
                                 (.preventDefault event)
                                 (js/console.log patient)
                                 (reset! errors (if (:id patient) (validation/editing patient) (validation/creating patient)))
                                 (if (empty? @errors)
                                   (do
                                     (if (:id patient) (rf/dispatch [::events/edit-patient patient]) (rf/dispatch [::events/create-patient patient]))
                                     (close))
                                   (js/console.log @errors))
                                 )}

             (map (fn [el] [input pat el errors]) [[:first_name "First name"] [:middle_name "Middle name"] [:last_name "Last name"]
                                                   [:birthday "Birthday"] [:address "Address"] [:medical_insurance "Medical_insurance"]
                                                   [:gender "Gender" true]])

             [:div.modal-footer
              [:button.btn.btn-danger
               {:data-dismiss "modal", :type "button" :on-click close}
               "Close"]
              [:button.btn.btn-dark {:type "submit"} "Save changes"]]

             ]]]]]))))

(defn main-panel []
  (rf/dispatch [::events/get-patients])
  (let [show-modal? (r/atom false)
        editable-patient (r/atom nil)]
    (fn []
      (let [patients @(rf/subscribe [::subs/patients])]
        [:div {:style {:width "90%" :margin-right "auto" :margin-left "auto"}}
         [:div
          [:button.btn.btn-dark {:on-click (fn []
                                             (reset! show-modal? true)
                                             (reset! editable-patient {:first_name "" :middle_name "" :last_name "" :birthday ""
                                                                       :address    "" :medical_insurance "" :gender ""}))
                                 :style    {:margin-top    "1%"
                                            :margin-bottom "1%"}} "Create"]]

         [modal editable-patient show-modal?]
         [:div
          [:table.table.table-striped
           [:thead.thead-dark
            [:tr
             [:th "id"]
             [:th "first_name"]
             [:th "middle_name"]
             [:th "last_name"]
             [:th "birthday"]
             [:th "created"]
             [:th "address"]
             [:th "medical_insurance"]
             [:th "gender"]
             [:th "actions"]
             ]]
           [:tbody
            (->> patients
                 (map-indexed (fn [index patient]
                                ^{:key index}
                                [:tr
                                 [:td (:id patient)]
                                 [:td (:first_name patient)]
                                 [:td (:middle_name patient)]
                                 [:td (:last_name patient)]
                                 [:td (format-date (:birthday patient))]
                                 [:td (format-date (:created patient))]
                                 [:td (:address patient)]
                                 [:td (:medical_insurance patient)]
                                 [:td (:gender patient)]
                                 [:td
                                  [:button.btn.btn-dark {:on-click (fn []
                                                                     (reset! show-modal? true)
                                                                     (reset! editable-patient (update patient :birthday format-date)))} "Edit"]
                                  [:button.btn.btn-dark {:on-click #(rf/dispatch [::events/delete-patient (:id patient)])
                                                         :style    {:margin-left "5%"}} "Delete"]]]))
                 doall)]]]]))
    ))
