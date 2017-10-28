(ns iot-radiator.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [iot-radiator.events]
            [iot-radiator.subs]
            [iot-radiator.routes :as routes]
            [iot-radiator.views :as views]
            [iot-radiator.config :as config]))


(defn dev-setup []
  (enable-console-print!)
  (when config/debug?
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
