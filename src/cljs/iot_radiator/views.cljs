(ns iot-radiator.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [cljs-time.coerce :as c]
            [cljs-time.local :as l]
            [cljsjs.highcharts]
            [cljs-time.local :as l]
            [reagent.core :as reagent]))


;; home

(defn now [] (t/now))

(defn timeformat [time]
  (when time
    (f/parse time)
    )
  )

(defn to-long [date]
  (.getTime date))

(defn date-for-high [teksti]
  (to-long (timeformat teksti)))

(defn data-to-area [events]
  (map (fn [event] [(date-for-high (:created event)) (:temperature event)]) events)
  )

(defn posts-with-offsets [events]
  (reductions (fn [sum num]
                [(first num) (+ (last sum) (last num))]
                ) events))

(defn happen-to-area [events]
  (let [events (map (fn [event] [(date-for-high (:created event)) 1]) (reverse events))
        lates (posts-with-offsets events)
        ]
    lates

    ))

(defn chart-global []
  {:global {
            :useUTC         false
            :timezoneOffset 3
            }}
  )

(defn chart-config [data title dataFN]
  {
   :chart       {
                 :plotBackgroundColor nil
                 :plotBorderWidth     nil
                 :plotShadow          false
                 :zoomType            'x'
                 :type                "column"
                 }
   :xAxis       {
                 :type "datetime"
                 }
   :yAxis       {
                 :title {
                         :text title
                         }
                 },
   :title       {:text title}
   :plotOptions {
                 :area   {
                          :marker    {
                                      :radius    2
                                      :lineWidth 10
                                      :lineColor "#666666"
                                      },

                          :states    {
                                      :hover {
                                              :lineWidth 5
                                              }
                                      },
                          :threshold nil
                          :lineColor "#666666"
                          }
                 :column {
                          :pointPadding 0.2,
                          :borderWidth  0
                          }
                 }
   :credits     {:enabled false}
   :series      [{

                  :name         ""
                  :colorByPoint true
                  :data         (dataFN data)
                  }]

   })


(defn chart-inner []
  (let [update (fn [comp]
                 (let [props (-> comp reagent/props :data)
                       title (-> comp reagent/props :title)
                       dataFN (-> comp reagent/props :datafn)
                       options (clj->js (chart-config props title dataFN))
                       globals (clj->js (chart-global))
                       ]
                   (js/Highcharts.setOptions. globals)
                   (js/Highcharts.Chart. (reagent/dom-node comp) options)
                   ))
        ]

    (reagent/create-class
      {:reagent-render       (fn []
                               [:div])
       :component-did-mount  (fn [comp]
                               (update comp))
       :component-did-update update
       :display-name         (str "graafi-inner")})))


(defn graafi [data title dataFN]
  [chart-inner {:data data :title title :datafn dataFN}])




(defn sauna [id]
  (re-frame/dispatch [:data/dev_events id])
  (let [events (re-frame/subscribe [:device id])]
    (graafi @events "Saunapallun lämpö" data-to-area)
    )
  )

(defn bissenappi [id title]
  (re-frame/dispatch [:data/dev_events id])
  (let [events (re-frame/subscribe [:device id])]
    (graafi @events title happen-to-area)
    )
  )

(defn onkoVaiEi [value]
  (when value
    (if (< value 70)
      [:div [:h2 {:style {:color "red"}} "No ei ole"]
       [:img {:style {:width "200px"} :alt "kekkonen" :src "https://res.cloudinary.com/androidconsulting/image/upload/c_scale,w_200/v1509122060/kekkonen_tunarit_fkweby.png"}]
       ]
      [:div
       [:h2 {:style {:color "green"}} "On, ihanaa. Saunajallulle!!!!"]
       [:img {:style {:width "200px"} :alt "kekkonen" :src "https://res.cloudinary.com/androidconsulting/image/upload/c_scale,w_200/v1509122064/kekkonen_nauraa_udulqu.jpg"}]
       ]
      )
    ))



(defn saunalatest [id]
  (let [events (re-frame/subscribe [:device id])]
    [:div
     [:h1 "Onko saunassa lämmintä?"]
     (onkoVaiEi (:temperature (first @events)))]
    )
  )

(defn home-panel []
  [re-com/v-box :children [
                           [re-com/h-box :children [
                                                    [re-com/box :height "250px" :child (sauna "37A657")]
                                                    [re-com/box :child (saunalatest "37A657")]

                                                    ]]
                           [re-com/h-box :children [

                                                    [re-com/box :max-height "250px" :child (bissenappi "75B7D" "Bisse nappi 1")]
                                                    [re-com/box :child (bissenappi "75C8D" "Bisse nappi 2")]
                                                    ]]

                           [re-com/h-box :children [

                                                    [re-com/box :height "250px" :child (bissenappi "24BBB3" "Ketä liikuu saunas?")]
                                                    ]]
                           ]
   ]

  )


;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])




;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :align :center
       :children [[panels @active-panel]]])))
