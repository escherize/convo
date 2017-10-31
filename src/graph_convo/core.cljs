(ns graph-convo.core
  (:require [rum.core :as rum]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(def debug? false)
(defonce app-state
  (atom
    {:path [:-1]
     :nodes
     {:-1 {:answers [:0 :3]
           :title ""
           :response "Want anything?"}
      :0 {:answers [:1 :2 :3]
          :title "Fruit, please"
          :response "Fruit sounds good, choose one:"}
      :1 {:answers [:4 :5 :6]
          :title "Banana"
          :response "Why do you like Bananas?"}
      :2 {:answers [:7 :8]
          :title "Cantelope"
          :response "Cantelopes are good, how ripe do you like them?"}
      :3 {:answers []
          :title "Ice Cream"
          :response "NO. Ice Cream is not healthy."}
      :4 {:answers []
          :title "They're yellow on the outside."
          :response "Actually they turn black when they're messed up."}
      :5 {:answers []
          :title "They're white on the inside."
          :response "No light can penetrate the inside so it's colorless."}
      :6 {:answers []
          :title "They're squishy."
          :response "They're only squishy when they're rotten."}
      :7 {:answers []
          :title "Soft for me."
          :response "Mmm, tasty and sweet and soft."}
      :8 {:answers []
          :title "Not too soft."
          :response "Oh.. I guess that's good. Nice and cantelopey."}}}))

(defonce print-path (add-watch app-state :ayo (fn [_ _ _ n] (js/console.log (:path n)))))

(defn title [key] (get-in @app-state [:nodes key :title]))
(defn response [key] (get-in @app-state [:nodes key :response]))
(defn answers [key] (get-in @app-state [:nodes key :answers]))

(defn add-node! [app-state choice]
  (swap! app-state update :path conj choice))

(defn go-back! [app-state]
  (swap! app-state update :path pop))

(rum/defc go-back < rum/reactive
  [app-state]
  (when (not= (count (:path (rum/react app-state))) 1)
    [:button {:on-click #(go-back! app-state)
              :style {:font-size "26px"}}
     "Go back"]))

(rum/defc display-question < rum/static [path current-node]
  (let [answers (answers current-node)]
    [:div {:style {:margin "30px"
                   :border-radius "15px"
                   :padding "20px"
                   :border (str "10px #eee solid")
                   :background-color (rand-nth ["#F0F0FF"
                                                "#F0FFF0"
                                                "#FFF0F0"
                                                "#FFFFF0"
                                                "#FFF0FF"])}}
     (map-indexed
       (fn [index node]
         [:div
          [:h3 (title node)]
          [:h3 (str "> ") (response node)]])
       path)
     (for [c answers]
       [:button {:style {:margin "10px"
                         :pointer :cursor
                         :font-size "26px"}
                 :on-click #(add-node! app-state c)}
        (title c)])]))

(rum/defc root < rum/reactive
  [app-state]
  (let [path (:path (rum/react app-state))
        current-node (last path)]
    [:div {:style {:font-family "monospace"}}
     [:h1 "CommuniGRAPH"]
     [:p [:em "(editing coming soon.)"]]
     (display-question path current-node)
     (go-back app-state)]))

(rum/mount (root app-state)
           (js/document.getElementById "app"))
