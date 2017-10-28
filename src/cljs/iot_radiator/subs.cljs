(ns iot-radiator.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  :name
  (fn [db]
    (:name db)))

(re-frame/reg-sub
  :active-panel
  (fn [db _]
    (:active-panel db)))


(re-frame/reg-sub
  :device
  (fn [db params]
    ((keyword (last params)) db)))


(re-frame/reg-sub
  :loading?
  (fn [db _]
    (:loading? db)))
