(ns iot-radiator.events
  (:require [re-frame.core :as re-frame]
            [iot-radiator.db :as db]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [cljs-time.format :as f]))


(def custom-formatter (f/formatter "yyyy-MM-dd"))

(defn unparse [date]
  (f/unparse custom-formatter date))

(re-frame/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
  :data/service_failure
  (fn [db [_ _]]
    (assoc db :services [])))

(re-frame/reg-event-db
  :data/service_ok
  (fn [db [_ data]]
    (assoc db :services (:data data))))


(re-frame/reg-event-db
  :data/channel
  (fn [db [_ channel]]
    (assoc db :channel channel)))

(re-frame/reg-event-db
  :data/date
  (fn [db [_ date]]
    (assoc db :date date)))

(re-frame/reg-event-db
  :data/dev_events_failure
  (fn [db [params _]]
    db))

(defn get-id [event]
  (when event
    (:id event)
    )
  )

(re-frame/reg-event-db
  :data/dev_events_ok
  (fn [db [params data]]
    (let [deviceid (get-id (first data))]
      (assoc db (keyword deviceid) data))))



(defn get-device-url [id]
  (str "https://us-central1-saatanankoodarit-1476648106745.cloudfunctions.net/iotApi?id=" id "&sort=true&limit=500"
       ))


(re-frame/reg-event-fx
  :data/dev_events
  (fn
    [{db :db} params]
    (println ":data/dev_events" params)
    {:http-xhrio {:method          :get
                  :uri             (get-device-url (last params))
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:data/dev_events_ok]
                  :on-failure      [:data/dev_events_failure]}
     :db         (assoc db :loading? true)}))
