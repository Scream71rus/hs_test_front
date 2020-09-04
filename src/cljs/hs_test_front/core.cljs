(ns hs-test-front.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [hs-test-front.events :as events]
   [hs-test-front.views :as views]
   [hs-test-front.config :as config]
   [re-frisk.core :as re-frisk]
   [day8.re-frame.http-fx]))

(re-frisk/enable)

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
