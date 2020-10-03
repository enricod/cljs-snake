(ns cljs-snake.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]))


;; -------------------------
;; state
;(defstruct Point :x :y)

(def settings {:dotSize 20
               :hiddenLayers 3
               :hiddenNodes 2})

(def app-state (r/atom {
                        :highScore 0
                        :mutationRate 0.05
                        :fps 100
                        :population []}))  ;; elenco di snakes


(defn create-snake [settings]
 {  :score 0
    :lifeLeft 200
    :xVel 0
    :yVel 0
    :dead false
    :head {:x 0 :y 0}
    :food {:x 0 :y 0}})


(defn create-population [n]
 {
   :snakes (map #(create-snake settings) (range n))

   :bestSnake 0
   :gen 0
   :bestSnakeScore 0
   :sameBest 0})

(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])

;; -------------------------
;; Views

(defn home-page []
   [:div
     [:h2 "Snake AI in clojurescript"]
     [simple-component]])


;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
